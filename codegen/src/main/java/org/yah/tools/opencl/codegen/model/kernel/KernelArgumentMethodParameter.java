package org.yah.tools.opencl.codegen.model.kernel;

import org.yah.tools.opencl.codegen.parser.ParsedKernelArgument;

public interface KernelArgumentMethodParameter extends KernelMethodParameter {

    ParsedKernelArgument getParsedKernelArgument();

}
