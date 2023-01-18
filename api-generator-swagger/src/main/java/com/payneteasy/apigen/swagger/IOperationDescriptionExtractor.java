package com.payneteasy.apigen.swagger;

import java.lang.reflect.Method;
import java.util.Optional;

public interface IOperationDescriptionExtractor {

    Optional<String> getOperationDescription(String aPath, Class<?> aClass, Method aMethod);

}
