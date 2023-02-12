package com.payneteasy.apigen.swagger.introspector;

import com.payneteasy.apigen.core.util.introspection.PropertyInfo;
import com.payneteasy.apigen.swagger.task.model.TaskInfo;
import org.junit.Test;

import java.util.List;

import static com.payneteasy.apigen.core.util.introspection.ParametrizedType.emptyParametrizedType;
import static com.payneteasy.apigen.core.util.introspection.PropertyName.createRootName;
import static org.assertj.core.api.Assertions.assertThat;

public class IntrospectionTest {

    @Test
    public void test() {
        PropertyInfo       clazz      = new PropertyInfo(createRootName(), TaskInfo.class, emptyParametrizedType());
        List<PropertyInfo> properties = clazz.getProperties();

        assertThat(properties.size()).isEqualTo(2);

        PropertyInfo taskIdProp = properties.get(0);
        assertThat(taskIdProp.getName().getPropertyName()).isEqualTo("taskId");

        PropertyInfo taskNameProp = properties.get(1);
        assertThat(taskNameProp.getName().getPropertyName()).isEqualTo("taskName");


    }
}
