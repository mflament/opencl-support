package org.yah.tools.opencl.codegen.model;

import java.util.List;
import java.util.stream.Collectors;

public interface MethodModel {

    TypeModel getDeclaringType();

    String getMethodName();

    List<? extends MethodParameterModel> getParameters();

    default List<? extends MethodParameterModel> getRequiredParameters() {
        return getParameters().stream().filter(p -> !p.isOptional()).collect(Collectors.toList());
    }
}
