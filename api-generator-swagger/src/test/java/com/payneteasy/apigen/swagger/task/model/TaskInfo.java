package com.payneteasy.apigen.swagger.task.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(makeFinal = true, level = PRIVATE)
@Builder
public class TaskInfo {
    @Schema(description = "task id", requiredMode = Schema.RequiredMode.REQUIRED, minimum = "1")
    long   taskId;
    String taskName;
}
