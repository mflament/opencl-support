package org.yah.tools.opencl.codegen.naming;

import org.apache.commons.lang3.StringUtils;
import org.yah.tools.opencl.codegen.parser.ParsedKernel;
import org.yah.tools.opencl.codegen.parser.ParsedKernelArgument;
import org.yah.tools.opencl.codegen.parser.ParsedProgram;
import org.yah.tools.opencl.codegen.parser.type.CLType;
import org.yah.tools.opencl.program.CLProgram;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class DefaultNamingStrategy implements NamingStrategy {

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
    public ProgramNamingStrategy program(ParsedProgram parsedProgram) {
        return new DefaultProgramNamingStrategy(parsedProgram);
    }

    private class DefaultProgramNamingStrategy implements NamingStrategy.ProgramNamingStrategy {
        private final ParsedProgram parsedProgram;

        public DefaultProgramNamingStrategy(ParsedProgram parsedProgram) {
            this.parsedProgram = Objects.requireNonNull(parsedProgram, "parsedProgram is null");
        }

        @Override
        public String interfaceName() {
            return decorate(baseName(parsedProgram.getFilePath()), programNameDecorator, "program");
        }

        @Override
        public KernelNamingStrategy kernel(ParsedKernel parsedKernel) {
            return new DefaultKernelNamingStrategy(parsedKernel);
        }

        @Override
        public String createKernel(ParsedKernel parsedKernel) {
            return camelCase(parsedKernel.getName(), false);
        }

        @Override
        public ProgramImplementationNamingStrategy clImplementation(Map<String, CLType> typeArguments) {
            return new DefaultProgramImplementationNamingStrategy(typeArguments);
        }

        private class DefaultKernelNamingStrategy implements NamingStrategy.KernelNamingStrategy {
            private final ParsedKernel parsedKernel;

            public DefaultKernelNamingStrategy(ParsedKernel parsedKernel) {
                this.parsedKernel = Objects.requireNonNull(parsedKernel, "parsedKernel is null");
            }

            @Override
            public String kernelPackage(String basePackage) {
                return basePackage + ".kernels";
            }

            @Override
            public String interfaceName() {
                return decorate(parsedKernel.getName(), kernelNameDecorator, "kernel");
            }

            @Override
            public MethodNamingStrategy createBufferWithSize(ParsedKernelArgument kernelArgument) {
                return newMethod("create" + argumentNameSuffix(kernelArgument), "size", "bufferProperties");
            }

            @Override
            public MethodNamingStrategy createBufferWithBuffer(ParsedKernelArgument kernelArgument) {
                return newMethod("create" + argumentNameSuffix(kernelArgument), "buffer", "bufferProperties");
            }

            @Override
            public MethodNamingStrategy writeBuffer(ParsedKernelArgument kernelArgument) {
                return newMethod("update" + argumentNameSuffix(kernelArgument), "buffer", "offset", "event");
            }

            @Override
            public MethodNamingStrategy readBuffer(ParsedKernelArgument kernelArgument) {
                return newMethod("read" + argumentNameSuffix(kernelArgument), "buffer", "offset", "event");
            }

            @Override
            public MethodNamingStrategy setLocalBufferSize(ParsedKernelArgument kernelArgument) {
                return newMethod("set" + argumentNameSuffix(kernelArgument) + "Size", "size");
            }

            @Override
            public MethodNamingStrategy setValue(ParsedKernelArgument kernelArgument) {
                return newMethod("set" + argumentNameSuffix(kernelArgument), "value");
            }

            @Override
            public MethodNamingStrategy invokeMethod() {
                return newMethod("invoke", "range", "event");
            }

            @Override
            public int maxVectorComponentParameters() {
                return VECTOR_COMPONENT_NAMES.length;
            }

            @Override
            public MethodNamingStrategy setSetVectorComponents(ParsedKernelArgument kernelArgument) {
                return newMethod("set" + argumentNameSuffix(kernelArgument), VECTOR_COMPONENT_NAMES);
            }

            private String argumentNameSuffix(ParsedKernelArgument kernelArgument) {
                return camelCase(kernelArgument.getArgName());
            }

        }

        private class DefaultProgramImplementationNamingStrategy implements ProgramImplementationNamingStrategy {
            private final Map<String, CLType> typeArguments;

            public DefaultProgramImplementationNamingStrategy(Map<String, CLType> typeArguments) {
                this.typeArguments = Objects.requireNonNull(typeArguments, "typeArguments is null");
            }

            @Override
            public String packageName(String basePackage) {
                return basePackage + ".cl";
            }

            @Override
            public String className() {
                return "CL" + DefaultProgramNamingStrategy.this.interfaceName() + typeArgumentsSuffix();
            }

            @Override
            public KernelImplementationNamingStrategy kernelImplementation(ParsedKernel parsedKernel) {
                return new DefaultImplementationNamingStrategy(parsedKernel);
            }

            public String typeArgumentsSuffix() {
                return typeArguments.values().stream()
                        .map(this::typeArgumentName)
                        .collect(Collectors.joining(""));
            }

            private String typeArgumentName(CLType clType) {
                return camelCase(clType.getComponentType().getName());
            }

            private class DefaultImplementationNamingStrategy implements KernelImplementationNamingStrategy {

                private final KernelNamingStrategy kernelNamingStrategy;

                public DefaultImplementationNamingStrategy(ParsedKernel parsedKernel) {
                    kernelNamingStrategy = kernel(parsedKernel);
                }

                @Override
                public String packageName(String basePackage) {
                    return basePackage + ".cl.kernels";
                }

                @Override
                public String className() {
                    return "CL" + kernelNamingStrategy.interfaceName() + typeArgumentsSuffix();
                }

                @Override
                public String bufferFieldName(ParsedKernelArgument kernelArgument) {
                    return camelCase(kernelArgument.getArgName(), false);
                }

            }

        }
    }

    private static MethodNamingStrategy newMethod(String name, String... parameterNames) {
        return new SimpleMethodNamingStrategy(name, Arrays.asList(parameterNames));
    }

    private static class SimpleMethodNamingStrategy implements MethodNamingStrategy {
        private final String methodName;
        private final List<String> parameterNames;

        public SimpleMethodNamingStrategy(String methodName, List<String> parameterNames) {
            this.methodName = Objects.requireNonNull(methodName, "methodName is null");
            this.parameterNames = Objects.requireNonNull(parameterNames, "parameterNames is null");
        }

        @Override
        public String methodName() {
            return methodName;
        }

        @Override
        public String parameterName(int index) {
            return parameterNames.get(index);
        }

    }

    private String camelCase(String argName) {
        return camelCase(argName, true);
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

    private String decorate(String name, @Nullable TypeNameDecorator decorator, String suffix) {
        name = camelCase(name);
        if (decorator != null)
            return decorator.decorate(name);

        if (!name.toLowerCase().contains(suffix.toLowerCase()))
            name += StringUtils.capitalize(suffix);

        return name;
    }
}
