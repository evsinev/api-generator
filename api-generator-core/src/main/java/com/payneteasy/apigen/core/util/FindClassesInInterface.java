package com.payneteasy.apigen.core.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.payneteasy.apigen.core.util.Methods.getAllMethods;

public class FindClassesInInterface {

    public static List<Class<?>> getAllClassesForInterfaces(Class<?> aClass) {
        Collection<Method> methods = getAllMethods(aClass);
        List<Class<?>> classes = new ArrayList<>();
        for (Method method : methods) {
            classes.add(method.getReturnType());
            classes.addAll(Arrays.asList(method.getParameterTypes()));
        }
        return classes;
    }

}
