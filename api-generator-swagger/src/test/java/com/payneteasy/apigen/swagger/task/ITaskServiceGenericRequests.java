package com.payneteasy.apigen.swagger.task;

import com.payneteasy.apigen.swagger.task.model.TaskItem;

import java.util.List;

/** Requests that are a collection rather than a request object. */
public interface ITaskServiceGenericRequests {
    void listOfItems(List<TaskItem> items);
    void listOfLongs(List<Long> ids);
}
