package com.payneteasy.apigen.swagger;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;


public interface IErrorResponsesExtractor {

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

    List<ErrorResponseInfo> getErrorResponse(String aPath, Class<?> aClass, Method aMethod);

}
