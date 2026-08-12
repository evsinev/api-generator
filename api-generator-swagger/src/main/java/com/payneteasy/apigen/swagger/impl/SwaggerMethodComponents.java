package com.payneteasy.apigen.swagger.impl;

import com.payneteasy.apigen.core.util.Methods;
import com.payneteasy.apigen.swagger.SwaggerBuilderStrategy.IMethodAcceptor;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.media.Schema;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.payneteasy.apigen.core.util.Fields.getAllFields;
import static com.payneteasy.apigen.swagger.impl.SwaggerSchemas.*;

public class SwaggerMethodComponents {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger( SwaggerMethodComponents.class );

    /** Types whose subtree has been walked to the end. */
    private final Set<Class<?>>    completed  = new HashSet<>();
    /** Types currently being walked — the cycle guard for mutually referencing models. */
    private final Set<Class<?>>    inProgress = new HashSet<>();
    private final List<Class<?>>   errorClasses;
    private final Components       components = new Components();
    private final IMethodAcceptor  methodAcceptor;
    private final TerminalPackages terminalPackages;
    private final ModelConverters  converters;

    public SwaggerMethodComponents(@Nonnull List<Class<?>> errorClasses, IMethodAcceptor aMethodAcceptor) {
        this(errorClasses, aMethodAcceptor, TerminalPackages.defaults());
    }

    public SwaggerMethodComponents(
              @Nonnull List<Class<?>> errorClasses
            , IMethodAcceptor         aMethodAcceptor
            , TerminalPackages        aTerminalPackages
    ) {
        this.errorClasses = errorClasses;
        methodAcceptor    = aMethodAcceptor;
        terminalPackages  = aTerminalPackages == null ? TerminalPackages.defaults() : aTerminalPackages;

        // an own instance, not ModelConverters.getInstance(): the singleton is JVM-wide and
        // registering a converter on it would change the behaviour of every other swagger user
        // in the process
        converters = new ModelConverters();
        converters.addConverter(new TerminalTypeModelConverter(terminalPackages));
    }

    public Components createComponents(List<Class<?>> aInterfaces) {
        for (Class<?> clazz : aInterfaces) {
            for (Method method : Methods.getAllMethods(clazz)) {

                if(!methodAcceptor.isMethodAccepted(clazz, method)) {
                    continue;
                }

                // one try per type: a type we cannot handle must not take the sibling
                // parameters down with it, because createPaths() still emits $refs for them
                addTypesSafely(clazz, method, method.getReturnType(), method.getGenericReturnType());

                for(int i=0; i<method.getParameterTypes().length; i++) {
                    addTypesSafely(clazz, method, method.getParameterTypes()[i], method.getGenericParameterTypes()[i]);
                }
            }
        }


        for (Class<?> errorClass : errorClasses) {
            addTypes(errorClass, null);
        }

        return components;
    }

    private void addTypesSafely(Class<?> aClass, Method aMethod, Class<?> aType, Type aGenericType) {
        try {
            addTypes(aType, aGenericType);
        } catch (Exception e) {
            LOG.error("Cannot process type {} of {}.{}()", aType, aClass.getSimpleName(), aMethod.getName(), e);
        }
    }

    private void addTypes(Class<?> aClass, Type aType) {
        if(SwaggerSchemas.isCollection(aClass)) {
            Class<?> collectionGenericType = getCollectionGenericType(aType);
            addTypes(collectionGenericType, null);
            return;
        }

        if(completed.contains(aClass) || terminalPackages.isTerminal(aClass)) {
            return;
        }

        // already on the stack: stop, or a cycle between two models would recurse forever
        if(!inProgress.add(aClass)) {
            return;
        }

        try {
            Map<String, Schema> map = converters.readAll(aClass);
            for (Map.Entry<String, Schema> entry : map.entrySet()) {
                components.addSchemas(entry.getKey(), entry.getValue());
            }

            for (Field field : getAllFields(aClass)) {
                if(isOurType(field)) {
                    addTypes(field.getType(), field.getGenericType());
                } else if(isCollection(field.getType())) {
                    addTypes(getCollectionGenericType(field), field.getGenericType());
                }
            }

            // marked only now: a type whose subtree blew up must be retried from the next
            // entry point instead of being remembered as done
            completed.add(aClass);
        } finally {
            inProgress.remove(aClass);
        }
    }


}
