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
            <version>1.0-12</version>
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
    <version>1.0-9</version>
</dependency>
```

### Create yaml

```java
OpenAPI api = SwaggerBuilder.builder()
    .interfaces(Collections.singletonList(ITaskService.class))
    .build()
    .buildOpenApiModel();

String yaml = Yaml.pretty(api);
```

## License

The project is licensed under the Apache License 2.0
