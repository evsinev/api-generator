package com.payneteasy.apigen.swagger.task.refs;

import java.util.List;

/** Generic model: $ref naming and swagger-core naming disagree on parametrized types. */
public class GenericHolder<T> {

    private T       value;
    private List<T> items;

    public T getValue() {
        return value;
    }

    public void setValue(T aValue) {
        value = aValue;
    }

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> aItems) {
        items = aItems;
    }
}
