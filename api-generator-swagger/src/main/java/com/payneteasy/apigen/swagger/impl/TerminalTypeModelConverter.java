package com.payneteasy.apigen.swagger.impl;

import com.fasterxml.jackson.databind.JavaType;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverter;
import io.swagger.v3.core.converter.ModelConverterContext;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Iterator;

/**
 * Stops swagger-core from resolving — and therefore from defining a component for — types of
 * {@link TerminalPackages}. Filtering the result of {@code readAll} afterwards is not an option:
 * that map is keyed by schema name, which no longer says which class it came from.
 */
public class TerminalTypeModelConverter implements ModelConverter {

    private final TerminalPackages terminalPackages;

    public TerminalTypeModelConverter(TerminalPackages aTerminalPackages) {
        terminalPackages = aTerminalPackages;
    }

    @Override
    public Schema resolve(AnnotatedType aType, ModelConverterContext aContext, Iterator<ModelConverter> aChain) {
        if (terminalPackages.isTerminal(rawClass(aType.getType()))) {
            return new ObjectSchema();
        }
        return aChain.hasNext() ? aChain.next().resolve(aType, aContext, aChain) : null;
    }

    private static Class<?> rawClass(Type aType) {
        if (aType instanceof Class) {
            return (Class<?>) aType;
        }
        if (aType instanceof JavaType) {
            return ((JavaType) aType).getRawClass();
        }
        if (aType instanceof ParameterizedType) {
            Type raw = ((ParameterizedType) aType).getRawType();
            return raw instanceof Class ? (Class<?>) raw : null;
        }
        return null;
    }
}
