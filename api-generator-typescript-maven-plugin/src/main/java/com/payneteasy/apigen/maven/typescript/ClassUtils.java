package com.payneteasy.apigen.maven.typescript;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.Arrays.stream;

public class ClassUtils {

    public static List<Method> getAllMethods(Class aClass) {

        List<Method> list = new ArrayList<>(asList(aClass.getMethods()));
        Set<Method>  set = new HashSet<>(list);

        list.addAll(stream(aClass.getDeclaredMethods())
                .filter(method -> !set.contains(method))
                .collect(Collectors.toList()));

        return list;
    }

    public static String getLastPackageName(Class<?> aClass) {
        StringTokenizer st = new StringTokenizer(aClass.getPackage().getName(), ".");
        String lastName = "unknown_name";
        while(st.hasMoreTokens()) {
            lastName = st.nextToken();
        }
        return lastName;
    }
}
