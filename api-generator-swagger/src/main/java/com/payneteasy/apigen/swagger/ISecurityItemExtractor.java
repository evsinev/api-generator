package com.payneteasy.apigen.swagger;

import io.swagger.v3.oas.models.security.SecurityRequirement;

import java.lang.reflect.Method;
import java.util.Optional;

public interface ISecurityItemExtractor {

    Optional<SecurityRequirement> getSecurityItem(Class<?> aClass, Method aMethod);

}
