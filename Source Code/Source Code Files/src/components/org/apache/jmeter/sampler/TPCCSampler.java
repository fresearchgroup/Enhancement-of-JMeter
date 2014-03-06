package org.apache.jmeter.sampler;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


import org.apache.jmeter.config.ConfigTestElement;
import org.apache.jmeter.samplers.AbstractSampler;
import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.services.FileServer;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.property.CollectionProperty;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;
import org.apache.jmeter.sampler.tpcc.*;
import org.apache.jmeter.sampler.tpcc.jdbc.*;
import org.apache.jmeter.sampler.tpcc.tables.*;
import org.apache.jmeter.sampler.tpcc.util.*;

/**
 * Sampler-Class for JMeter - builds, starts and interprets the results of the
 * sampler. Has to implement some standard-methods for JMeter in order to be
 * integrated in the framework. All getter/setter methods just deliver/set
 * values from/to the sampler, not from/to the message-object. Therefore, all
 * these methods are also present in class SendMailCommand.
 * 
 * 
 */
public class TPCCSampler extends AbstractSampler {
	/**
	 * @author naman
	 * @since 2.9
	 */

    private static final long serialVersionUID = 1L;

    private static final Logger log = LoggingManager.getLoggerForClass();
    public static  String DBURL              			= "TPCCSampler.dburl"; // $NON-NLS-1$
    public static  String DRIVER_CLASS              	= "TPCCSampler.driverclass"; // $NON-NLS-1$
    public static  String USERNAME              		= "TPCCSampler.username"; // $NON-NLS-1$
    public static  String PASSWORD             			= "TPCCSampler.password"; // $NON-NLS-1$
    public static  String DB_NAME           			= "TPCCSampler.dbName"; // $NON-NLS-1$
    public static  String NUM_WHSE        	 			= "TPCCSampler.numWhse"; // $NON-NLS-1$
    
    public TPCCSampler() {
    }

    /**
     * Performs the sample, and returns the result
     *
     * @param e Standard-method-header from JMeter
     * @return sampleresult Result of the sample
     * @see org.apache.jmeter.samplers.Sampler#sample(org.apache.jmeter.samplers.Entry)
     */
    @Override
   public SampleResult sample(Entry e) 
    {
     SampleResult res = new SampleResult();
     return res;
    }
    public void callCreator(boolean status)
    {
    	if(status)
    	{
    		CreatorOracle co=new CreatorOracle();
    		try
        	{
        		co.create();
        	}
        	catch(Exception e)
        	{
        		e.printStackTrace();
        	}
    		
    	}
    	else
    	{
    		Creator c=new Creator();
        	try
        	{
        		c.create();
        	}
        	catch(Exception e)
        	{
        		e.printStackTrace();
        	}
    	}
    	
    }

}