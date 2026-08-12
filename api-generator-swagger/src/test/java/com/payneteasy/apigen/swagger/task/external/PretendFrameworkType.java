package com.payneteasy.apigen.swagger.task.external;

/** Stands in for a framework type (org.springframework.*, org.joda.*, sun.*) in tests. */
public class PretendFrameworkType {

    private String  internalName;
    private Nested  nested;

    public String getInternalName() {
        return internalName;
    }

    public Nested getNested() {
        return nested;
    }

    public static class Nested {
        private String deep;

        public String getDeep() {
            return deep;
        }
    }
}
