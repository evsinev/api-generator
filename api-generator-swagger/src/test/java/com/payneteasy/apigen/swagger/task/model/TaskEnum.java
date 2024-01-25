package com.payneteasy.apigen.swagger.task.model;

import lombok.Getter;

@Getter
public enum TaskEnum {
    TASK_ONE("1"),
    TASK_TWO("2"),
    TASK_THREE("3"),
    ;

    private final String code;
    TaskEnum(String code) {
        this.code = code;
    }
}
