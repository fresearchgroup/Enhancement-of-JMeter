/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.jmeter.samplers;

import org.apache.log.Logger;
import org.apache.commons.io.IOUtils;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.jorphan.util.JMeterError;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.OutputStream;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Version of HoldSampleSender that stores the samples on disk as a serialised stream.
 */

public class DiskStoreSampleSender extends AbstractSampleSender implements Serializable {
    private static final Logger log = LoggingManager.getLoggerForClass();

    private static final long serialVersionUID = 252L;

    private final RemoteSampleListener listener;

    private transient volatile ObjectOutputStream oos;
    private transient volatile File temporaryFile;
    private transient volatile ExecutorService singleExecutor;

    /**
     * @deprecated only for use by test code
     */
    @Deprecated
    public DiskStoreSampleSender(){
        log.warn("Constructor only intended for use in testing"); // $NON-NLS-1$
        listener = null;
    }

    DiskStoreSampleSender(RemoteSampleListener listener) {
        this.listener = listener;
        log.info("Using DiskStoreSampleSender for this test run"); // client log file
    }

    @Override
    public void testEnded(String host) {
        log.info("Test Ended on " + host);
        singleExecutor.submit(new Runnable(){
            @Override
            public void run() {
                try {
                    oos.close(); // ensure output is flushed
                } catch (IOException e) {
                    log.error("Failed to close data file ", e);
                }                
            }});
        singleExecutor.shutdown(); // finish processing samples
        try {
            if (!singleExecutor.awaitTermination(3, TimeUnit.SECONDS)) {
                log.error("Executor did not terminate in a timely fashion");
            }
        } catch (InterruptedException e1) {
            log.error("Executor did not terminate in a timely fashion", e1);
        }
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(new FileInputStream(temporaryFile));
            Object obj = null;
            while((obj = ois.readObject()) != null) {
                if (obj instanceof SampleEvent) {
                    try {
                        listener.sampleOccurred((SampleEvent) obj);
                    } catch (RemoteException err) {
                        if (err.getCause() instanceof java.net.ConnectException){
                            throw new JMeterError("Could not return sample",err);
                        }
                        log.error("returning sample", err);
                    }
                } else {
                    log.error("Unexpected object type found in data file "+obj.getClass().getName());
                }
            }                    
        } catch (EOFException err) {
            // expected
        } catch (IOException err) {
            log.error("returning sample", err);
        } catch (ClassNotFoundException err) {
            log.error("returning sample", err);
        } finally {
            try {
                listener.testEnded(host);
            } catch (RemoteException e) {
                log.error("returning sample", e);
            }
            IOUtils.closeQuietly(ois);
            if(!temporaryFile.delete()) {
            	log.warn("Could not delete file:"+temporaryFile.getAbsolutePath());
            }
        }
    }

    @Override
    public void sampleOccurred(final SampleEvent e) {
        // sampleOccurred is called from multiple threads; not safe to write from multiple threads.
        // also decouples the file IO from sample generation
        singleExecutor.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        oos.writeObject(e);
                    } catch (IOException err) {
                        log.error("sampleOccurred", err);
                    }                
                }
            
            }
        );
    }

    /**
     * Processed by the RMI server code; acts as testStarted().
     * @throws ObjectStreamException  
     */
    // TODO should errors be thrown back through RMI?
    private Object readResolve() throws ObjectStreamException{
        log.info("Using DiskStoreSampleSender for this test run"); // server log file
        singleExecutor = Executors.newSingleThreadExecutor();
        try {
            temporaryFile = File.createTempFile("SerialisedSampleSender", ".ser");
            temporaryFile.deleteOnExit();
            singleExecutor.submit(new Runnable(){
                @Override
                public void run() {
                    OutputStream anOutputStream;
                    try {
                        anOutputStream = new FileOutputStream(temporaryFile);
                        oos = new ObjectOutputStream(anOutputStream);
                    } catch (IOException e) {
                        log.error("Failed to create output Stream", e);
                    }
                }});
        } catch (IOException e) {
            log.error("Failed to create output file", e);
        }
        return this;
    }
}
