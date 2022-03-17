package org.yah.tools.opencl.codegen;

import org.yah.tools.opencl.codegen.model.kernel.KernelMethod;
import org.yah.tools.opencl.codegen.model.kernel.KernelMethodParameter;
import org.yah.tools.opencl.codegen.model.kernel.KernelModel;
import org.yah.tools.opencl.codegen.model.kernel.SetKernelArgumentMethod;
import org.yah.tools.opencl.codegen.model.program.ProgramMethod;
import org.yah.tools.opencl.codegen.parser.model.ParsedKernel;
import org.yah.tools.opencl.codegen.parser.model.ParsedKernelArgument;
import org.yah.tools.opencl.codegen.parser.model.ParsedProgram;
import org.yah.tools.opencl.program.CLProgram;

import static org.apache.commons.lang3.StringUtils.capitalize;

public class DefaultNamingStrategy implements NamingStrategy {
    private static final DefaultNamingStrategy INSTANCE = new DefaultNamingStrategy();

    private static final String[] VECTOR_ARGS_NAME = {"x", "y", "z", "w"};

    public static NamingStrategy get() {
        return INSTANCE;
    }

    private DefaultNamingStrategy() {
    }

    @Override
    public String programName(ParsedProgram program) {
        String name = baseName(program.getFilePath());
        if (!name.toLowerCase().contains("program"))
            name += "Program";
        return camelCase(name, true);
    }

    @Override
    public String kernelName(ParsedKernel parsedKernel) {
        String name = parsedKernel.getName();
        if (!name.toLowerCase().endsWith("kernel"))
            name += "Kernel";
        return camelCase(name, true);
    }

    @Override
    public String programMethodName(ProgramMethod programMethod) {
        if (programMethod.isCreateKernel()) {
            KernelModel kernelModel = programMethod.asCreateKernel().getKernelModel();
            String kernelName = kernelModel.getName();
            return "create" + capitalize(kernelName);
        } else if (programMethod.isCloseProgram()) {
            return "close";
        }

        throw new IllegalArgumentException("Invalid parameter " + programMethod);
    }

    @Override
    public String kernelMethodName(KernelMethod method) {
        String name;
        if (method.isWriteBuffer()) {
            ParsedKernelArgument parsedKernelArgument = method.asKernelArgumentMethod().getParsedKernelArgument();
            name = "update" + camelCase(parsedKernelArgument.getArgName(), true);
        } else if (method.isReadBuffer()) {
            ParsedKernelArgument parsedKernelArgument = method.asKernelArgumentMethod().getParsedKernelArgument();
            name = "read" + camelCase(parsedKernelArgument.getArgName(), true);
        } else if (method.isCreateBuffer()) {
            ParsedKernelArgument parsedKernelArgument = method.asKernelArgumentMethod().getParsedKernelArgument();
            name = "create" + camelCase(parsedKernelArgument.getArgName(), true);
        } else if (method.isSetValue()) {
            ParsedKernelArgument parsedKernelArgument = method.asKernelArgumentMethod().getParsedKernelArgument();
            name = "set" + camelCase(parsedKernelArgument.getArgName(), true);
        } else if (method.isInvoke()) {
            name = "invoke";
        } else if (method.isSetLocalSize()) {
            ParsedKernelArgument parsedKernelArgument = method.asKernelArgumentMethod().getParsedKernelArgument();
            name = "set" + camelCase(parsedKernelArgument.getArgName(), true) + "Size";
        } else {
            throw new IllegalArgumentException("Invalid method " + method);
        }

        return name;
    }

    @Override
    public String methodParameterName(KernelMethodParameter methodParameter) {
        if (methodParameter.isInvokeRangeParameter())
            return "range";

        if (methodParameter.isInvokeArgument()) {
            SetKernelArgumentMethod argumentSetterMethod = methodParameter.asInvokeArgument().getSetKernelArgumentMethod();
            String name = camelCase(argumentSetterMethod.getParsedKernelArgument().getArgName(), false);
            String parameterName = argumentSetterMethod.getInvokeParameter()
                    .map(KernelMethodParameter::getParameterName)
                    .orElseThrow(IllegalArgumentException::new);
            return name + capitalize(parameterName);
        }

        if (methodParameter.isBuffer())
            return "buffer";

        if (methodParameter.isBufferSize())
            return "size";

        if (methodParameter.isBufferOffset())
            return "offset";

        if (methodParameter.isEventBuffer())
            return "event";

        if (methodParameter.isBufferProperties())
            return "bufferProperties";

        if (methodParameter.isValue()) {
            int size = methodParameter.getMethod().getParameters().size();
            if (size == 1)
                return "value";
            return VECTOR_ARGS_NAME[methodParameter.getParameterIndex()];
        }

        throw new IllegalArgumentException("Invalid parameter " + methodParameter.getClass().getName());
    }

    @Override
    public String fieldName(ParsedKernelArgument argument) {
        return camelCase(argument.getArgName(), false);
    }

    private String camelCase(String argName, boolean capitalize) {
        StringBuilder sb = new StringBuilder(argName.length());
        for (int i = 0; i < argName.length(); i++) {
            char c = argName.charAt(i);
            if (c == '_') {
                capitalize = true;
                continue;
            }
            if (Character.isUpperCase(c))
                capitalize = false;
            else if (capitalize) {
                c = Character.toUpperCase(c);
                capitalize = false;
            }
            sb.append(c);
        }
        return sb.toString();
    }

    private static String baseName(String filePath) {
        String path = CLProgram.getProgramPath(filePath);
        int index = path.lastIndexOf('/');
        if (index >= 0)
            path = path.substring(index + 1);
        index = path.lastIndexOf('.');
        if (index > 0)
            path = path.substring(0, index);
        return path;
    }
}
