package org.apache.jmeter.functions;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.jmeter.engine.util.CompoundVariable;

import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.samplers.Sampler;
import org.apache.jmeter.threads.JMeterVariables;
import org.apache.jmeter.util.JMeterUtils;



public class Counter_tpcc_3 extends AbstractFunction {
	/**
	 * @author surabhi
	 */

    private static final List<String> desc = new LinkedList<String>();

    private static final String KEY = "__countertpcc_2"; //$NON-NLS-1$

    private ThreadLocal<Integer> perThreadInt1;

    private Object[] variables;

    private int gcount;//MAXINT = 2,147,483,647

    private void init()
    {
       synchronized(this)
       {
    	   gcount=0;
       }
       perThreadInt1 = new ThreadLocal<Integer>()
       {
            @Override
            protected Integer initialValue()
            {
                return Integer.valueOf(0);
            }
        };
    }

    static
    {
      
        desc.add(JMeterUtils.getResString("function_name_paropt")); //$NON-NLS-1$
    }

    public Counter_tpcc_3() {
        init();
    }

    /** {@inheritDoc} */
    @Override
    public synchronized String execute(SampleResult previousResult, Sampler currentSampler)
            throws InvalidVariableException {

    	gcount++;

        JMeterVariables vars = getVariables();

        boolean perThread=false;

        String varName = ""; //$NON-NLS-1$
        if (variables.length >=1) {// Ensure variable has been provided
            varName = ((CompoundVariable) variables[0]).execute().trim();
        }

        String counterString = ""; //$NON-NLS-1$
        int whno;
        try
        {
        	
        	whno=2;
        	int a;
    		System.out.println("value of warehouse noted "+whno);
        
		
		
        if (perThread) 
        {
            int threadCounter;
            threadCounter = perThreadInt1.get().intValue() + 1;
            perThreadInt1.set(Integer.valueOf(threadCounter));
            counterString = String.valueOf(threadCounter);
            System.out.println("in if");
        } 
        else 
        {
        
        		a=gcount;
        		System.out.println("value of warehouse noted "+a);
        		counterString = String.valueOf(a);
        }

        // vars will be null on Test Plan
        if (vars != null && varName.length() > 0) {
            vars.put(varName, counterString);
        }
        
    }
        catch(Exception e)
        {
        	e.printStackTrace();
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
