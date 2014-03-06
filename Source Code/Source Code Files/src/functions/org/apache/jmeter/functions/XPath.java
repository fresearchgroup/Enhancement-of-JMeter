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

package org.apache.jmeter.functions;


import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.jmeter.engine.util.CompoundVariable;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.samplers.Sampler;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

// @see org.apache.jmeter.functions.PackageTest for unit tests

/**
 * The function represented by this class allows data to be read from XML files.
 * Syntax is similar to the CVSRead function. The function allows the test to
 * line-thru the nodes in the XML file - one node per each test. E.g. inserting
 * the following in the test scripts :
 *
 * ${_XPath(c:/BOF/abcd.xml,/xpath/)} // match the (first) node
 * ${_XPath(c:/BOF/abcd.xml,/xpath/)} // Go to next match of '/xpath/' expression
 *
 * NOTE: A single instance of each different file/expression combination
 * is opened and used for all threads.
 * @since 2.0.3
 */
public class XPath extends AbstractFunction {
    private static final Logger log = LoggingManager.getLoggerForClass();

    // static {
    // LoggingManager.setPriority("DEBUG","jmeter");
    // LoggingManager.setTarget(new java.io.PrintWriter(System.out));
    // }
    private static final String KEY = "__XPath"; // Function name //$NON-NLS-1$

    private static final List<String> desc = new LinkedList<String>();

    private Object[] values; // Parameter list

    static {
        desc.add(JMeterUtils.getResString("xpath_file_file_name")); //$NON-NLS-1$
        desc.add(JMeterUtils.getResString("xpath_expression")); //$NON-NLS-1$
    }

    public XPath() {
    }

    /** {@inheritDoc} */
    @Override
    public synchronized String execute(SampleResult previousResult, Sampler currentSampler)
            throws InvalidVariableException {
        String myValue = ""; //$NON-NLS-1$

        String fileName = ((CompoundVariable) values[0]).execute();
        String xpathString = ((CompoundVariable) values[1]).execute();

        if (log.isDebugEnabled()){
            log.debug("execute (" + fileName + " " + xpathString + ")   ");
        }

        myValue = XPathWrapper.getXPathString(fileName, xpathString);

        if (log.isDebugEnabled()){
            log.debug("execute value: " + myValue);
        }

        return myValue;
    }

    /** {@inheritDoc} */
    @Override
    public List<String> getArgumentDesc() {
        return desc;
    }

    /** {@inheritDoc} */
    @Override
    public String getReferenceKey() {
        return KEY;
    }

    /** {@inheritDoc} */
    @Override
    public synchronized void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
        log.debug("setParameter - Collection.size=" + parameters.size());

        values = parameters.toArray();

        if (log.isDebugEnabled()) {
            for (int i = 0; i < parameters.size(); i++) {
                log.debug("i:" + ((CompoundVariable) values[i]).execute());
            }
        }

        checkParameterCount(parameters, 2);

        /*
         * Need to reset the containers for repeated runs; about the only way
         * for functions to detect that a run is starting seems to be the
         * setParameters() call.
         */
        XPathWrapper.clearAll();// TODO only clear the relevant entry - if possible...

    }
}