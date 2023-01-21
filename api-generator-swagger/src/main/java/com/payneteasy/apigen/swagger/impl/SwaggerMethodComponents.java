package com.payneteasy.apigen.swagger.impl;

import com.payneteasy.apigen.core.util.Methods;
import com.payneteasy.apigen.swagger.SwaggerBuilderStrategy;
import com.payneteasy.apigen.swagger.SwaggerBuilderStrategy.IMethodAcceptor;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.media.Schema;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.payneteasy.apigen.core.util.Fields.getAllFields;
import static com.payneteasy.apigen.swagger.impl.SwaggerSchemas.*;

public class SwaggerMethodComponents {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger( SwaggerMethodComponents.class );

    private final ModelConverters converters = ModelConverters.getInstance();
    private final Set<Class<?>>   added      = new HashSet<>();
    private final List<Class<?>>  errorClasses;
    private final Components      components = new Components();
    private final IMethodAcceptor methodAcceptor;

    public SwaggerMethodComponents(@Nonnull List<Class<?>> errorClasses, IMethodAcceptor aMethodAcceptor) {
        this.errorClasses = errorClasses;
        methodAcceptor = aMethodAcceptor;
    }

    public Components createComponents(List<Class<?>> aInterfaces) {
        for (Class<?> clazz : aInterfaces) {
            for (Method method : Methods.getAllMethods(clazz)) {

                if(!methodAcceptor.isMethodAccepted(clazz, method)) {
                    continue;
                }

                try {
                    addTypes(method.getReturnType());
                    for (Class<?> parameterType : method.getParameterTypes()) {
                        addTypes(parameterType);
                    }
                } catch (Exception e) {
                    LOG.error("Cannot processes {}.{}()", clazz.getSimpleName(), method.getName());
                }
            }
        }


        for (Class<?> errorClass : errorClasses) {
            addTypes(errorClass);
        }

        return components;
    }

    private void addTypes(Class<?> aType) {
        if(added.contains(aType)) {
            return;
        }

        added.add(aType);

        Map<String, Schema> map = converters.read(aType);
        for (Map.Entry<String, Schema> entry : map.entrySet()) {
            components.addSchemas(entry.getKey(), entry.getValue());
        }

        for (Field field : getAllFields(aType)) {
            if(isOurType(field)) {
                addTypes(field.getType());
            } else if(isCollection(field.getType())) {
                addTypes(getCollectionGenericType(field));
            }
        }
    }


}
