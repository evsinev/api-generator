package com.payneteasy.apigen.swagger.impl.schema;

import lombok.Data;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(makeFinal = true, level = PRIVATE)
public class PropertyKey implements Comparable<PropertyKey> {
    String className;

    @Override
    public int compareTo(PropertyKey o) {
        return className.compareTo(o.className);
    }
}
