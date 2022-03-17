package org.yah.tools.opencl.codegen.model.kernel.methods;

import org.yah.tools.opencl.codegen.model.kernel.KernelModel;
import org.yah.tools.opencl.codegen.model.kernel.SetKernelArgumentMethod;
import org.yah.tools.opencl.codegen.parser.ParsedKernelArgument;

abstract class AbstractSetKernelArgumentMethod extends AbstractKernelArgumentMethod implements SetKernelArgumentMethod {

    public AbstractSetKernelArgumentMethod(KernelModel kernelModel, ParsedKernelArgument parsedKernelArgument) {
        super(kernelModel, parsedKernelArgument);
    }

    @Override
    public boolean isSetKernelArgumentMethod() {
        return true;
    }

    @Override
    public SetKernelArgumentMethod asSetKernelArgumentMethod() {
        return this;
    }

}
