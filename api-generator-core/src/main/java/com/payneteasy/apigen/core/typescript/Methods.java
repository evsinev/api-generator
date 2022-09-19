package com.payneteasy.apigen.core.typescript;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;

public class Methods {

    public static Collection<Method> getAllMethods(Class<?> aClass) {
        HashSet<Method> methods = new HashSet<>();
        for (Method method : aClass.getMethods()) {
            methods.add(method);
        }
        for (Method method : aClass.getDeclaredMethods()) {
            methods.add(method);
        }
        return methods;
    }

}
