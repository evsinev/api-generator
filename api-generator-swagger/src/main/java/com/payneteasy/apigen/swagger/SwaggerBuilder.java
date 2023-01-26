package com.payneteasy.apigen.swagger;

import com.payneteasy.apigen.core.util.Methods;
import com.payneteasy.apigen.swagger.SwaggerBuilderStrategy.*;
import com.payneteasy.apigen.swagger.impl.SwaggerMethodComponents;
import com.payneteasy.apigen.swagger.impl.SwaggerMethodPathItem;
import com.payneteasy.apigen.swagger.impl.sorted.SortedPaths;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class SwaggerBuilder {

    private static final Logger LOG = LoggerFactory.getLogger( SwaggerBuilder.class );

    private final OpenAPI                      api;
    private final List<Class<?>>               interfaces;
    private final IPathExtractor               methodPathExtractor;
    private final SwaggerMethodPathItem        swaggerMethodPathItem;
    private final List<Class<?>>               errorClasses;
    private final IMethodAcceptor              methodAcceptor;
    private final IServiceDescriptionExtractor serviceDescription;
    private final IServiceAddListener          serviceAddListener;

    public SwaggerBuilder(
              @Nonnull OpenAPI                        aOpenApi
            , @Nonnull IMethodAcceptor                aMethodAcceptor
            , @Nonnull List<Class<?>>                 aInterfaces
            , @Nonnull IPathExtractor                 aMethodPathExtractor
            , @Nonnull ISecurityItemExtractor         aSecurityItemExtractor
            , @Nonnull IServiceDescriptionExtractor   aServiceDescriptionExtractor
            , @Nonnull IOperationDescriptionExtractor aOperationDescriptionExtractor
            , @Nonnull IPathParameters aAdditionalParameters
            , @Nonnull List<Class<?>>                 aErrorClasses
            , @Nonnull IErrorResponsesExtractor       aErrorResponsesExtractor
            , @Nonnull IServiceAddListener            aServiceAddListener
    ) {
        interfaces          = createSortedArray(aInterfaces);
        api                 = aOpenApi;
        methodPathExtractor = aMethodPathExtractor;
        errorClasses        = aErrorClasses;
        methodAcceptor      = aMethodAcceptor;
        serviceDescription  = aServiceDescriptionExtractor;
        serviceAddListener  = aServiceAddListener;

        swaggerMethodPathItem = new SwaggerMethodPathItem(
                  aServiceDescriptionExtractor
                , aOperationDescriptionExtractor
                , aMethodPathExtractor
                , aSecurityItemExtractor
                , aAdditionalParameters
                , aErrorResponsesExtractor
        );
    }

    private static List<Class<?>> createSortedArray(List<Class<?>> aInterfaces) {
        ArrayList<Class<?>> classes = new ArrayList<>(aInterfaces);
        classes.sort(Comparator.comparing(Class::getSimpleName));
        return classes;
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

            serviceAddListener.onServiceAdd(paths, clazz);

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

    private PathItem createOverviewPathItem(Class<?> aClass) {
        Operation operation = new Operation();
        operation.addTagsItem(aClass.getSimpleName());
        operation.summary("Overview");
        serviceDescription.getServiceDescription(aClass).ifPresent(operation::description);

        PathItem item = new PathItem();
        item.operation(PathItem.HttpMethod.HEAD, operation);
        return item;
    }

    private List<Tag> getTags(List<Class<?>> aInterfaces) {
        return aInterfaces.stream()
                .map(trait -> new Tag()
                        .name(trait.getSimpleName())
                        .description(serviceDescription.getServiceDescription(trait).orElse(trait.getSimpleName()))
                )
                .collect(toList());
    }

}
