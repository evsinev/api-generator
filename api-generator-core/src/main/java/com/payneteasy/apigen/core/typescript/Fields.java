package com.payneteasy.apigen.core.typescript;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Fields {

    public static Collection<Field> getAllFields(Class<?> aClass) {
        Set<Field> fields = new HashSet<>();
        Collections.addAll(fields, aClass.getFields());
        Collections.addAll(fields, aClass.getDeclaredFields());
        return fields;
    }

}
