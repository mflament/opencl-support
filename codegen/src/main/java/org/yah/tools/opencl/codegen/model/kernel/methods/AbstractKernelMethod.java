package org.yah.tools.opencl.codegen.model.kernel.methods;

import org.yah.tools.opencl.codegen.NamingStrategy;
import org.yah.tools.opencl.codegen.model.kernel.KernelMethod;
import org.yah.tools.opencl.codegen.model.kernel.KernelMethodParameter;
import org.yah.tools.opencl.codegen.model.kernel.KernelModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

abstract class AbstractKernelMethod implements KernelMethod {

    protected final KernelModel kernelModel;

    private String methodName;

    protected final List<KernelMethodParameter> parameters = new ArrayList<>();

    public AbstractKernelMethod(KernelModel kernelModel) {
        this.kernelModel = Objects.requireNonNull(kernelModel, "kernelModel is null");
    }

    @Override
    public KernelModel getDeclaringType() {
        return kernelModel;
    }

    @Override
    public String getMethodName() {
        if (methodName == null)
            methodName = getNamingStrategy().kernelMethodName(this);
        return methodName;
    }

    @Override
    public List<? extends KernelMethodParameter> getParameters() {
        return parameters;
    }

    @Override
    public String toString() {
        String parameters = this.parameters.stream().map(Object::toString).collect(Collectors.joining(", "));
        return String.format("%s.%s(%s)", kernelModel.getName(), methodName, parameters);
    }

    protected final NamingStrategy getNamingStrategy() {
        return kernelModel.getNamingStrategy();
    }

}
