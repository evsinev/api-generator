package com.payneteasy.apigen.swagger.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Packages whose types are described as opaque objects instead of being introspected.
 * <p>
 * Without this, {@code sun.misc.JavaLangAccess}, {@code sun.util.calendar.Era} and the like end up
 * in {@code components/schemas} of a public API reference, dragged in by a single property of a
 * framework type.
 * <p>
 * {@code java.} and {@code javax.} are deliberately absent: swagger-core maps {@code String},
 * {@code Date}, {@code LocalDate} and friends onto proper primitive schemas, and
 * {@link SwaggerSchemas#createSchema} routes them through {@code PrimitiveType} — declaring them
 * terminal would turn every string into {@code {type: object}}.
 */
public class TerminalPackages {

    private static final List<String> DEFAULT_PREFIXES = Collections.unmodifiableList(Arrays.asList(
              "sun."
            , "com.sun."
            , "jdk."
            , "org.springframework."
            , "org.joda."
    ));

    private static final TerminalPackages DEFAULTS = new TerminalPackages(DEFAULT_PREFIXES);

    private final List<String> prefixes;

    public TerminalPackages(List<String> aPrefixes) {
        prefixes = aPrefixes == null ? DEFAULT_PREFIXES : aPrefixes;
    }

    public static TerminalPackages defaults() {
        return DEFAULTS;
    }

    public static List<String> defaultPrefixes() {
        return DEFAULT_PREFIXES;
    }

    public boolean isTerminal(Class<?> aClass) {
        if (aClass == null) {
            return false;
        }
        String name = aClass.getName();
        for (String prefix : prefixes) {
            if (name.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }
}
