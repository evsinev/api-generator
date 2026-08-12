package com.payneteasy.apigen.swagger.task;

import com.payneteasy.apigen.swagger.task.external.ExternalHolder;
import com.payneteasy.apigen.swagger.task.external.PretendFrameworkType;

public interface ITaskServiceExternal {

    ExternalHolder getHolder();

    PretendFrameworkType getFrameworkTypeDirectly();
}
