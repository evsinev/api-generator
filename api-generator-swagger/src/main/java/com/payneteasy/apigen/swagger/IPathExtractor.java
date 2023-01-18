package com.payneteasy.apigen.swagger;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;

public interface IPathExtractor {

    @Nonnull String getMethodPath(Class<?> aClass, Method aMethod);

}
