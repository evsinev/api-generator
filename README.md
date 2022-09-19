# Generates procedures stub

Maven plugin for generating typescript, swagger API from java interfaces.

## How to use

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

## Configuration parameters

| Name          | Default value     | Description                         |
|---------------|-------------------|-------------------------------------|
| prefixSegment | /api              | Prefix for path                     |
| targetDir     | target/procedures | Where to generate stored procedures |

