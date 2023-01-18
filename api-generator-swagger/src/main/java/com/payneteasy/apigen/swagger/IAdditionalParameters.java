package com.payneteasy.apigen.swagger;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import io.swagger.v3.oas.models.parameters.Parameter;

public interface IAdditionalParameters {

    Optional<List<Parameter>> getAdditionalParameters(String path, Class<?> clazz, Method aMethod);
}
