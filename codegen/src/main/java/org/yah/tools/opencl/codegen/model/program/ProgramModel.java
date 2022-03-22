package org.yah.tools.opencl.codegen.model.program;

import org.yah.tools.opencl.codegen.NamingStrategy;
import org.yah.tools.opencl.codegen.model.TypeModel;
import org.yah.tools.opencl.codegen.model.kernel.KernelModel;
import org.yah.tools.opencl.codegen.parser.ParsedProgram;

import java.util.*;
import java.util.stream.Collectors;

public class ProgramModel implements TypeModel {

    private final ParsedProgram parsedProgram;
    private final NamingStrategy namingStrategy;
    private final String basePackage;

    private final List<ProgramMethod> methods = new ArrayList<>();
    private final List<KernelModel> kernels = new ArrayList<>();

    private String name;

    public ProgramModel(ParsedProgram parsedProgram, NamingStrategy namingStrategy, String basePackage) {
        this.parsedProgram = Objects.requireNonNull(parsedProgram, "parsedProgram is null");
        this.namingStrategy = Objects.requireNonNull(namingStrategy, "namingStrategy is null");
        if (basePackage == null || basePackage.length() == 0)
            throw new IllegalArgumentException("basePackage " + basePackage);
        this.basePackage = basePackage;
    }

    public ParsedProgram getParsedProgram() {
        return parsedProgram;
    }

    public NamingStrategy getNamingStrategy() {
        return namingStrategy;
    }

    @Override
    public String getPackageName() {
        return basePackage;
    }

    @Override
    public String getName() {
        if (name == null)
            name = namingStrategy.programName(parsedProgram);
        return name;
    }

    public String getBasePackage() {
        return basePackage;
    }

    public List<? extends ProgramMethod> getMethods() {
        return methods;
    }

    public List<KernelModel> getKernels() {
        return Collections.unmodifiableList(kernels);
    }

    public void addMethod(ProgramMethod method) {
        methods.add(method);
        if (method.isCreateKernel() && !kernels.contains(method.asCreateKernel().getKernelModel()))
            kernels.add(method.asCreateKernel().getKernelModel());
    }

    @Override
    public String toString() {
        return getName();
    }

    public List<String> getReferencedTypeParameters() {
        return getKernels().stream()
                .flatMap(km -> km.getReferencedTypeParameters().stream())
                .distinct()
                .collect(Collectors.toList());
    }
}
