package com.payneteasy.apigen.swagger.task.model;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

/**
 * Collection fields whose generic argument is not a plain class: a wildcard, and a generic type of
 * its own. Both shapes are ordinary Java that a service model may declare, and both used to take
 * the whole model down while its fields were being walked.
 */
@Data
@FieldDefaults(makeFinal = true, level = PRIVATE)
@Builder
public class TaskItemHolder {
    String                          holderName;
    /** {@code List<? extends X>} — the item type is the bound of the wildcard. */
    List<? extends TaskItem>        items;
    /**
     * {@code List<Class<? extends X>>} — the argument is generic, so only its raw type could be a
     * schema, and that raw type is not one worth describing. Swagger leaves the field out of the
     * model, which is right; the walk of the remaining fields must go on all the same.
     */
    List<Class<? extends TaskItem>> itemClasses;
    /** Declared last on purpose: it is only described if the two fields above did not end the walk. */
    String                          trailingName;
}
