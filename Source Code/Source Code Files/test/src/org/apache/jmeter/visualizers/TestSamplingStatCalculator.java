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

package org.apache.jmeter.visualizers;

import org.apache.jmeter.samplers.SampleResult;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestSamplingStatCalculator {

    private SamplingStatCalculator ssc;
    @Before
    public void setUp(){
        ssc = new SamplingStatCalculator("JUnit");
    }

    @Test
    public void testGetCurrentSample() {
        Assert.assertNotNull(ssc.getCurrentSample()); // probably needed to avoid NPEs with GUIs
    }

//    @Test
//    public void testGetElapsed() {
//        fail("Not yet implemented");
//    }
//
//    @Test
//    public void testGetRate() {
//        fail("Not yet implemented");
//    }
//
//    @Test
//    public void testGetBytesPerSecond() {
//        fail("Not yet implemented");
//    }
//
//    @Test
//    public void testGetKBPerSecond() {
//        fail("Not yet implemented");
//    }

    @Test
    public void testGetAvgPageBytes() {
        SampleResult res = new SampleResult();
        Assert.assertEquals(0,ssc.getAvgPageBytes(),0);
        res.setResponseData("abcdef", "UTF-8");
        ssc.addSample(res);
        res.setResponseData("abcde", "UTF-8");
        ssc.addSample(res);
        res.setResponseData("abcd", "UTF-8");
        ssc.addSample(res);
        Assert.assertEquals(5,ssc.getAvgPageBytes(),0);
    }

//    @Test
//    public void testGetLabel() {
//        fail("Not yet implemented");
//    }
//
//    @Test
//    public void testAddSample() {
//        fail("Not yet implemented");
//    }
//
//    @Test
//    public void testGetErrorPercentage() {
//        fail("Not yet implemented");
//    }
//
//    @Test
//    public void testToString() {
//        fail("Not yet implemented");
//    }
//
//    @Test
//    public void testGetErrorCount() {
//        fail("Not yet implemented");
//    }
//
//    @Test
//    public void testGetMaxThroughput() {
//        fail("Not yet implemented");
//    }
//
//    @Test
//    public void testGetDistribution() {
//        fail("Not yet implemented");
//    }
//
//    @Test
//    public void testGetPercentPointDouble() {
//        fail("Not yet implemented");
//    }
//
//    @Test
//    public void testGetCount() {
//        fail("Not yet implemented");
//    }
//
//    @Test
//    public void testGetMax() {
//        fail("Not yet implemented");
//    }
//
//    @Test
//    public void testGetMean() {
//        fail("Not yet implemented");
//    }
//
//    @Test
//    public void testGetMeanAsNumber() {
//        fail("Not yet implemented");
//    }
//
//    @Test
//    public void testGetMedian() {
//        fail("Not yet implemented");
//    }
//
//    @Test
//    public void testGetMin() {
//        fail("Not yet implemented");
//    }
//
//    @Test
//    public void testGetPercentPointFloat() {
//        fail("Not yet implemented");
//    }
//
//    @Test
//    public void testGetStandardDeviation() {
//        fail("Not yet implemented");
//    }

}
