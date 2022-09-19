package com.payneteasy.apigen.core.typescript;

import java.util.*;

import static java.util.Comparator.comparing;

public class TypescriptMembers {

    private final Set<TypescriptType> types = new HashSet<>();
    private final Set<TypescriptEnum> enums = new HashSet<>();

    public void addType(TypescriptType aType) {
        types.add(aType);
    }

    public void addEnum(TypescriptEnum aEnum) {
        enums.add(aEnum);
    }

    public List<TypescriptType> getTypes() {
        List<TypescriptType> list = new ArrayList<>(types);
        Collections.sort(list, comparing(TypescriptType::getTypeName));
        return list;
    }

    public List<TypescriptEnum> getEnums() {
        List<TypescriptEnum> list = new ArrayList<>(enums);
        Collections.sort(list, comparing(TypescriptEnum::getEnumName));
        return list;
    }

    public void addAllMembers(TypescriptMembers aMembers) {
        types.addAll(aMembers.types);
        enums.addAll(aMembers.enums);
    }

    public boolean hasType(String aTypename) {
        return types.stream()
                .anyMatch(typescriptType -> typescriptType.getTypeName().equals(aTypename));
    }
}
