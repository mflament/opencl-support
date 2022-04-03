package org.yah.tools.opencl.codegen.builder;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.type.TypeParameter;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class InterfaceBuilder extends JavaTypeBuilder {

    public InterfaceBuilder(String packageName, String simpleName) {
        this(packageName, simpleName, Collections.emptyList());
    }

    public InterfaceBuilder(String packageName, String simpleName, Collection<String> typeParameters) {
        super(packageName, simpleName);
        declaration.setInterface(true);
    }

    public <C> InterfaceMethodBuilder addMethod(String methodName) {
        return new InterfaceMethodBuilder(declaration.addMethod(methodName));
    }

    public void makeAutoCloseable() {
        declaration.addExtendedType(AutoCloseable.class);
        addMethod("close").addMarkerAnnotation(Override.class);
    }

}
