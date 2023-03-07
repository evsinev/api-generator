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
import lombok.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
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
    private final IServiceTagExtractor         serviceTag;
    private final IServiceAddListener          serviceAddListener;

    @Builder
    public SwaggerBuilder(
              @Nonnull OpenAPI                        openApi
            , @Nonnull IMethodAcceptor                methodAcceptor
            , @Nonnull List<Class<?>>                 interfaces
            , @Nonnull IPathExtractor                 methodPathExtractor
            , @Nonnull ISecurityItemExtractor         securityItemExtractor
            , @Nonnull IServiceDescriptionExtractor   serviceDescriptionExtractor
            , @Nonnull IServiceTagExtractor           serviceTagExtractor
            , @Nonnull IOperationSummary              operationSummary
            , @Nonnull IOperationDescriptionExtractor operationDescriptionExtractor
            , @Nonnull IPathParameters                additionalParameters
            , @Nonnull List<Class<?>>                 errorClasses
            , @Nonnull IErrorResponsesExtractor       errorResponsesExtractor
            , @Nonnull IServiceAddListener            serviceAddListener
            , @Nonnull IRequestExamples               requestExamples
            , @Nonnull IResponseExamples              responseExamples
    ) {
        this.interfaces          = createSortedArray(interfaces);
        this.api                 = def ( openApi, new OpenAPI());
        this.methodPathExtractor = def ( methodPathExtractor, (aClass, aMethod) -> "/api/" + aClass.getSimpleName() + "/" + aMethod.getName());
        this.errorClasses        = def ( errorClasses, emptyList());
        this.methodAcceptor      = def ( methodAcceptor, (clazz, aMethod) -> true);
        this.serviceDescription  = def ( serviceDescriptionExtractor, aClass -> Optional.empty());
        this.serviceTag          = def ( serviceTagExtractor, aClass -> Optional.empty());
        this.serviceAddListener  = def ( serviceAddListener, (aPaths, aClass) -> {});
        swaggerMethodPathItem = new SwaggerMethodPathItem(
                  def ( operationDescriptionExtractor, (aPath, aClass, aMethod) -> Optional.empty())
                , this.methodPathExtractor
                , def ( securityItemExtractor, (aClass, aMethod) -> Optional.empty())
                , def ( additionalParameters, (path, clazz, aMethod) -> emptyList())
                , def ( errorResponsesExtractor, (aPath, aClass, aMethod) -> emptyList())
                , def ( requestExamples, (aPath, aClass, aMethod) -> emptyList())
                , def ( responseExamples, (aPath, aClass, aMethod) -> emptyList())
                , serviceTag
                , def ( operationSummary, (aPath, aClass, aMethod) -> Optional.empty())
        );
    }

    private static <T> T def(T aValue, T aDefault) {
        return aValue != null ? aValue : aDefault;
    }

    private static List<Class<?>> createSortedArray(List<Class<?>> aInterfaces) {
        if(aInterfaces == null) {
            throw new IllegalStateException("No any interfaces provided. Did you fill interfaces() method or interfaces parameter?  ");
        }
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
                    LOG.error("Cannot create path items for method {}.{}()", clazz.getSimpleName(), method.getName(), e);
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
                        .name(serviceTag.getServiceTag(trait).orElse(trait.getSimpleName()))
                        .description(serviceDescription.getServiceDescription(trait).orElse(trait.getSimpleName()))
                )
                .collect(toList());
    }

}
