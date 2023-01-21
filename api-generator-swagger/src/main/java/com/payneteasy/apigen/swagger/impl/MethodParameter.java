package com.payneteasy.apigen.swagger.impl;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(makeFinal = true, level = PRIVATE)
@Builder
public class MethodParameter {
    int      index;
    String   name;
    String   typeName;
    Class<?> type;
}
