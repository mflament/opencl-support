package org.yah.tools.opencl.codegen.generator;

import org.yah.tools.opencl.codegen.generator.kernel.MethodKind;
import org.yah.tools.opencl.codegen.generator.kernel.ParameterKind;
import org.yah.tools.opencl.codegen.generator.model.old.GeneratedKernelArgument;
import org.yah.tools.opencl.codegen.parser.model.ParsedKernelArgument;
import org.yah.tools.opencl.codegen.parser.model.ParsedProgram;

import javax.annotation.Nullable;
import java.util.Objects;

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
        String name = program.getMetadata().getBaseName();
        if (!name.toLowerCase().endsWith("program"))
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
    public String kernelArgumentName(ParsedKernelArgument argument) {
        return camelCase(argument.getArgName(), false);
    }

    @Override
    public String methodName(int kinds, @Nullable GeneratedKernelArgument argument) {
        String name;
        if (MethodKind.contains(kinds, MethodKind.GETTER)) {
            Objects.requireNonNull(argument, "argument is null");
            name = "get" + capitalize(argument.getJavaName());
        } else if (MethodKind.contains(kinds, MethodKind.SETTER)) {
            Objects.requireNonNull(argument, "argument is null");
            name = "set" + capitalize(argument.getJavaName());
        } else if (MethodKind.contains(kinds, MethodKind.INVOKE)) {
            name = "invoke";
        } else if (MethodKind.contains(kinds, MethodKind.AWAIT)) {
            name = "await";
        } else if (MethodKind.contains(kinds, MethodKind.CLOSE)) {
            name = "close";
        } else {
            throw new IllegalArgumentException("Unhandled kinds " + kinds);
        }

        if (MethodKind.contains(kinds, MethodKind.ASYNC))
            name += "Async";

        return name;
    }

    @Override
    public String parameterName(int kinds, ParameterKind parameterKind, GeneratedKernelArgument argument) {
        String name = camelCase(parameterKind.name().toLowerCase(), false);
        if (MethodKind.contains(kinds, MethodKind.INVOKE))
            name = argument.getArgName() + capitalize(name);
        return name;
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

    private static String bufferElementType(@Nullable Class<?> bufferClass) {
        if (bufferClass == null)
            return "Byte";
        return bufferClass.getSimpleName().replaceAll("Buffer", "");
    }

}
