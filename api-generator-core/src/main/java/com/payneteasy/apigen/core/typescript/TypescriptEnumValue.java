package com.payneteasy.apigen.core.typescript;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Data
@Builder
@FieldDefaults(makeFinal = true, level = PRIVATE)
public class TypescriptEnumValue {
    String name;
    String value;
}
