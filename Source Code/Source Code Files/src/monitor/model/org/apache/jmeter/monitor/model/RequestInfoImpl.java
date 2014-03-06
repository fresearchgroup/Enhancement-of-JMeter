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
 */
package org.apache.jmeter.monitor.model;

/**
 *
 * @version $Revision: 1413262 $
 */
public class RequestInfoImpl implements RequestInfo {
    private long bytesReceived = 0;

    private long bytesSent = 0;

    private long requestCount = 0;

    private long errorCount = 0;

    private int maxTime = 0;

    private int processingTime = 0;

    /**
     *
     */
    public RequestInfoImpl() {
        super();
    }

    @Override
    public long getBytesReceived() {
        return this.bytesReceived;
    }

    @Override
    public void setBytesReceived(long value) {
        this.bytesReceived = value;
    }

    @Override
    public long getBytesSent() {
        return this.bytesSent;
    }

    @Override
    public void setBytesSent(long value) {
        this.bytesSent = value;
    }

    @Override
    public long getRequestCount() {
        return requestCount;
    }

    @Override
    public void setRequestCount(long value) {
        this.requestCount = value;
    }

    @Override
    public long getErrorCount() {
        return this.errorCount;
    }

    @Override
    public void setErrorCount(long value) {
        this.errorCount = value;
    }

    @Override
    public int getMaxTime() {
        return this.maxTime;
    }

    @Override
    public void setMaxTime(int value) {
        this.maxTime = value;
    }

    @Override
    public int getProcessingTime() {
        return this.processingTime;
    }

    @Override
    public void setProcessingTime(int value) {
        this.processingTime = value;
    }

}
