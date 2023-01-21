package com.payneteasy.apigen.swagger.impl;

import com.payneteasy.apigen.swagger.SwaggerBuilderStrategy;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

import static com.payneteasy.apigen.swagger.impl.SwaggerSchemas.createSchema;


public class SwaggerMethodPathItem {

    private final SwaggerBuilderStrategy.IPathExtractor                 pathExtractor;
    private final SwaggerBuilderStrategy.ISecurityItemExtractor         securityItemExtractor;
    private final SwaggerBuilderStrategy.IOperationDescriptionExtractor operationDescriptionExtractor;
    private final SwaggerBuilderStrategy.IAdditionalParameters          additionalParameters;
    private final SwaggerBuilderStrategy.IErrorResponsesExtractor       errorResponsesExtractor;

    public SwaggerMethodPathItem(
              SwaggerBuilderStrategy.IOperationDescriptionExtractor aOperationDescriptionExtractor
            , SwaggerBuilderStrategy.IPathExtractor pathExtractor
            , SwaggerBuilderStrategy.ISecurityItemExtractor securityItemExtractor
            , SwaggerBuilderStrategy.IAdditionalParameters aAdditionalParameters
            , SwaggerBuilderStrategy.IErrorResponsesExtractor aErrorResponsesExtractor
    ) {
        operationDescriptionExtractor = aOperationDescriptionExtractor;
        this.pathExtractor            = pathExtractor;
        this.securityItemExtractor    = securityItemExtractor;
        additionalParameters          = aAdditionalParameters;
        errorResponsesExtractor       = aErrorResponsesExtractor;
    }

    public PathItem createPathItem(Class<?> clazz, Method aMethod) {
        String    path      = pathExtractor.getMethodPath(clazz, aMethod);
        PathItem  item      = new PathItem();
        Operation operation = getOperation(path, clazz, aMethod);

        item.operation(PathItem.HttpMethod.POST, operation);

        securityItemExtractor
                .getSecurityItem(clazz, aMethod)
                .ifPresent(operation::addSecurityItem);

        operationDescriptionExtractor
                .getOperationDescription(path, clazz, aMethod)
                .ifPresent(operation::description);

        additionalParameters
                .getAdditionalParameters(path, clazz, aMethod)
                .forEach(item::addParametersItem);

        return item;
    }

    @Nonnull
    private Operation getOperation(String aPath, Class<?> clazz, Method aMethod) {
        Operation operation = new Operation();
        operation.addTagsItem(clazz.getSimpleName());

        MethodParameters parameters = getParameters(aMethod);
        if(parameters.hasParameters()) {
            operation.requestBody(createRequestBody(parameters));
        }

        operation.responses(createResponse(aPath, clazz, aMethod));

        return operation;
    }

    private ApiResponses createResponse(String aPath, Class<?> aClass, Method aMethod) {

        Schema<?> schema = void.class.equals(aMethod.getReturnType())
            ? null
            : createSchema(aMethod.getReturnType(), "Return for " + aClass.getSimpleName() + "." + aMethod.getName() + "()");

        ApiResponses responses = new ApiResponses()
            .addApiResponse("200", new ApiResponse()
                .description("Success response")
                .content(new Content()
                    .addMediaType("application/json", new MediaType()
                        .schema(schema)
                    )
                )
            );

        errorResponsesExtractor
            .getErrorResponse(aPath, aClass, aMethod)
            .forEach(error -> responses
                .addApiResponse(
                    error.getName()
                    , new ApiResponse()
                        .description(error.getDescription())
                        .content(errorJson(error.getResponseClass()))
                )
            );

        return responses;
    }

    private Content errorJson(Class<?> aClass) {
        return new Content()
                .addMediaType("application/json; charset=utf-8", new MediaType()
                        .schema(createSchema(aClass, "Error class " + aClass.getSimpleName()))
                );
    }

    private RequestBody createRequestBody(MethodParameters aParameters) {
        if(!aParameters.hasParameters()) {
            throw new IllegalStateException("No any parameters");
        }

        if(aParameters.getParameters().size() == 1) {
            Class<?> oneArgument = aParameters.getParameters().get(0).getType();
            return new RequestBody()
                .content(new Content()
                    .addMediaType("application/json", new MediaType()
                        .schema(createSchema(oneArgument, "One argument for method "))
                    )
                );
        }

        // todo create array of parameters
        return null;
    }

    @Nonnull
    private MethodParameters getParameters(Method aMethod) {
        List<MethodParameter> args       = new ArrayList<>();
        Parameter[]           parameters = aMethod.getParameters();

        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            args.add(MethodParameter.builder()
                .typeName ( parameter.getType().getSimpleName())
                .type     ( parameter.getType() )
                .index    ( i                   )
                .name     ( parameter.getName() ) // compiler arguments should have '-parameters'
                .build()
            );
        }

        return new MethodParameters(args);
    }
}
