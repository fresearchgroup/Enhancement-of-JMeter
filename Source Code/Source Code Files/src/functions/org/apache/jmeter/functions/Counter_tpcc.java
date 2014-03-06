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

import org.apache.jmeter.control.PassingVar;
import org.apache.jmeter.engine.util.CompoundVariable;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.samplers.Sampler;
import org.apache.jmeter.threads.JMeterVariables;
import org.apache.jmeter.util.JMeterUtils;

/**
 * TPC-C counter for the thread currently under execution.
 * @since 2.9
 */
public class Counter_tpcc extends AbstractFunction {

	/**
	 * @author Surabhi
	 */
    private static final List<String> desc = new LinkedList<String>();

    private static final String KEY = "__countertpcc"; //$NON-NLS-1$

    private ThreadLocal<Integer> perThreadInt;
    public static ThreadLocal<Integer> perThreadInt1;
    public static ThreadLocal<Integer> perThreadInt2;
    public static ThreadLocal<Integer> perThreadInt3;

    private Object[] variables;

    private int globalCounter;//MAXINT = 2,147,483,647

    private void init(){
       synchronized(this){
           globalCounter=-1;
       }
       perThreadInt = new ThreadLocal<Integer>(){
            @Override
            protected Integer initialValue() {
                return Integer.valueOf(0);
            }
        };
        
        perThreadInt3 = new ThreadLocal<Integer>(){
            @Override
            protected Integer initialValue() {
                return Integer.valueOf(0);
            }
        };
        
        perThreadInt2 = new ThreadLocal<Integer>(){
            @Override
            protected Integer initialValue() {
                return Integer.valueOf(0);
            }
        };
        perThreadInt1 = new ThreadLocal<Integer>(){
            @Override
            protected Integer initialValue() {
                return Integer.valueOf(0);
            }
        };
    }

    static {
      //  desc.add(JMeterUtils.getResString("iteration_counter_arg_1")); //$NON-NLS-1$
        desc.add(JMeterUtils.getResString("function_name_paropt")); //$NON-NLS-1$
    }

    public Counter_tpcc() {
        init();
    }

    /** {@inheritDoc} */
    @Override
    public synchronized String execute(SampleResult previousResult, Sampler currentSampler)
            throws InvalidVariableException {

        globalCounter++;

        perThreadInt3.set(globalCounter);
        JMeterVariables vars = getVariables();
        
        

        boolean perThread = Boolean.parseBoolean("FALSE");

        String varName = ""; //$NON-NLS-1$
        if (variables.length >=1) {// Ensure variable has been provided
            varName = ((CompoundVariable) variables[0]).execute().trim();
        }

        String counterString = ""; //$NON-NLS-1$

        if (perThread) {
            int threadCounter;
            threadCounter = perThreadInt.get().intValue() + 1;
            perThreadInt.set(Integer.valueOf(threadCounter));
            counterString = String.valueOf(threadCounter);
        } else {
        	int a=globalCounter;
        	int whno=PassingVar.numwhs;
        	a=a%whno;
        	if(a==0)
        		a=whno;
            counterString = String.valueOf(a);
            System.out.println("\nin iteration counter"+globalCounter);
        }

        // vars will be null on Test Plan
        if (vars != null && varName.length() > 0) {
            vars.put(varName, counterString);
        }
        return counterString;
    }

    /** {@inheritDoc} */
    @Override
    public synchronized void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
        checkParameterCount(parameters, 0, 1);
        variables = parameters.toArray();
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
