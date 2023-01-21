package com.payneteasy.apigen.swagger;

import com.payneteasy.apigen.swagger.impl.MarkdownHeaders;
import com.payneteasy.apigen.swagger.task.ITaskService;
import io.swagger.v3.core.util.Yaml;
import io.swagger.v3.oas.models.OpenAPI;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Collections;

import static java.util.Collections.emptyList;
import static java.util.Optional.empty;
import static org.junit.Assert.assertTrue;

public class SwaggerBuilderTest {

    private static final Logger LOG = LoggerFactory.getLogger( SwaggerBuilderTest.class );

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

        LOG.atDebug()
            .addKeyValue("yaml", yaml)
            .log("Created yaml");

        assertTrue("Class TaskInfo should be in ref", yaml.contains("$ref: '#/components/schemas/TaskInfo'"));
        assertTrue("Class TaskInfo should have schema", yaml.contains("TaskInfo:"));

        assertTrue("Class TaskItem should be in ref", yaml.contains("$ref: '#/components/schemas/TaskItem'"));
        assertTrue("Class TaskItem should have schema", yaml.contains("TaskItem:"));
    }
}