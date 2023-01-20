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


public class SwaggerMethodPathItem {

    private final SwaggerBuilderStrategy.IPathExtractor                 pathExtractor;
    private final SwaggerBuilderStrategy.ISecurityItemExtractor         securityItemExtractor;
    private final SwaggerBuilderStrategy.IOperationDescriptionExtractor operationDescriptionExtractor;
    private final SwaggerBuilderStrategy.IAdditionalParameters          additionalParameters;
    private final SwaggerBuilderStrategy.IErrorResponsesExtractor errorResponsesExtractor;

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

        if(!"VoidRequest".equals(getParameterName(aMethod))){
            operation.requestBody(createRequestBody(aMethod));
        }

        operation.responses(createResponse(aPath, clazz, aMethod));

        return operation;
    }

    private ApiResponses createResponse(String aPath, Class<?> aClass, Method aMethod) {
        ApiResponses responses = new ApiResponses()
        .addApiResponse("200", new ApiResponse()
            .description("Success response")
            .content(new Content()
                .addMediaType("application/json", new MediaType()
                    .schema(new Schema().$ref("#/components/schemas/" + aMethod.getReturnType().getSimpleName()))
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
                        .schema(new Schema().$ref("#/components/schemas/" + aClass.getSimpleName()))
                );
    }

    private RequestBody createRequestBody(Method aMethod) {
        return new RequestBody()
                .content(new Content()
                        .addMediaType("application/json", new MediaType()
                                .schema(new Schema().$ref("#/components/schemas/" + getParameterName(aMethod)))
                        )
                );
    }

    @Nonnull
    private String getParameterName(Method aMethod) {
        return aMethod.getParameterTypes()[0].getSimpleName();
    }
}
