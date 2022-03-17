package org.yah.tools.opencl.codegen;

import org.yah.tools.opencl.codegen.model.kernel.KernelMethod;
import org.yah.tools.opencl.codegen.model.kernel.KernelMethodParameter;
import org.yah.tools.opencl.codegen.model.program.ProgramMethod;
import org.yah.tools.opencl.codegen.parser.model.ParsedKernel;
import org.yah.tools.opencl.codegen.parser.model.ParsedKernelArgument;
import org.yah.tools.opencl.codegen.parser.model.ParsedProgram;

public interface NamingStrategy {

    String programName(ParsedProgram program);

    String programMethodName(ProgramMethod programMethod);

    String kernelName(ParsedKernel parsedKernel);

    String kernelMethodName(KernelMethod method);

    String methodParameterName(KernelMethodParameter methodParameter);

    String fieldName(ParsedKernelArgument argument);

}
