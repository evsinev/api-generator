package com.payneteasy.apigen.typescript;

import com.payneteasy.apigen.core.util.Fields;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.stream.Collectors;

public class JavaClassToTypescriptTypeConverter {

    private final Map<Class<?>, String> map;

    public JavaClassToTypescriptTypeConverter() {
        map = new HashMap<>();
        map.put(String.class , "string");
        map.put(int.class    , "number");
        map.put(Integer.class, "number");
        map.put(Float.class  , "number");
        map.put(float.class  , "number");
        map.put(double.class , "number");
        map.put(Double.class , "number");
        map.put(Long.class   , "number");
        map.put(long.class   , "number");
        map.put(short.class  , "number");
        map.put(Short.class  , "number");
        map.put(byte.class   , "number");
        map.put(Byte.class   , "number");
        map.put(boolean.class, "boolean");
        map.put(Boolean.class, "boolean");
    }

    public TypescriptMembers getTopLevelTypes(List<Class<?>> aClasses) {
        TypescriptMembers members = new TypescriptMembers();
        for (Class<?> clazz : aClasses) {
            members.addAllMembers(getTopLevelTypes(clazz));
        }
        return members;
    }

    public TypescriptMembers getTopLevelTypes(Class<?> aClass) {
        TypescriptMembers members = new TypescriptMembers();
        fillTypes(members, aClass);
        return members;
    }

    private void fillTypes(TypescriptMembers aMembers, Class<?> aClass) {
        if(!isCustomType(aClass)) {
            return;
        }
        
        List<TypescriptTypeField> simpleFields = getSimpleFields(aClass);

        TypescriptType currentType = TypescriptType.builder()
                .typeName(aClass.getSimpleName())
                .fields(simpleFields)
                .build();
        if(aMembers.hasType(currentType.getTypeName())) {
            return;
        }

        aMembers.addType(currentType);

        for (Field field : Fields.getAllFields(aClass)) {

            // list
            Class<?> type = field.getType();
            if (type.isAssignableFrom(List.class)) {
                ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
                Class<?>          stringListClass   = (Class<?>) parameterizedType.getActualTypeArguments()[0];
                fillTypes(aMembers, stringListClass);

            // enum
            } else if (type.isEnum()) {
                aMembers.addEnum(TypescriptEnum.builder()
                        .enumName(type.getSimpleName())
                        .enumValues(Arrays.stream(type.getEnumConstants())
                                .map(Object::toString)
                                .collect(Collectors.toList())
                        )
                        .build()
                );
            } else if(isCustomType(type)) {
                fillTypes(aMembers, type);
            }
        }
    }

    private boolean isCustomType(Class<?> aType) {
        if(aType.isPrimitive()) {
            return false;
        }

        if(aType.isEnum()) {
            return false;
        }

        if(aType.getName().startsWith("[Ljava")) {
            return false;
        }

        if(aType.getName().startsWith("java")) {
            return false;
        }

        return true;
    }

    private List<TypescriptTypeField> getSimpleFields(Class<?> aClass) {
        return Fields.getAllFields(aClass).stream()
                .filter(field -> !Modifier.isStatic(field.getModifiers()))
                .map(field -> TypescriptTypeField.builder()
                        .fieldName(field.getName())
                        .fieldNullable(field.isAnnotationPresent(Nullable.class) ? "?" : "")
                        .fieldType(javaTypeToTypescriptType(field))
                        .build())
                .collect(Collectors.toList());
    }

    private String javaTypeToTypescriptType(Field aField) {
        Class<?> type = aField.getType();

        if (type.isAssignableFrom(List.class)) {
            ParameterizedType parameterizedType = (ParameterizedType) aField.getGenericType();
            Class<?>          stringListClass   = (Class<?>) parameterizedType.getActualTypeArguments()[0];
            return stringListClass.getSimpleName() + "[]";
        }

        return map.getOrDefault(type, type.getSimpleName());
    }

}
