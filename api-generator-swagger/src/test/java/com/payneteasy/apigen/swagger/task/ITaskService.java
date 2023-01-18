package com.payneteasy.apigen.swagger.task;

import com.payneteasy.apigen.swagger.task.messages.ListTaskRequest;
import com.payneteasy.apigen.swagger.task.messages.ListTaskResponse;
import com.payneteasy.apigen.swagger.task.messages.SaveTaskRequest;
import com.payneteasy.apigen.swagger.task.messages.VoidResponse;

public interface ITaskService {

    ListTaskResponse listTasks(ListTaskRequest aRequest);

    VoidResponse saveTask(SaveTaskRequest aRequest);

    VoidResponse saveTaskArgs(long aTaskId, String aTaskName);


}
