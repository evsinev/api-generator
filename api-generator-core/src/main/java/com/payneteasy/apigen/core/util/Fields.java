package com.payneteasy.apigen.core.util;

import java.lang.reflect.Field;
import java.util.*;

import static java.util.Collections.addAll;
import static java.util.Comparator.comparing;

public class Fields {

    public static Collection<Field> getAllFields(Class<?> aClass) {
        Set<Field> fields = new HashSet<>();
        addAll(fields, aClass.getFields());
        addAll(fields, aClass.getDeclaredFields());

        List<Field> list = new ArrayList<>(fields);
        list.sort(comparing(Field::getName));

        return list;
    }

}
