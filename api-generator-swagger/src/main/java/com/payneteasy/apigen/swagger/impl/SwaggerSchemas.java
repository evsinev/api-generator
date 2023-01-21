package com.payneteasy.apigen.swagger.impl;

import io.swagger.v3.core.util.PrimitiveType;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;

public class SwaggerSchemas {

    public static Schema<?> createSchema(Class<?> aClass) {
        PrimitiveType primitiveType = PrimitiveType.fromType(aClass);

        if(primitiveType != null) {
            return primitiveType.createProperty();
        }

        if(isOurType(aClass)) {
            return new ObjectSchema().$ref("#/components/schemas/" + aClass.getSimpleName());
        }

        throw new IllegalStateException("No any schema type for " + aClass);
    }

    static boolean isOurType(Field field) {
        return isOurType(field.getType());
    }

    static boolean isOurType(Class<?> type) {

        if(type.isPrimitive()) {
            return false;
        }

        return !type.getName().startsWith("java");
    }

    static boolean isCollection(Class<?> aClass) {
        return Collection.class.isAssignableFrom(aClass);
    }

    static Class<?> getCollectionGenericType(Field aField) {
        ParameterizedType genericType = (ParameterizedType) aField.getGenericType();
        if(genericType == null) {
            throw new IllegalStateException("Generic type is null for field " + aField.getName());
        }

        return (Class<?>) genericType.getActualTypeArguments()[0];
    }
}
