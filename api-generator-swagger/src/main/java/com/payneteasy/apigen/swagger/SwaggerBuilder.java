package com.payneteasy.apigen.swagger;

import com.payneteasy.apigen.core.util.Methods;
import com.payneteasy.apigen.swagger.impl.SwaggerMethodComponents;
import com.payneteasy.apigen.swagger.impl.SwaggerMethodPathItem;
import io.swagger.v3.core.util.Yaml;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.tags.Tag;

import javax.annotation.Nonnull;
import javax.validation.constraints.NotNull;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static java.util.stream.Collectors.toList;

public class SwaggerBuilder {

    private final OpenAPI               api;
    private final List<Class<?>>        interfaces;
    private final IPathExtractor        methodPathExtractor;
    private final SwaggerMethodPathItem swaggerMethodPathItem;

    public SwaggerBuilder(
              @Nonnull OpenAPI                        aOpenApi
            , @Nonnull List<Class<?>>                 aInterfaces
            , @Nonnull IPathExtractor                 aMethodPathExtractor
            , @Nonnull ISecurityItemExtractor         aSecurityItemExtractor
            , @Nonnull IOperationDescriptionExtractor aOperationDescriptionExtractor
            , @Nonnull IAdditionalParameters          aAdditionalParameters
    ) {
        interfaces          = aInterfaces;
        api                 = aOpenApi;
        methodPathExtractor = aMethodPathExtractor;

        swaggerMethodPathItem = new SwaggerMethodPathItem(
                  aOperationDescriptionExtractor
                , aMethodPathExtractor
                , aSecurityItemExtractor
                , aAdditionalParameters
        );
    }

    public String buildYaml() {
        api
            .tags(getTags(interfaces))
            .paths(createPaths(interfaces))
            .setComponents(new SwaggerMethodComponents()
                .createComponents(interfaces)
            );

        return Yaml.pretty(api);
    }

    private Paths createPaths(List<Class<?>> aInterfaces) {
        SortedPaths paths = new SortedPaths();
        for (Class<?> clazz : aInterfaces) {
            for (Method method : Methods.getAllMethods(clazz)) {
                paths.addPathItem(
                          methodPathExtractor.getMethodPath(clazz, method)
                        , swaggerMethodPathItem.createPathItem(clazz, method)
                );
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
        public int compareTo(@NotNull SwaggerBuilder.SortedMapEntry another) {
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
