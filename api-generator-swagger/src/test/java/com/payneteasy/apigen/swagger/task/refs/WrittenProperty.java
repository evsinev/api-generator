package com.payneteasy.apigen.swagger.task.refs;

/** The {@code condition} property is a setter without either a field or a getter. */
public class WrittenProperty {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String aName) {
        name = aName;
    }

    public void setCondition(IRefCondition aCondition) {
        // no field on purpose
    }
}
