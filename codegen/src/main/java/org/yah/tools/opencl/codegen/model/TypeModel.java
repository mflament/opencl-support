package org.yah.tools.opencl.codegen.model;

import java.util.List;

public interface TypeModel {

    String getPackageName();

    String getName();

    List<? extends MethodModel> getMethods();

    default String getQualifiedName() {
        return getPackageName() + "." + getName();
    }
}
