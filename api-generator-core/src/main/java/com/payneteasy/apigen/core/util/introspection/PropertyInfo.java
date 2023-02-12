package com.payneteasy.apigen.core.util.introspection;

import com.payneteasy.apigen.core.util.Fields;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

import static com.payneteasy.apigen.core.util.introspection.PropertyName.createNamed;

public class PropertyInfo implements Comparable<PropertyInfo> {

    private final PropertyName     propertyName;
    private final Class<?>         clazz;
    private final ParametrizedType parametrizedType;

    public PropertyInfo(PropertyName aName, Class<?> aClazz, ParametrizedType aParametrizedType) {
        propertyName     = aName;
        clazz            = aClazz;
        parametrizedType = aParametrizedType;
    }

    public List<PropertyInfo> getProperties() {
        if(getPropertyType() != PropertyType.OBJECT) {
            throw new IllegalStateException("Clazz should be OBJECT but was " + getPropertyType());
        }

        return Fields.getAllFields(clazz)
                .stream()
                .map(PropertyInfo::propertyFromField)
                .collect(Collectors.toList());
    }

    private static PropertyInfo propertyFromField(Field aField) {
        return new PropertyInfo(createNamed(aField.getName()), aField.getType(), ParametrizedType.paraTypeFromField(aField));
    }

    public PropertyType getPropertyType() {
        return PropertyType.OBJECT;
    }

    @Override
    public int compareTo(PropertyInfo aAnother) {
        return propertyName.getPropertyName().compareTo(aAnother.getName().getPropertyName());
    }

    public PropertyName getName() {
        return propertyName;
    }
}
