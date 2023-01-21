package com.payneteasy.apigen.swagger;

import com.payneteasy.apigen.core.util.Methods;
import com.payneteasy.apigen.swagger.SwaggerBuilderStrategy.*;
import com.payneteasy.apigen.swagger.impl.SwaggerMethodComponents;
import com.payneteasy.apigen.swagger.impl.SwaggerMethodPathItem;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static java.util.stream.Collectors.toList;

public class SwaggerBuilder {

    private static final Logger LOG = LoggerFactory.getLogger( SwaggerBuilder.class );

    private final OpenAPI                               api;
    private final List<Class<?>>                        interfaces;
    private final IPathExtractor methodPathExtractor;
    private final SwaggerMethodPathItem                 swaggerMethodPathItem;
    private final List<Class<?>>                        errorClasses;
    private final IMethodAcceptor                       methodAcceptor;

    public SwaggerBuilder(
              @Nonnull OpenAPI                        aOpenApi
            , @Nonnull IMethodAcceptor                aMethodAcceptor
            , @Nonnull List<Class<?>>                 aInterfaces
            , @Nonnull IPathExtractor                 aMethodPathExtractor
            , @Nonnull ISecurityItemExtractor         aSecurityItemExtractor
            , @Nonnull IOperationDescriptionExtractor aOperationDescriptionExtractor
            , @Nonnull IAdditionalParameters          aAdditionalParameters
            , @Nonnull List<Class<?>>                 aErrorClasses
            , @Nonnull IErrorResponsesExtractor       aErrorResponsesExtractor
    ) {
        interfaces              = aInterfaces;
        api                     = aOpenApi;
        methodPathExtractor     = aMethodPathExtractor;
        errorClasses            = aErrorClasses;
        methodAcceptor          = aMethodAcceptor;

        swaggerMethodPathItem = new SwaggerMethodPathItem(
                  aOperationDescriptionExtractor
                , aMethodPathExtractor
                , aSecurityItemExtractor
                , aAdditionalParameters
                , aErrorResponsesExtractor
        );
    }

    public OpenAPI buildOpenApiModel() {
        api
            .tags(getTags(interfaces))
            .paths(createPaths(interfaces))
            .setComponents(new SwaggerMethodComponents(errorClasses, methodAcceptor)
                .createComponents(interfaces)
            );

        return api;
    }

    private Paths createPaths(List<Class<?>> aInterfaces) {
        SortedPaths paths = new SortedPaths();
        for (Class<?> clazz : aInterfaces) {
            for (Method method : Methods.getAllMethods(clazz)) {

                if(!methodAcceptor.isMethodAccepted(clazz, method)) {
                    continue;
                }

                try {
                    paths.addPathItem(
                              methodPathExtractor.getMethodPath(clazz, method)
                            , swaggerMethodPathItem.createPathItem(clazz, method)
                    );
                } catch (Exception e) {
                    LOG.error("Cannot create path items for method {}.{}()", clazz.getSimpleName(), method.getName());
                }
            }
        }
        return paths;
    }

    private List<Tag> getTags(List<Class<?>> aInterfaces) {
        return aInterfaces.stream()
                .map(trait -> new Tag()
                        .name(trait.getSimpleName())
                        .description(trait.getSimpleName())
                )
                .collect(toList());
    }

    private static class SortedPaths extends Paths {
        @Override
        public Set<Map.Entry<String, PathItem>> entrySet() {
            TreeSet<Map.Entry<String, PathItem>> set = new TreeSet<>();
            for (Map.Entry<String, PathItem> entry : super.entrySet()) {
                set.add(new SortedMapEntry(entry));
            }
            return set;
        }
    }

    private static class SortedMapEntry implements Map.Entry<String, PathItem>, Comparable<SortedMapEntry> {
        private final Map.Entry<String, PathItem> delegate;

        public SortedMapEntry(Map.Entry<String, PathItem> delegate) {
            this.delegate = delegate;
        }

        @Override
        public int compareTo(@Nonnull SwaggerBuilder.SortedMapEntry another) {
            return delegate.getKey().compareTo(another.getKey());
        }

        @Override
        public String getKey() {
            return delegate.getKey();
        }

        @Override
        public PathItem getValue() {
            return delegate.getValue();
        }

        @Override
        public PathItem setValue(PathItem value) {
            return delegate.setValue(value);
        }
    }
}
