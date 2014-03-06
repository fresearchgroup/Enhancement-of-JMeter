

//package com.kikini.perf.jmeter.functions;
 
package org.apache.jmeter.functions;

import java.util.ArrayList;

import java.util.Arrays;



import java.util.Collection;

import java.util.Collections;
import java.util.List;
 


import org.apache.jmeter.engine.util.CompoundVariable;
import org.apache.jmeter.functions.AbstractFunction;
import org.apache.jmeter.functions.InvalidVariableException;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.samplers.Sampler;

public class MaskUserIDFunction extends AbstractFunction {






    private static final List<String> DESC = Arrays.asList("uid_to_mask");

    private static final String KEY = "__maskUserID";

    private List<CompoundVariable> parameters = Collections.emptyList();





    @Override

    public String execute(SampleResult arg0, Sampler arg1) throws InvalidVariableException {

        List<String> resolvedArgs = new ArrayList<String>(parameters.size());




        for (CompoundVariable parameter : parameters) {


            resolvedArgs.add(parameter.execute());


        }

        // TODO: mask the user ID in resolvedArgs.get(0). For demo purposes,

        // just return the arguments given.
        return resolvedArgs.toString();

    }

    @Override
    public String getReferenceKey() {

        return KEY;

    }


    @SuppressWarnings("unchecked")

    @Override

    public void setParameters(Collection arg0) throws InvalidVariableException {

        parameters = new ArrayList<CompoundVariable>(arg0);


    }


    @Override

    public List<String> getArgumentDesc() {

        return DESC;

    }

}