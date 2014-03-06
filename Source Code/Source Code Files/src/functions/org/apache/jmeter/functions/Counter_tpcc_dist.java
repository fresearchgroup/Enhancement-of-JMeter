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

import java.util.Random;


/**
 * Counter for calculating the district for a virtual user in TPC-C
 * @since 2.9
 */
public class Counter_tpcc_dist extends AbstractFunction {

	/**
	 * @author Surabhi
	 */
    private static final List<String> desc = new LinkedList<String>();
    
    private static final RandomGenerator ran = new RandomGenerator(0);

    private static final java.util.Random r = new java.util.Random();
    
    private static final int OL_I_ID_C = 7911; // in range [0, 8191]
	private static final int C_ID_C = 259; // in range [0, 1023]
	private static final int C_LAST_LOAD_C = 157; // in range [0, 255]
	private static final int C_LAST_RUN_C = 223; // in range [0, 255]
	
    private static final String KEY = "__counter_tpcc_dist"; //$NON-NLS-1$

    static {

        desc.add("variable");//$NON-NLS-1$ 
    }

    private CompoundVariable varName, minimum, maximum, char_base,num_char;

    /**
     * No-arg constructor.
     */
    public Counter_tpcc_dist() {
    }

    /** {@inheritDoc} */
    @Override
    public synchronized String execute(SampleResult previousResult, Sampler currentSampler)
            throws InvalidVariableException {


       int min = 1;
    		   
        int max = 10;

        int ran2=(int) (r.nextDouble() * (max - min + 1) + min);
        Counter_tpcc.perThreadInt1.set(ran2);
        
       
        String randString = Integer.toString(ran2);

        if (varName != null) {
            JMeterVariables vars = getVariables();
            final String varTrim = varName.execute().trim();
            if (vars != null && varTrim.length() > 0)
            {// vars will be null on TestPlan
                vars.put(varTrim, randString);
            }
        }

        return randString;

    }

    /** {@inheritDoc} */
    @Override
    public synchronized void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
        checkParameterCount(parameters, 0, 1);
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
    
    public static int nonUniformRandom(int A, int C, int min, int max, java.util.Random r) {
		return (((randomNumber(0, A, r) | randomNumber(min, max, r)) + C) % (max
				- min + 1))
				+ min;
	}
    
    public static int randomNumber(int min, int max, java.util.Random r) {
		return (int) (r.nextDouble() * (max - min + 1) + min);
	}

}
