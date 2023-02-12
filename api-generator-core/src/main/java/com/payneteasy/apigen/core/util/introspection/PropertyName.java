package com.payneteasy.apigen.core.util.introspection;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(makeFinal = true, level = PRIVATE)
@Builder
public class PropertyName implements Comparable<PropertyName> {

    boolean isRoot;
    String  propertyName;

    public static PropertyName createRootName() {
        return new PropertyName(true, "ROOT");
    }

    public static PropertyName createNamed(String aName) {
        return new PropertyName(false, aName);
    }

    @Override
    public int compareTo(PropertyName o) {
        return propertyName.compareTo(o.propertyName);
    }
}
