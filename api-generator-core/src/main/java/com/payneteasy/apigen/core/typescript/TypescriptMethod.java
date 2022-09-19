package com.payneteasy.apigen.core.typescript;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Data
@Builder
@FieldDefaults(makeFinal = true, level = PRIVATE)
public class TypescriptMethod {
    String methodName;
    String returnType;
    String parameterType;
    String path;

    public boolean hasParameters() {
        return !"VoidRequest".equals(parameterType);
    }
    
    public String getUppercaseName() {
        return Character.toUpperCase(methodName.charAt(0)) + methodName.substring(1);
    }
}
