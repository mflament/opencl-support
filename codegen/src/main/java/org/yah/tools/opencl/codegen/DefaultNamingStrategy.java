package org.yah.tools.opencl.codegen;

import org.yah.tools.opencl.codegen.model.kernel.KernelMethod;
import org.yah.tools.opencl.codegen.model.kernel.KernelMethodParameter;
import org.yah.tools.opencl.codegen.model.kernel.KernelModel;
import org.yah.tools.opencl.codegen.model.kernel.SetKernelArgumentMethod;
import org.yah.tools.opencl.codegen.model.program.ProgramMethod;
import org.yah.tools.opencl.codegen.parser.ParsedKernel;
import org.yah.tools.opencl.codegen.parser.ParsedKernelArgument;
import org.yah.tools.opencl.codegen.parser.ParsedProgram;
import org.yah.tools.opencl.program.CLProgram;

import javax.annotation.Nullable;

import static org.apache.commons.lang3.StringUtils.capitalize;

public class DefaultNamingStrategy implements NamingStrategy {

    public static final class TypeNameDecorator {
        @Nullable
        private final String prefix;
        @Nullable
        private final String suffix;

        public TypeNameDecorator(@Nullable String prefix, @Nullable String suffix) {
            this.prefix = prefix;
            this.suffix = suffix;
        }

        public String decorate(String name) {
            String res = "";
            if (prefix != null) res += prefix;
            res += name;
            if (suffix != null) res += suffix;
            return res;
        }
    }

    private static final DefaultNamingStrategy INSTANCE = new DefaultNamingStrategy(null, null);

    private static final String[] VECTOR_COMPONENT_NAMES = {"x", "y", "z", "w"};

    public static NamingStrategy get() {
        return INSTANCE;
    }

    @Nullable
    private final TypeNameDecorator programNameDecorator;

    @Nullable
    private final TypeNameDecorator kernelNameDecorator;

    public DefaultNamingStrategy(@Nullable TypeNameDecorator programNameDecorator, @Nullable TypeNameDecorator kernelNameDecorator) {
        this.programNameDecorator = programNameDecorator;
        this.kernelNameDecorator = kernelNameDecorator;
    }

    @Override
    public String programName(ParsedProgram program) {
        String name = baseName(program.getFilePath());
        if (!name.toLowerCase().contains("program"))
            name += "Program";
        name = camelCase(name, true);
        if (programNameDecorator != null)
            name = programNameDecorator.decorate(name);
        return name;
    }

    @Override
    public String kernelName(ParsedKernel parsedKernel) {
        String name = parsedKernel.getName();
        if (!name.toLowerCase().endsWith("kernel"))
            name += "Kernel";
        name = camelCase(name, true);
        if (kernelNameDecorator != null)
            name = kernelNameDecorator.decorate(name);
        return name;
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
        } else if (method.isSetValue() || method.isSetValueComponent()) {
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

        if (methodParameter.isValue())
            return "value";

        if (methodParameter.isValueComponent())
            return VECTOR_COMPONENT_NAMES[methodParameter.getParameterIndex()];

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
