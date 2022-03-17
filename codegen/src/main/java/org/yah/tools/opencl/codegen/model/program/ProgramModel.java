package org.yah.tools.opencl.codegen.model.program;

import org.yah.tools.opencl.codegen.NamingStrategy;
import org.yah.tools.opencl.codegen.model.TypeModel;
import org.yah.tools.opencl.codegen.model.kernel.KernelModel;
import org.yah.tools.opencl.codegen.parser.ParsedProgram;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ProgramModel implements TypeModel {

    private final ParsedProgram parsedProgram;
    private final NamingStrategy namingStrategy;
    private final String basePackage;
    private final List<ProgramMethod> methods = new ArrayList<>();
    private String name;

    public ProgramModel(ParsedProgram parsedProgram, NamingStrategy namingStrategy, String basePackage) {
        this.parsedProgram = Objects.requireNonNull(parsedProgram, "parsedProgram is null");
        this.namingStrategy = Objects.requireNonNull(namingStrategy, "namingStrategy is null");
        this.basePackage = Objects.requireNonNull(basePackage, "basePackage is null");
        if (basePackage.length() == 0)
            throw new IllegalArgumentException("basePackage is empty");
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
        return getMethods().stream()
                .filter(ProgramMethod::isCreateKernel)
                .map(m -> m.asCreateKernel().getKernelModel())
                .collect(Collectors.toList());
    }

    public void addMethod(ProgramMethod method) {
        methods.add(method);
    }

    @Override
    public String toString() {
        return getName();
    }
}
