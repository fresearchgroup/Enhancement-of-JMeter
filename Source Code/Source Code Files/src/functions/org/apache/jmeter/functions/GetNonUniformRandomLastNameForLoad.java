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
 * Provides a function which returns a string to be used as the last name while loading
 * @since 2.9
 */
public class GetNonUniformRandomLastNameForLoad extends AbstractFunction {
	/**
	 * @author surabhi
	 */

    private static final List<String> desc = new LinkedList<String>();
    
    private static final RandomGenerator ran = new RandomGenerator(0);

    private static final java.util.Random r = new java.util.Random();
    
	public final static int NEW_ORDER = 1, PAYMENT = 2, ORDER_STATUS = 3,
			DELIVERY = 4, STOCK_LEVEL = 5;

	public final static String[] nameTokens = { "BAR", "OUGHT", "ABLE", "PRI",
			"PRES", "ESE", "ANTI", "CALLY", "ATION", "EING" };
    
    private static final int OL_I_ID_C = 7911; // in range [0, 8191]
	private static final int C_ID_C = 259; // in range [0, 1023]
	// NOTE: TPC-C 2.1.6.1 specifies that abs(C_LAST_LOAD_C - C_LAST_RUN_C) must
	// be within [65, 119]
	private static final int C_LAST_LOAD_C = 157; // in range [0, 255]
	private static final int C_LAST_RUN_C = 223; // in range [0, 255]
	
    private static final String KEY = "__getNURandLNameRun"; //$NON-NLS-1$

    static 
    {
           desc.add("variable");//$NON-NLS-1$ 
    }

    private CompoundVariable varName, minimum, maximum, char_base,num_char;

    /**
     * No-arg constructor.
     */
    public GetNonUniformRandomLastNameForLoad() {
    }

    /** {@inheritDoc} */
    @Override
    public synchronized String execute(SampleResult previousResult, Sampler currentSampler)
            throws InvalidVariableException {


      
      String randString;
      randString = getLastName(nonUniformRandom(255, C_LAST_LOAD_C, 0, 999, r));
       
      if (varName != null) 
      {
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
        
        Object[] values = parameters.toArray();

        if (values.length>0)
        {
            varName = (CompoundVariable) values[0];
        } else
        {
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
    
    public static String getLastName(int num) {
		return nameTokens[num / 100] + nameTokens[(num / 10) % 10]
				+ nameTokens[num % 10];
	}

}
