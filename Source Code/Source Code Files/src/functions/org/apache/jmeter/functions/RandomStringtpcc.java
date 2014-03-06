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
 * Provides a Random function generates and returns a random string
 * @since 2.9
 */
public class RandomStringtpcc extends AbstractFunction {
	/**
	 * @author surabhi
	 */

    private static final List<String> desc = new LinkedList<String>();
    
    

    private static final String KEY = "__RandomStringtpcc"; //$NON-NLS-1$

    static {
        desc.add("min_value"); //$NON-NLS-1$
        desc.add("max_value"); //$NON-NLS-1$
        desc.add("char_base");//$NON-NLS-1$
        desc.add("num_char");//$NON-NLS-1$
        desc.add("variable");//$NON-NLS-1$ 
    }

    private CompoundVariable varName, minimum, maximum, char_base,num_char;

    /**
     * No-arg constructor.
     */
    public RandomStringtpcc() {
    }

    /** {@inheritDoc} */
    @Override
    public synchronized String execute(SampleResult previousResult, Sampler currentSampler)
            throws InvalidVariableException {


        int min = Integer.parseInt(minimum.execute().trim());
        int max = Integer.parseInt(maximum.execute().trim());
        char c = (char_base.execute().trim()).charAt(0);
        int nc = Integer.parseInt(num_char.execute().trim());

       
        
        long rand = min + (long) (Math.random() * (max - min + 1));

        String randString = Long.toString(rand);

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
        checkParameterCount(parameters, 2, 3);
        Object[] values = parameters.toArray();

        minimum = (CompoundVariable) values[0];
        maximum = (CompoundVariable) values[1];
        if (values.length>2)
        {
            varName = (CompoundVariable) values[2];
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

}
