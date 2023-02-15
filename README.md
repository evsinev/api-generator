# Generates API

* Maven plugin for generating typescript, swagger API from java interfaces.
* Generates swagger API from java sources

## Typescript plugin

### Adding plugin to your pom.xml

```xml
<build>
    <plugins>
        <plugin>
            <groupId>com.payneteasy.api-generator</groupId>
            <artifactId>api-generator-typescript-maven-plugin</artifactId>
            <version>1.0-SNAPSHOT</version>
            <configuration>
                <prefixSegment>/ui/admin/api</prefixSegment>
                <targetDir>target/api-typescript</targetDir>
            </configuration>
            <executions>
                <execution>
                    <goals>
                        <goal>generate</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

### Configuration parameters

| Name          | Default value     | Description                         |
|---------------|-------------------|-------------------------------------|
| prefixSegment | /api              | Prefix for path                     |
| targetDir     | target/procedures | Where to generate stored procedures |
   

## Generates swagger API

### Adding library to your pom.xml

```xml
<dependency>
    <groupId>com.payneteasy.api-generator</groupId>
    <artifactId>api-generator-swagger</artifactId>
    <version>1.0-8</version>
</dependency>
```

### Create yaml

```java
SwaggerBuilder swaggerBuilder = new SwaggerBuilder(
        new OpenAPI()
        , this::acceptMethod
        , Collections.singletonList(ITaskService.class)
        , (aClass, aMethod) -> "/api/" + aClass.getSimpleName() + "." + aMethod.getName()
        , (aClass, aMethod) -> empty()
        , (aClass) -> empty()
        , new MarkdownHeaders(new File("src/test/resources/sample-api.md"))
        , (path, clazz, aMethod) -> emptyList()
        , emptyList()
        , (aPath, aClass, aMethod) -> emptyList()
        , (aPaths, aClass) -> {}
        , (aPath, aClass, aMethod) -> emptyList()
        , (aPath, aClass, aMethod) -> emptyList()
);

OpenAPI openAPI = swaggerBuilder.buildOpenApiModel();
String  yaml    = Yaml.pretty(openAPI);
```

## License

The project is licensed under the Apache License 2.0