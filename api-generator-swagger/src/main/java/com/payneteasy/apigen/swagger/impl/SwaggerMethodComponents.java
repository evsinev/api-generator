package com.payneteasy.apigen.swagger.impl;

import com.payneteasy.apigen.core.util.Methods;
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

    private final ModelConverters converters = ModelConverters.getInstance();
    private final Set<Class<?>>   added      = new HashSet<>();
    private final List<Class<?>>  errorClasses;
    private final Components      components = new Components();

    public SwaggerMethodComponents(@Nonnull List<Class<?>> errorClasses) {
        this.errorClasses = errorClasses;
    }

    public Components createComponents(List<Class<?>> aInterfaces) {
        for (Class<?> clazz : aInterfaces) {
            for (Method method : Methods.getAllMethods(clazz)) {
                addTypes(method.getReturnType());
                for (Class<?> parameterType : method.getParameterTypes()) {
                    addTypes(parameterType);
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
