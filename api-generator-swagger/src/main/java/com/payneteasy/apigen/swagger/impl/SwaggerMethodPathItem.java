package com.payneteasy.apigen.swagger.impl;

import com.payneteasy.apigen.swagger.SwaggerBuilderStrategy.*;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.examples.Example;
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
    private final IRequestExamples               requestExample;
    private final IResponseExamples              responseExamples;
    private final IServiceTagExtractor           serviceTagExtractor;
    private final IOperationSummary              operationSummary;

    public SwaggerMethodPathItem(
              IOperationDescriptionExtractor aOperationDescriptionExtractor
            , IPathExtractor                 pathExtractor
            , ISecurityItemExtractor         securityItemExtractor
            , IPathParameters                aAdditionalParameters
            , IErrorResponsesExtractor       aErrorResponsesExtractor
            , IRequestExamples               aRequestExamples
            , IResponseExamples              aResponseExamples
            , IServiceTagExtractor           aServiceTagExtractor
            , IOperationSummary              aOperationSummary
    ) {
        operationDescriptionExtractor = aOperationDescriptionExtractor;
        this.pathExtractor            = pathExtractor;
        this.securityItemExtractor    = securityItemExtractor;
        additionalParameters          = aAdditionalParameters;
        errorResponsesExtractor       = aErrorResponsesExtractor;
        requestExample                = aRequestExamples;
        responseExamples              = aResponseExamples;
        serviceTagExtractor           = aServiceTagExtractor;
        operationSummary              = aOperationSummary;
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
        operation.addTagsItem(serviceTagExtractor.getServiceTag(clazz).orElse(clazz.getSimpleName()));

        MethodParameters parameters = getParameters(aMethod);
        if(parameters.hasParameters()) {
            operation.requestBody(createRequestBody(parameters, aPath, clazz, aMethod));
        }

        operation.responses(createResponse(aPath, clazz, aMethod));

        operation.summary(operationSummary.getOperationSummary(aPath, clazz, aMethod).orElse(aMethod.getName()));

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


        MediaType successMediaType = new MediaType().schema(schema);
        List<String> responseExamples = this.responseExamples.getResponseExamples(aPath, aClass, aMethod);
        for (String responseExample : responseExamples) {
            successMediaType.addExamples(
                    responseExample
                    , new Example()
                            .$ref(
                                    "#/components/examples/"
                                    + aClass.getSimpleName()
                                    + "."
                                    + aMethod.getName()
                                    + ".Response."
                                    + responseExample
                            )
            );
        }

        ApiResponses responses = new ApiResponses()
            .addApiResponse("200", new ApiResponse()
                .description("Success response")
                .content(new Content()
                    .addMediaType(
                            "application/json"
                            , successMediaType
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

    private RequestBody createRequestBody(MethodParameters aParameters, String aPath, Class<?> aClass, Method aMethod) {
        if(!aParameters.hasParameters()) {
            throw new IllegalStateException("No any parameters");
        }

        // one argument
        if(aParameters.getParameters().size() == 1) {
            MethodParameter methodParameter = aParameters.getParameters().get(0);
            Class<?>        oneArgument     = methodParameter.getType();
            MediaType       mediaType       = new MediaType();

            for (String requestExample : requestExample.getRequestExamples(aPath, aClass, aMethod)) {
                mediaType.addExamples(
                        requestExample
                        , new Example()
                                .$ref(
                                        "#/components/examples/"
                                                + aClass.getSimpleName()
                                                + "."
                                                + aMethod.getName()
                                                + ".Request."
                                                + requestExample
                                )
                );
            }

            return new RequestBody()
                .content(new Content()
                    .addMediaType("application/json", mediaType
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
