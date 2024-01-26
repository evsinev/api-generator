package com.payneteasy.apigen.swagger.task;

import com.payneteasy.apigen.swagger.task.model.TaskEnum;
import com.payneteasy.apigen.swagger.task.model.TaskItem;
import com.payneteasy.apigen.swagger.task.model.TaskItemPos;

public interface ITaskServiceBugs {
    void taskItemPos(TaskItemPos[] conditionsOrder);
    void listTasksMulti(TaskItem[] taskItems);
    long[] listTasksMulti(long[] id);
    TaskEnum listTasksMultiTaskEnum(long[] id);
    TaskEnum[] listTasksMultiTaskEnumArray(long[] id);
}
