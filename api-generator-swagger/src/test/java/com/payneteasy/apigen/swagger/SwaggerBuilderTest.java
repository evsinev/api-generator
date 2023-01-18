package com.payneteasy.apigen.swagger;

import com.payneteasy.apigen.swagger.task.ITaskService;
import io.swagger.v3.oas.models.OpenAPI;
import org.junit.Test;

import java.io.File;
import java.util.Collections;
import java.util.Optional;

public class SwaggerBuilderTest {

    @Test
    public void buildYaml() {
        SwaggerBuilder swaggerBuilder = new SwaggerBuilder(
                new OpenAPI()
                , Collections.singletonList(ITaskService.class)
                , new MarkdownHeaders(new File("src/test/resources/sample-api.md"))
                , (aClass, aMethod) -> "/api/" + aClass.getSimpleName() + "." + aMethod.getName()
                , (aClass, aMethod) -> Optional.empty()
        );
        
        String yaml = swaggerBuilder.buildYaml();

        System.out.println("yaml = " + yaml);

    }
}