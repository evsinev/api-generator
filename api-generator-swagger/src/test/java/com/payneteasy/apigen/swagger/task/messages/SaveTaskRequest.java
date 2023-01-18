package com.payneteasy.apigen.swagger.task.messages;

import lombok.Data;

@Data
public class SaveTaskRequest {
    long   taskId;
    String taskName;
}
