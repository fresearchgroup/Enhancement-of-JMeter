

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


import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.jmeter.engine.util.CompoundVariable;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.samplers.Sampler;
import org.apache.jmeter.threads.JMeterVariables;
import org.apache.jmeter.util.JMeterUtils;

/**
 * Get the current Timestamp for TPC-C procedure parameters
 * @since 2.9
 */
public class getCurrentTimetpcc extends AbstractFunction {
	
	/**
	 * @author Surabhi
	 */

    private static final List<String> desc = new LinkedList<String>();
    
    private static final RandomGenerator ran = new RandomGenerator(0);
    
    public final static SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");


    private static final String KEY = "__getCurrentTime_tpcc"; //$NON-NLS-1$

    static {
        desc.add("variable");//$NON-NLS-1$ 
    }

    private CompoundVariable varName, minimum, maximum, char_base,num_char;

    /**
     * No-arg constructor.
     */
    public getCurrentTimetpcc() {
    }

    /** {@inheritDoc} */
    @Override
    public synchronized String execute(SampleResult previousResult, Sampler currentSampler)
            throws InvalidVariableException {



       String randString;
      randString= dateFormat.format(new java.util.Date());  
      
        if (varName != null) {
            JMeterVariables vars = getVariables();
            final String varTrim = varName.execute().trim();
            if (vars != null && varTrim.length() > 0){// vars will be null on TestPlan
                vars.put(varTrim, randString);
            }
        }

        return randString;

    }

    /** {@inheritDoc} */
    @Override
    public synchronized void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
        //checkParameterCount(parameters, 1, 2);
        Object[] values = parameters.toArray();

        if (values.length>0){
            varName = (CompoundVariable) values[0];
        } else {
            varName = null;
        }

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
