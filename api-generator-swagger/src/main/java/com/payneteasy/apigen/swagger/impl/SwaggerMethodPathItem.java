package com.payneteasy.apigen.swagger.impl;

import com.payneteasy.apigen.swagger.IAdditionalParameters;
import com.payneteasy.apigen.swagger.IOperationDescriptionExtractor;
import com.payneteasy.apigen.swagger.IPathExtractor;
import com.payneteasy.apigen.swagger.ISecurityItemExtractor;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;

import javax.validation.constraints.NotNull;
import java.lang.reflect.Method;


public class SwaggerMethodPathItem {

    private final IPathExtractor                 pathExtractor;
    private final ISecurityItemExtractor         securityItemExtractor;
    private final IOperationDescriptionExtractor operationDescriptionExtractor;
    private final IAdditionalParameters          additionalParameters;

    public SwaggerMethodPathItem(
              IOperationDescriptionExtractor aOperationDescriptionExtractor
            , IPathExtractor                 pathExtractor
            , ISecurityItemExtractor         securityItemExtractor
            , IAdditionalParameters          aAdditionalParameters
    ) {
        operationDescriptionExtractor = aOperationDescriptionExtractor;
        this.pathExtractor            = pathExtractor;
        this.securityItemExtractor    = securityItemExtractor;
        additionalParameters          = aAdditionalParameters;
    }

    public PathItem createPathItem(Class<?> clazz, Method aMethod) {
        String    path      = pathExtractor.getMethodPath(clazz, aMethod);
        PathItem  item      = new PathItem();
        Operation operation = getOperation(clazz, aMethod);

        item.operation(PathItem.HttpMethod.POST, operation);

        securityItemExtractor
                .getSecurityItem(clazz, aMethod)
                .ifPresent(operation::addSecurityItem);

        operationDescriptionExtractor
                .getOperationDescription(path, clazz, aMethod)
                .ifPresent(operation::description);

        additionalParameters
                .getAdditionalParameters(path, clazz, aMethod)
                .ifPresent(parameters -> parameters.forEach(item::addParametersItem));

        return item;
    }

    @NotNull
    private Operation getOperation(Class<?> clazz, Method aMethod) {
        Operation operation = new Operation();
        operation.addTagsItem(clazz.getSimpleName());

        if(!"VoidRequest".equals(getParameterName(aMethod))){
            operation.requestBody(createRequestBody(aMethod));
        }

        operation.responses(createResponse(aMethod));

        return operation;
    }

    private ApiResponses createResponse(Method aMethod) {
        return new ApiResponses()
                .addApiResponse("200", new ApiResponse()
                        .description("Success response")
                        .content(new Content()
                                .addMediaType("application/json", new MediaType()
                                        .schema(new Schema().$ref("#/components/schemas/" + aMethod.getReturnType().getSimpleName()))
                                )
                        )
                );
        // todo add non-200 responses
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

    @NotNull
    private String getParameterName(Method aMethod) {
        return aMethod.getParameterTypes()[0].getSimpleName();
    }
}
