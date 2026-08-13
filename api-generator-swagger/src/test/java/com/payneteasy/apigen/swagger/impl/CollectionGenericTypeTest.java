package com.payneteasy.apigen.swagger.impl;

import com.payneteasy.apigen.swagger.task.model.TaskItem;
import com.payneteasy.apigen.swagger.task.model.TaskItemHolder;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The element type of a collection field. A field declares the same shapes a method signature does —
 * a wildcard, or an argument that is generic itself — and reading it any other way ends the walk of
 * the model it was found in: the type is logged as unsupported and stepped over, so the fields behind
 * it are never visited and whatever only they referenced never reaches the components.
 */
public class CollectionGenericTypeTest {

    @Test
    public void plainArgument() throws NoSuchFieldException {
        assertThat(elementTypeOf(Plain.class, "items")).isEqualTo(TaskItem.class);
    }

    /** {@code List<? extends TaskItem>}: the bound is what a caller may put in. */
    @Test
    public void wildcardArgument() throws NoSuchFieldException {
        assertThat(elementTypeOf(TaskItemHolder.class, "items")).isEqualTo(TaskItem.class);
    }

    /** {@code List<Class<? extends TaskItem>>}: only the raw type of the argument can be a schema. */
    @Test
    public void genericArgument() throws NoSuchFieldException {
        assertThat(elementTypeOf(TaskItemHolder.class, "itemClasses")).isEqualTo(Class.class);
    }

    private static Class<?> elementTypeOf(Class<?> aClass, String aFieldName) throws NoSuchFieldException {
        Field field = aClass.getDeclaredField(aFieldName);
        return SwaggerSchemas.getCollectionGenericType(field);
    }

    private static class Plain {
        List<TaskItem> items;
    }
}
