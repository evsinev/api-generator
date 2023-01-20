package com.payneteasy.apigen.swagger;

import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

public interface SwaggerBuilderStrategy {

    interface IAdditionalParameters {
        List<Parameter> getAdditionalParameters(String path, Class<?> clazz, Method aMethod);
    }

    interface IErrorResponsesExtractor {
        List<ErrorResponseInfo> getErrorResponse(String aPath, Class<?> aClass, Method aMethod);
    }

    interface IOperationDescriptionExtractor {
        Optional<String> getOperationDescription(String aPath, Class<?> aClass, Method aMethod);
    }

    interface IPathExtractor {
        @Nonnull
        String getMethodPath(Class<?> aClass, Method aMethod);
    }

    interface ISecurityItemExtractor {
        Optional<SecurityRequirement> getSecurityItem(Class<?> aClass, Method aMethod);
    }

    @Data
    @FieldDefaults(makeFinal = true, level = PRIVATE)
    @Builder
    class ErrorResponseInfo {
        int            httpStatusCode;
        String         description;
        Class<?>       responseClass;

        public String getName() {
            return String.valueOf(httpStatusCode);
        }
    }

}
