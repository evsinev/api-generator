package com.payneteasy.apigen.swagger.task;

import com.payneteasy.apigen.swagger.task.model.TaskItem;
import com.payneteasy.apigen.swagger.task.refs.ComputedProperty;
import com.payneteasy.apigen.swagger.task.refs.GenericHolder;
import com.payneteasy.apigen.swagger.task.refs.WrittenProperty;

/** Shapes whose $ref used to be emitted without a matching schema. */
public interface ITaskServiceRefs {

    ComputedProperty getComputedProperty();

    void saveWrittenProperty(WrittenProperty aRequest);

    GenericHolder<TaskItem> getGenericHolder();
}
