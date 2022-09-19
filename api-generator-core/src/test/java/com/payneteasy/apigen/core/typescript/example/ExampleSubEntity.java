package com.payneteasy.apigen.core.typescript.example;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(makeFinal = true, level = PRIVATE)
@Builder
public class ExampleSubEntity {
    String      subEntityName;
    ExampleEnum exampleEnum;

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

}
