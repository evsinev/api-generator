package com.payneteasy.apigen.swagger.task.refs;

/**
 * The {@code linkable} property exists as a computed getter only — there is no backing field,
 * so a field-based traversal cannot see it while swagger-core (bean properties) can.
 */
public class ComputedProperty {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String aName) {
        name = aName;
    }

    public IRefLinkable getLinkable() {
        return null;
    }
}
