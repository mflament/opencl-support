package org.yah.tools.opencl.codegen.model.kernel;

import org.yah.tools.opencl.codegen.parser.model.ParsedKernelArgument;

public interface KernelArgumentMethodParameter extends KernelMethodParameter {

    ParsedKernelArgument getParsedKernelArgument();

}
