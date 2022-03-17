package org.yah.tools.opencl.codegen.model.kernel;

import java.util.Optional;

public interface SetKernelArgumentMethod extends KernelArgumentMethod {

    Optional<KernelMethodParameter> getInvokeParameter();

}
