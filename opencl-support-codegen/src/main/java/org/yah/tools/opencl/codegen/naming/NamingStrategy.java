package org.yah.tools.opencl.codegen.naming;

import org.yah.tools.opencl.codegen.parser.ParsedKernel;
import org.yah.tools.opencl.codegen.parser.ParsedKernelArgument;
import org.yah.tools.opencl.codegen.parser.ParsedProgram;
import org.yah.tools.opencl.codegen.parser.type.CLType;

import java.util.Map;

public interface NamingStrategy {

    ProgramNamingStrategy program(ParsedProgram parsedProgram);

    interface ProgramNamingStrategy {

        String interfaceName();

        String createKernel(ParsedKernel parsedKernel);

        KernelNamingStrategy kernel(ParsedKernel parsedKernel);

        ProgramImplementationNamingStrategy clImplementation(Map<String, CLType> typeArguments);

    }

    interface KernelNamingStrategy {

        String kernelPackage(String basePackage);

        String interfaceName();

        //String clImplementationName(Map<String, JavaTypeArgument> args);

        MethodNamingStrategy createBufferWithSize(ParsedKernelArgument kernelArgument);

        MethodNamingStrategy createBufferWithBuffer(ParsedKernelArgument kernelArgument);

        MethodNamingStrategy setLocalBufferSize(ParsedKernelArgument kernelArgument);

        MethodNamingStrategy writeBuffer(ParsedKernelArgument kernelArgument);

        MethodNamingStrategy readBuffer(ParsedKernelArgument kernelArgument);

        MethodNamingStrategy setValue(ParsedKernelArgument kernelArgument);

        int maxVectorComponentParameters();

        MethodNamingStrategy setSetVectorComponents(ParsedKernelArgument kernelArgument);

        MethodNamingStrategy invokeMethod();

    }

    interface MethodNamingStrategy {

        String methodName();

        String parameterName(int index);

    }

    interface ImplementationNamingStrategy {

        String packageName(String basePackage);

        String className();

    }

    interface ProgramImplementationNamingStrategy extends ImplementationNamingStrategy {

        KernelImplementationNamingStrategy kernelImplementation(ParsedKernel kernel);

    }

    interface KernelImplementationNamingStrategy extends ImplementationNamingStrategy {

        String bufferFieldName(ParsedKernelArgument kernelArgument);

    }
}
