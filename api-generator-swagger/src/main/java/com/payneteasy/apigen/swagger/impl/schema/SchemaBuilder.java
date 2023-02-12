package com.payneteasy.apigen.swagger.impl.schema;

import com.payneteasy.apigen.core.util.introspection.PropertyInfo;
import com.payneteasy.apigen.core.util.introspection.PropertyName;
import io.swagger.v3.oas.models.media.Schema;

import java.util.*;

public class SchemaBuilder {

    private final List<PropertyInfo>           properties = new ArrayList<>();
    private final Map<PropertyName, Schema<?>> schemas    = new TreeMap<>();

    public SchemaBuilder addProperty(PropertyInfo aProperty) {
        properties.add(aProperty);
        return this;
    }

    public Map<String, Schema<?>> buildSchemaMap() {
        for (PropertyInfo property : properties) {
            processProperty(property);
        }
        return Collections.emptyMap();
    }

    private void processProperty(PropertyInfo aProperty) {
        switch (aProperty.getPropertyType()) {
            case OBJECT:
                processObjectProperty(aProperty);
                break;

            default:
                throw new IllegalStateException("unknown type" + aProperty.getPropertyType());
        }
    }

    private void processObjectProperty(PropertyInfo aProperty) {
//        Schema<?> schema =
//        schemas.put(aProperty.getName(), createObjectSchema(aProperty));
    }

}
