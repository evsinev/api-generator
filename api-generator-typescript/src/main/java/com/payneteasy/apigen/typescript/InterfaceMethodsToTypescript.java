package com.payneteasy.apigen.typescript;

import com.payneteasy.apigen.core.util.Methods;
import jakarta.ws.rs.Path;

import java.util.Collection;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;

public class InterfaceMethodsToTypescript {

    public static Collection<TypescriptMethod> getTypescriptMethods(String aPrefix, Class<?> aInterface) {
        Path path = aInterface.getAnnotation(Path.class);
        return Methods.getAllMethods(aInterface).stream()
                .map(method -> TypescriptMethod.builder()
                        .methodName(method.getName())
                        .returnType(method.getReturnType().getSimpleName())
                        .parameterType(method.getParameterTypes()[0].getSimpleName())
                        .path(aPrefix + path.value() + method.getAnnotation(Path.class).value())
                        .build())
                .sorted(comparing(TypescriptMethod::getMethodName))
                .collect(Collectors.toList());
    }
}
