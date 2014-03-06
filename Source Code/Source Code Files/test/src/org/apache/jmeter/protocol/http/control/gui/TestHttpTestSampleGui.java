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

package org.apache.jmeter.protocol.http.control.gui;

import junit.framework.TestCase;

import org.apache.jmeter.protocol.http.sampler.HTTPSamplerBase;

public class TestHttpTestSampleGui extends TestCase {
        private HttpTestSampleGui gui;

        public TestHttpTestSampleGui(String name) {
            super(name);
        }

        @Override
        public void setUp() {
            gui = new HttpTestSampleGui();
        }

        public void testCloneSampler() throws Exception {
            HTTPSamplerBase sampler = (HTTPSamplerBase) gui.createTestElement();
            sampler.addArgument("param", "value");
            HTTPSamplerBase clonedSampler = (HTTPSamplerBase) sampler.clone();
            clonedSampler.setRunningVersion(true);
            sampler.getArguments().getArgument(0).setValue("new value");
            assertEquals("Sampler didn't clone correctly", "new value", sampler.getArguments().getArgument(0)
                    .getValue());
        }
}
