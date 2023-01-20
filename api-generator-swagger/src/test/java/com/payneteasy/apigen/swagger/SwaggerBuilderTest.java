package com.payneteasy.apigen.swagger;

import com.payneteasy.apigen.swagger.impl.MarkdownHeaders;
import com.payneteasy.apigen.swagger.task.ITaskService;
import io.swagger.v3.core.util.Yaml;
import io.swagger.v3.oas.models.OpenAPI;
import org.junit.Test;

import java.io.File;
import java.util.Collections;

import static java.util.Collections.emptyList;
import static java.util.Optional.empty;

public class SwaggerBuilderTest {

    @Test
    public void buildYaml() {
        SwaggerBuilder swaggerBuilder = new SwaggerBuilder(
                new OpenAPI()
                , Collections.singletonList(ITaskService.class)
                , (aClass, aMethod) -> "/api/" + aClass.getSimpleName() + "." + aMethod.getName()
                , (aClass, aMethod) -> empty()
                , new MarkdownHeaders(new File("src/test/resources/sample-api.md"))
                , (path, clazz, aMethod) -> emptyList()
                , emptyList()
                , (aPath, aClass, aMethod) -> emptyList()
        );

        OpenAPI openAPI = swaggerBuilder.buildOpenApiModel();
        String  yaml    = Yaml.pretty(openAPI);

        System.out.println("yaml = " + yaml);

    }
}