package com.payneteasy.apigen.swagger.impl;

import com.payneteasy.apigen.core.util.Methods;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.media.Schema;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.payneteasy.apigen.core.util.Fields.getAllFields;

public class SwaggerMethodComponents {

    private final ModelConverters converters = ModelConverters.getInstance();
    private final Set<Class<?>>     added      = new HashSet<>();

    public Components createComponents(List<Class<?>> aInterfaces) {
        Components components = new Components();
        for (Class<?> clazz : aInterfaces) {
            for (Method method : Methods.getAllMethods(clazz)) {
                addTypes(components, method.getReturnType());
                addTypes(components, method.getParameterTypes()[0]);
            }
        }

        // todo add non-200 error responses

        return components;
    }

    private void addTypes(Components aComponents, Class<?> aType) {
        if(added.contains(aType)) {
            return;
        }

        added.add(aType);

        Map<String, Schema> map = converters.read(aType);
        for (Map.Entry<String, Schema> entry : map.entrySet()) {
            aComponents.addSchemas(entry.getKey(), entry.getValue());
        }

        for (Field field : getAllFields(aType)) {
            if(isOurType(field)) {
                addTypes(aComponents, field.getType());
            }
        }
    }

    private boolean isOurType(Field field) {
        Class<?> type = field.getType();

        if(type.isPrimitive()) {
            return false;
        }

        return !type.getName().startsWith("java");
    }

}
