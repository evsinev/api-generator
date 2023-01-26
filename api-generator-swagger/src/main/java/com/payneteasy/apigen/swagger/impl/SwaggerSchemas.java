package com.payneteasy.apigen.swagger.impl;

import io.swagger.v3.core.util.PrimitiveType;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Collection;

public class SwaggerSchemas {

    public static Schema<?> createSchema(Class<?> aClass, String aLocation) {
        return createSchema(aClass, null, aLocation);
    }

    public static Schema<?> createSchema(Class<?> aClass, Type aType, String aLocation) {
        PrimitiveType primitiveType = PrimitiveType.fromType(aClass);

        if(primitiveType != null) {
            return primitiveType.createProperty();
        }

        if(isOurType(aClass)) {
            return new ObjectSchema().$ref("#/components/schemas/" + aClass.getSimpleName());
        }

        if(isCollection(aClass)) {
            return new ArraySchema()
                    .items(createSchema(
                            getCollectionGenericType(aType)
                            , aLocation + " is collection"
                    ));
        }

        throw new IllegalStateException("No any schema type for " + aClass + " : " + aLocation);
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
        return (Class<?>) genericType.getActualTypeArguments()[0];
    }

    static Class<?> getCollectionGenericType(Type aType) {
        ParameterizedType genericType = (ParameterizedType)aType ;

        Type firstArgument = genericType.getActualTypeArguments()[0];

        if(firstArgument instanceof Class<?>) {
            return (Class<?>) firstArgument;
        }

        if(firstArgument instanceof WildcardType) {
            WildcardType wildcardType = (WildcardType) firstArgument;
            return (Class<?>) wildcardType.getUpperBounds()[0];
        }

        throw new IllegalStateException("Strange argument" + firstArgument + " for type " + aType);
//        return (Class<?>) genericType.getActualTypeArguments()[0];
    }
}
