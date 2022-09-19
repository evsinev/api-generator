package com.payneteasy.apigen.core.typescript.example;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(makeFinal = true, level = PRIVATE)
@Builder
public class ExampleEntity {
    String                 entityName;
    ExampleSubEntity       subEntity;
    List<ExampleSubEntity> subEntities;

    ExampleEnum exampleEnum;

}
