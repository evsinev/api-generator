package com.payneteasy.apigen.typescript;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Data
@Builder
@FieldDefaults(makeFinal = true, level = PRIVATE)
public class TypescriptTypeField {
    String fieldName;
    String fieldNullable;
    String fieldType;
}
