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

package org.apache.jmeter.protocol.http.util;

import java.security.GeneralSecurityException;

import org.apache.jmeter.util.HttpSSLProtocolSocketFactory;
import org.apache.jmeter.util.JsseSSLManager;

/**
 * Apache HttpClient protocol factory to generate "slow" SSL sockets for emulating dial-up modems
 */

public class SlowHC4SSLSocketFactory extends HC4TrustAllSSLSocketFactory {

    /**
     * Create a factory 
     * @param cps - characters per second, must be &gt; 0
     * @throws GeneralSecurityException if there's a problem setting up the security
     * @throws IllegalArgumentException if cps &le; 0
     */
    public SlowHC4SSLSocketFactory(final int cps) throws GeneralSecurityException {
        super(new HttpSSLProtocolSocketFactory((JsseSSLManager)JsseSSLManager.getInstance(), cps));
    }
}
