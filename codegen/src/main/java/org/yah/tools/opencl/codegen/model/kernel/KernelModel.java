package org.yah.tools.opencl.codegen.model.kernel;

import org.yah.tools.opencl.codegen.NamingStrategy;
import org.yah.tools.opencl.codegen.model.TypeModel;
import org.yah.tools.opencl.codegen.model.program.ProgramModel;
import org.yah.tools.opencl.codegen.parser.ParsedKernel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class KernelModel implements TypeModel {

    private static final String KERNEL_PACKAGE = ".kernels";

    private final ProgramModel programModel;
    private final ParsedKernel parsedKernel;
    private final List<KernelMethod> methods = new ArrayList<>();

    private String name;

    public KernelModel(ProgramModel programModel, ParsedKernel parsedKernel) {
        this.programModel = Objects.requireNonNull(programModel, "programModel is null");
        this.parsedKernel = Objects.requireNonNull(parsedKernel, "parsedKernel is null");
    }

    @Override
    public String getPackageName() {
        return programModel.getPackageName() + KERNEL_PACKAGE;
    }

    @Override
    public String getName() {
        if (name == null)
            name = getNamingStrategy().kernelName(parsedKernel);
        return name;
    }

    public ProgramModel getProgramModel() {
        return programModel;
    }

    public ParsedKernel getParsedKernel() {
        return parsedKernel;
    }

    public List<? extends KernelMethod> getMethods() {
        return methods;
    }

    public NamingStrategy getNamingStrategy() {
        return programModel.getNamingStrategy();
    }

    public void addMethod(KernelMethod method) {
        methods.add(method);
    }

    public void addMethods(Collection<? extends KernelMethod> methods) {
        this.methods.addAll(methods);
    }

    @Override
    public String toString() {
        return getName();
    }
}
