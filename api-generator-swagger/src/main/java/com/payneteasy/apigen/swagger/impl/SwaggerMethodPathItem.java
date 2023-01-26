package com.payneteasy.apigen.swagger.impl;

import com.payneteasy.apigen.swagger.SwaggerBuilderStrategy.*;
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

    private final IPathExtractor                 pathExtractor;
    private final ISecurityItemExtractor         securityItemExtractor;
    private final IOperationDescriptionExtractor operationDescriptionExtractor;
    private final IPathParameters                additionalParameters;
    private final IErrorResponsesExtractor       errorResponsesExtractor;

    public SwaggerMethodPathItem(
              IServiceDescriptionExtractor   aServiceDescriptionExtractor
            , IOperationDescriptionExtractor aOperationDescriptionExtractor
            , IPathExtractor                 pathExtractor
            , ISecurityItemExtractor         securityItemExtractor
            , IPathParameters aAdditionalParameters
            , IErrorResponsesExtractor       aErrorResponsesExtractor
    ) {
        operationDescriptionExtractor = aOperationDescriptionExtractor;
        this.pathExtractor            = pathExtractor;
        this.securityItemExtractor    = securityItemExtractor;
        additionalParameters          = aAdditionalParameters;
        errorResponsesExtractor       = aErrorResponsesExtractor;
    }

    public PathItem createPathItem(Class<?> clazz, Method aMethod) {
        String    path      = pathExtractor.getMethodPath(clazz, aMethod);
        Operation operation = createOperation(path, clazz, aMethod);

        PathItem item = new PathItem();
        item.operation(PathItem.HttpMethod.POST, operation);

        additionalParameters
            .getPathParameters(path, clazz, aMethod)
            .forEach(item::addParametersItem);

        return item;
    }

    @Nonnull
    private Operation createOperation(String aPath, Class<?> clazz, Method aMethod) {
        Operation operation = new Operation();
        operation.addTagsItem(clazz.getSimpleName());

        MethodParameters parameters = getParameters(aMethod);
        if(parameters.hasParameters()) {
            operation.requestBody(createRequestBody(parameters));
        }

        operation.responses(createResponse(aPath, clazz, aMethod));

        operation.summary(aMethod.getName());

        securityItemExtractor
                .getSecurityItem(clazz, aMethod)
                .ifPresent(operation::addSecurityItem);

        operationDescriptionExtractor
                .getOperationDescription(aPath, clazz, aMethod)
                .ifPresent(operation::description);

        return operation;
    }

    private ApiResponses createResponse(String aPath, Class<?> aClass, Method aMethod) {

        Schema<?> schema = void.class.equals(aMethod.getReturnType())
            ? null
            : createSchema(aMethod.getReturnType(), aMethod.getGenericReturnType(), "Return for " + aClass.getSimpleName() + "." + aMethod.getName() + "()");

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

        // one argument
        if(aParameters.getParameters().size() == 1) {
            MethodParameter methodParameter = aParameters.getParameters().get(0);
            Class<?>        oneArgument     = methodParameter.getType();
            return new RequestBody()
                .content(new Content()
                    .addMediaType("application/json", new MediaType()
                        .schema(
                                createSchema(oneArgument, "One argument for method ")
                                    .description(methodParameter.getName())
                        )

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
