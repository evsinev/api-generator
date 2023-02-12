package com.payneteasy.apigen.swagger.impl.schema;


import com.payneteasy.apigen.core.util.introspection.ParametrizedType;
import com.payneteasy.apigen.core.util.introspection.PropertyInfo;
import com.payneteasy.apigen.swagger.task.model.TaskItem;
import io.swagger.v3.oas.models.media.Schema;
import org.junit.Test;

import java.util.Map;

import static com.payneteasy.apigen.core.util.introspection.PropertyName.createRootName;

public class SchemaBuilderTest {

    @Test
    public void test() {
        Map<String, Schema<?>> map = new SchemaBuilder()
                .addProperty(new PropertyInfo(createRootName(), TaskItem.class, ParametrizedType.emptyParametrizedType()))
                .buildSchemaMap();

        
    }
}