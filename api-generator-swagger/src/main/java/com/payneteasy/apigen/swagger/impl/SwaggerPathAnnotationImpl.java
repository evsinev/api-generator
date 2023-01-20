package com.payneteasy.apigen.swagger.impl;

import com.payneteasy.apigen.swagger.SwaggerBuilderStrategy;
import jakarta.ws.rs.Path;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;

public class SwaggerPathAnnotationImpl implements SwaggerBuilderStrategy.IPathExtractor {

    @Override
    @Nonnull
    public String getMethodPath(Class<?> aClass, Method aMethod) {
        return aClass.getAnnotation(Path.class).value()
                + aMethod.getAnnotation(Path.class).value();
    }
}
