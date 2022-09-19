package com.payneteasy.apigen.core.typescript.example;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.annotation.Nullable;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(makeFinal = true, level = PRIVATE)
@Builder
public class ExampleRequest {
    String fieldString;

    byte    fieldByte;
    int     fieldInt;
    short   fieldShort;
    long    fieldLong;
    float   fieldFloat;
    double  fieldDouble;
    boolean fieldBoolean;

    Byte    fieldLangByte;
    Integer fieldLangInt;
    Short   fieldLangShort;
    Long    fieldLangLong;
    Float   fieldLangFloat;
    Double  fieldLangDouble;
    Boolean fieldLangBoolean;

    ExampleEntity       entity;
    List<ExampleEntity> entities;

    ExampleEnum exampleEnum;

    @Nullable ExampleEntity       nullableEntity;
    @Nullable List<ExampleEntity> nullableEntities;
    @Nullable ExampleEnum         nullableExampleEnum;
}
