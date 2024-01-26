package com.payneteasy.apigen.swagger;

import com.payneteasy.apigen.swagger.impl.MarkdownHeaders;
import com.payneteasy.apigen.swagger.task.ITaskService;
import com.payneteasy.apigen.swagger.task.ITaskServiceBugs;
import io.swagger.v3.core.util.Yaml;
import io.swagger.v3.oas.models.OpenAPI;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Collections;

import static org.junit.Assert.assertTrue;

public class SwaggerBuilderTest {

    private static final Logger LOG = LoggerFactory.getLogger( SwaggerBuilderTest.class );

    @Test
    public void buildYaml() {
        SwaggerBuilder swaggerBuilder = SwaggerBuilder.builder()
                .openApi(new OpenAPI())
                .methodAcceptor(this::acceptMethod)
                .interfaces(Collections.singletonList(ITaskService.class))
                .methodPathExtractor((aClass, aMethod) -> "/api/" + aClass.getSimpleName() + "." + aMethod.getName())
                .operationDescriptionExtractor(new MarkdownHeaders(new File("src/test/resources/sample-api.md")))
                .build();
        
        OpenAPI openAPI = swaggerBuilder.buildOpenApiModel();
        String  yaml    = Yaml.pretty(openAPI);

        LOG.atDebug()
            .addKeyValue("yaml", yaml)
            .log("Created yaml");

        assertTrue("Class TaskInfo should be in ref", yaml.contains("$ref: '#/components/schemas/TaskInfo'"));
        assertTrue("Class TaskInfo should have schema", yaml.contains("TaskInfo:"));

        assertTrue("Class TaskItem should be in ref", yaml.contains("$ref: '#/components/schemas/TaskItem'"));
        assertTrue("Class TaskItem should have schema", yaml.contains("TaskItem:"));

        assertTrue("Class TaskItem should be in ref", yaml.contains("$ref: '#/components/schemas/TaskItemFromList'"));
        assertTrue("Class TaskItem should have schema", yaml.contains("TaskItemFromList:"));
    }

    @Test
    public void test_fluid() {
        OpenAPI api  = SwaggerBuilder.builder()
                .interfaces(Collections.singletonList(ITaskService.class))
                .build()
                .buildOpenApiModel();
        String  yaml = Yaml.pretty(api);
    }

    @Test
    public void test_bugs() {
        OpenAPI api  = SwaggerBuilder.builder()
                .interfaces(Collections.singletonList(ITaskServiceBugs.class))
                .build()
                .buildOpenApiModel();
        String  yaml = Yaml.pretty(api);
    }

    private boolean acceptMethod(Class<?> aClass, Method method) {
        for (Class<?> type : method.getParameterTypes()) {
            if(InputStream.class.isAssignableFrom(type)) {
                return false;
            }
        }
        return true;
    }
}