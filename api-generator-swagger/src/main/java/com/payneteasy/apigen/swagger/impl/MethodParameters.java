package com.payneteasy.apigen.swagger.impl;

import java.util.List;

public class MethodParameters {

    private final List<MethodParameter> parameters;

    public MethodParameters(List<MethodParameter> parameters) {
        this.parameters = parameters;
    }

    public List<MethodParameter> getParameters() {
        return parameters;
    }


    public boolean hasParameters() {
        return parameters.size() > 0 && !parameters.get(0).getType().equals("VoidRequest");
    }
}
