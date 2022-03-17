package org.yah.tools.opencl.codegen.model;

import javax.annotation.Nullable;

public interface MethodParameterModel {

    MethodModel getMethod();

    int getParameterIndex();

    String getParameterName();

    Class<?> getParameterType();

    @Nullable
    default String getDefaultValue() {
        return null;
    }

    default boolean isOptional() {
        return getDefaultValue() != null;
    }
}
