package org.apache.jmeter.functions;

import org.apache.log.Logger;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.jmeter.engine.util.CompoundVariable;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.samplers.Sampler;
import org.apache.jorphan.logging.LoggingManager;


/**
 * Function to return the warehouse id.
 * @since 2.9
 */
public class GetWarehouseID extends AbstractFunction {
	/**
	 * @author surabhi
	 */


	 private static final Logger log = LoggingManager.getLoggerForClass();
	 
	private static final String KEY = "__GetWarehouseID"; //$NON-NLS-1$

    private static final List<String> desc = new LinkedList<String>();

    /** {@inheritDoc} */
    @Override
    public String execute(SampleResult previousResult, Sampler currentSampler) throws InvalidVariableException {
       
       
        int whnumber;
        int b;
        int whno = getResult2(); //warehouseno taken from user
        System.out.println(whno);
         
        String wh= ""+whno;
        
        return wh;
    }

    /** {@inheritDoc} */
    @Override
    public void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
        checkParameterCount(parameters,0,0);
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
    
    public int getResult2() {
        //return KEY;

   	 String threadName = Thread.currentThread().getName();
   	       return Integer.parseInt(threadName.substring(threadName.lastIndexOf('-') + 1));
    }
    
    
}
