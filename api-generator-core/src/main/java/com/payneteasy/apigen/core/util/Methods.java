package com.payneteasy.apigen.core.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import static java.util.Collections.addAll;
import static java.util.Comparator.comparing;

public class Methods {

    public static Collection<Method> getAllMethods(Class<?> aClass) {
        HashSet<Method> methods = new HashSet<>();
        addAll(methods, aClass.getMethods());
        addAll(methods, aClass.getDeclaredMethods());

        List<Method> list = new ArrayList<>(methods);
        list.sort(comparing(Method::getName));

        return list;
    }

}
