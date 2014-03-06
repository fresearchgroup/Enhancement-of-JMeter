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

import java.util.Random;


/**
 * Provides a Random function which returns a random long integer between a min
 * (first argument) and a max (second argument).
 * @since 1.9
 */
public class nonUniformRandom extends AbstractFunction {

    private static final List<String> desc = new LinkedList<String>();
    
    private static final RandomGenerator ran = new RandomGenerator(0);

    private static final java.util.Random r = new java.util.Random();
    
    private static final int OL_I_ID_C = 7911; // in range [0, 8191]
	private static final int C_ID_C = 259; // in range [0, 1023]
	// NOTE: TPC-C 2.1.6.1 specifies that abs(C_LAST_LOAD_C - C_LAST_RUN_C) must
	// be within [65, 119]
	private static final int C_LAST_LOAD_C = 157; // in range [0, 255]
	private static final int C_LAST_RUN_C = 223; // in range [0, 255]
	
    private static final String KEY = "__nonuniformRandom"; //$NON-NLS-1$

    static {
        desc.add("int A"); //$NON-NLS-1$
        desc.add("int C"); //$NON-NLS-1$
        desc.add("int min");//$NON-NLS-1$
        desc.add("int max");//$NON-NLS-1$
        desc.add("variable");//$NON-NLS-1$ 
    }

    private CompoundVariable varName, minimum, maximum,int_a,int_c;

    /**
     * No-arg constructor.
     */
    public nonUniformRandom() {
    }

    /** {@inheritDoc} */
    @Override
    public synchronized String execute(SampleResult previousResult, Sampler currentSampler)
            throws InvalidVariableException {


       int min = Integer.parseInt(minimum.execute().trim());
        int max = Integer.parseInt(maximum.execute().trim());
        int ic = Integer.parseInt(int_c.execute().trim());
        int ia = Integer.parseInt(int_a.execute().trim());

       
      //  int strLen=min;
        int ran2=(((randomNumber(0, ia, r) | randomNumber(min, max, r)) + ic) % (max
				- min + 1))
				+ min;
//        long rand = min + (long) (Math.random() * (max - min + 1));
        String randString = Integer.toString(ran2);
      // String randString;
        
       // if (strLen > 1) 
	     //   randString=ran.astring(strLen - 1, strLen - 1);
	  //  else
	    //    randString="";

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
        checkParameterCount(parameters, 4, 5);
        Object[] values = parameters.toArray();

        int_a = (CompoundVariable) values[0];
        int_c = (CompoundVariable) values[1];
        
        minimum = (CompoundVariable) values[2];
        maximum = (CompoundVariable) values[3];
        
        if (values.length>4){
            varName = (CompoundVariable) values[4];
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
    
   
    
    public static int randomNumber(int min, int max, java.util.Random r) {
		return (int) (r.nextDouble() * (max - min + 1) + min);
	}

}
