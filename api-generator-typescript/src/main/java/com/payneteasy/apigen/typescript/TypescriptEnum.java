package com.payneteasy.apigen.typescript;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Data
@Builder
@FieldDefaults(makeFinal = true, level = PRIVATE)
public class TypescriptEnum {

    String       enumName;
    List<String> enumValues;
}
