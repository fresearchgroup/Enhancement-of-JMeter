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

package org.apache.jmeter.reporters;

import java.io.Serializable;

import org.apache.jmeter.samplers.SampleEvent;
import org.apache.jmeter.samplers.SampleListener;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testelement.OnErrorTestElement;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

/**
 * ResultAction - take action based on the status of the last Result
 *
 */
public class ResultAction extends OnErrorTestElement implements Serializable, SampleListener {

    private static final long serialVersionUID = 240L;

    private static final Logger log = LoggingManager.getLoggerForClass();

    /*
     * Constructor is initially called once for each occurrence in the test plan
     * For GUI, several more instances are created Then clear is called at start
     * of test Called several times during test startup The name will not
     * necessarily have been set at this point.
     */
    public ResultAction() {
        super();
        // log.debug(Thread.currentThread().getName());
        // System.out.println(">> "+me+" "+this.getName()+"
        // "+Thread.currentThread().getName());
    }

    /**
     * Examine the sample(s) and take appropriate action
     *
     * @see org.apache.jmeter.samplers.SampleListener#sampleOccurred(org.apache.jmeter.samplers.SampleEvent)
     */
    @Override
    public void sampleOccurred(SampleEvent e) {
        SampleResult s = e.getResult();
        log.debug(s.getSampleLabel() + " OK? " + s.isSuccessful());
        if (!s.isSuccessful()) {
            if (isStopTestNow()) {
                s.setStopTestNow(true);
            }
            if (isStopTest()) {
                s.setStopTest(true);
            }
            if (isStopThread()) {
                s.setStopThread(true);
            }
            if (isStartNextThreadLoop()) {
               s.setStartNextThreadLoop(true);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sampleStarted(SampleEvent e) {
        // not used
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sampleStopped(SampleEvent e) {
        // not used
    }

}
