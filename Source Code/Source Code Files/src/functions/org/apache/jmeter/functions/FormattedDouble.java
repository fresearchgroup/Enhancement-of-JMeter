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
import org.apache.jmeter.threads.JMeterVariables;
import org.apache.jmeter.util.JMeterUtils;

/**
 * Provides a Random function which returns a random long integer between a min
 * (first argument) and a max (second argument).
 * @since 1.9
 */
public class FormattedDouble extends AbstractFunction {

    private static final List<String> desc = new LinkedList<String>();
    
    private static final RandomGenerator ran = new RandomGenerator(0);


    private static final String KEY = "__formattedDouble"; //$NON-NLS-1$

    static {
        desc.add("min_value"); //$NON-NLS-1$
       // desc.add("max_value"); //$NON-NLS-1$
       // desc.add("char_base");//$NON-NLS-1$
       // desc.add("num_char");//$NON-NLS-1$
        desc.add("variable");//$NON-NLS-1$ 
    }

    private CompoundVariable varName, minimum, maximum, char_base,num_char;

    /**
     * No-arg constructor.
     */
    public FormattedDouble() {
    }

    /** {@inheritDoc} */
    @Override
    public synchronized String execute(SampleResult previousResult, Sampler currentSampler)
            throws InvalidVariableException {


        double min =Double.parseDouble(minimum.execute().trim());
      //  int max = Integer.parseInt(maximum.execute().trim());
     //   char c = (char_base.execute().trim()).charAt(0);
      //  int nc = Integer.parseInt(num_char.execute().trim());

       
       // int strLen=min;
//        long rand = min + (long) (Math.random() * (max - min + 1));

       String randString;
        
       String dS = "" + min;
		randString= dS.length() > 6 ? dS.substring(0, 6) : dS; 
       
       
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
        checkParameterCount(parameters, 1, 2);
        Object[] values = parameters.toArray();

        minimum = (CompoundVariable) values[0];
      //  maximum = (CompoundVariable) values[1];
        if (values.length>1){
            varName = (CompoundVariable) values[1];
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
