package com.payneteasy.apigen.core.util;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static java.util.Collections.addAll;

public class Fields {

    public static Collection<Field> getAllFields(Class<?> aClass) {
        Set<Field> fields = new HashSet<>();
        addAll(fields, aClass.getFields());
        addAll(fields, aClass.getDeclaredFields());
        return fields;
    }

}
