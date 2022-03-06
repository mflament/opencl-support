package org.yah.tools.opencl.codegen.generator;

import org.yah.tools.opencl.codegen.generator.model.GeneratedKernelInterface;
import org.yah.tools.opencl.codegen.generator.model.KernelMethodType;
import org.yah.tools.opencl.codegen.parser.model.ParsedKernel;
import org.yah.tools.opencl.codegen.parser.model.ParsedKernelArgument;
import org.yah.tools.opencl.codegen.parser.model.ParsedProgram;

import javax.annotation.Nullable;

public interface NamingStrategy {

    String programName(ParsedProgram program);

    String kernelName(ParsedKernel parsedKernel);

    String getKernelMethodName(GeneratedKernelInterface kernelInterface);

    String getMehodName(KernelMethodType methodType, @Nullable ParsedKernelArgument argument);

    String getParameterName(KernelMethodType methodType, ParsedKernelArgument argument, int parameterIndex);
}
