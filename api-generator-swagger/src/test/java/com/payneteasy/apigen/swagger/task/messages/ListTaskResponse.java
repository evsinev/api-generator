package com.payneteasy.apigen.swagger.task.messages;

import com.payneteasy.apigen.swagger.task.model.TaskInfo;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(makeFinal = true, level = PRIVATE)
@Builder
public class ListTaskResponse {
    List<TaskInfo> tasks;
}
