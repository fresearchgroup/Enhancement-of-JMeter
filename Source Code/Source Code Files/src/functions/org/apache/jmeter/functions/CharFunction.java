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

/**
 * Function to generate chars from a list of decimal or hex values
 * @since 2.3.3
 */
public class CharFunction extends AbstractFunction {

    private static final Logger log = LoggingManager.getLoggerForClass();

    private static final List<String> desc = new LinkedList<String>();

    private static final String KEY = "__char"; //$NON-NLS-1$

    static {
        desc.add(JMeterUtils.getResString("char_value")); //$NON-NLS-1$
    }

    private Object[] values;

    public CharFunction() {
    }

    /** {@inheritDoc} */
    @Override
    public synchronized String execute(SampleResult previousResult, Sampler currentSampler)
            throws InvalidVariableException {

        StringBuilder sb = new StringBuilder(values.length);
        for (int i=0; i < values.length; i++){
            String numberString = ((CompoundVariable) values[i]).execute().trim();
            try {
                long value=Long.decode(numberString).longValue();
                char ch = (char) value;
                sb.append(ch);
            } catch (NumberFormatException e){
                log.warn("Could not parse "+numberString+" : "+e);
            }
        }
        return sb.toString();

    }

    /** {@inheritDoc} */
    @Override
    public synchronized void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
        checkMinParameterCount(parameters, 1);
        values = parameters.toArray();
    }

    /** {@inheritDoc} */
    @Override
    public String getReferenceKey() {
        return KEY;
    }

    /** {@inheritDoc} */
    @Override
    public List<String> getArgumentDesc() {
        return desc;
    }
}
