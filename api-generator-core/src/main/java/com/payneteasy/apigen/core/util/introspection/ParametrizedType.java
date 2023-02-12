package com.payneteasy.apigen.core.util.introspection;

import java.lang.reflect.Field;

public class ParametrizedType {

    public static ParametrizedType emptyParametrizedType() {
        return new ParametrizedType();
    }

    public static ParametrizedType paraTypeFromField(Field aField) {
        return new ParametrizedType();
    }
}
