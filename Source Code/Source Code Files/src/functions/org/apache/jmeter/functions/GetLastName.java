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
 * Provides a Random function which returns a random string to be used as the last name
 * @since 2.9
 */
public class GetLastName extends AbstractFunction {
	/**
	 * @author surabhi
	 */

    private static final List<String> desc = new LinkedList<String>();
    
    private static final RandomGenerator ran = new RandomGenerator(0);
    
	public final static int NEW_ORDER = 1, PAYMENT = 2, ORDER_STATUS = 3,
			DELIVERY = 4, STOCK_LEVEL = 5;

	public final static String[] nameTokens = { "BAR", "OUGHT", "ABLE", "PRI",
			"PRES", "ESE", "ANTI", "CALLY", "ATION", "EING" };


    private static final String KEY = "__getLastName"; //$NON-NLS-1$

    static 
    {
        desc.add("min_value"); //$NON-NLS-1$
        desc.add("variable");//$NON-NLS-1$ 
    }

    private CompoundVariable varName, minimum, maximum, char_base,num_char;

    /**
     * No-arg constructor.
     */
    public GetLastName() {
    }

    /** {@inheritDoc} */
    @Override
    public synchronized String execute(SampleResult previousResult, Sampler currentSampler)
            throws InvalidVariableException {


        int min = Integer.parseInt(minimum.execute().trim());
        int num=min;
        String randString;
        
        randString= nameTokens[num / 100] + nameTokens[(num / 10) % 10]
				+ nameTokens[num % 10];

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
        checkParameterCount(parameters, 1, 2);
        Object[] values = parameters.toArray();

        minimum = (CompoundVariable) values[0];
        if (values.length>1)
        {
            varName = (CompoundVariable) values[1];
        } 
        else
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

}
