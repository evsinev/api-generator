package com.payneteasy.apigen.core.typescript;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Data
@Builder
@FieldDefaults(makeFinal = true, level = PRIVATE)
public class TypescriptType {
    String                    typeName;
    List<TypescriptTypeField> fields;
}
