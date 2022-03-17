package org.yah.tools.opencl.codegen.model.program;

import org.yah.tools.opencl.codegen.model.kernel.KernelModel;

import java.util.List;
import java.util.Objects;

public final class ProgramMethods {
    private ProgramMethods() {
    }

    static abstract class AbstractProgramMethod implements ProgramMethod {
        private final ProgramModel programModel;

        private String methodName;

        public AbstractProgramMethod(ProgramModel programModel) {
            this.programModel = Objects.requireNonNull(programModel, "programModel is null");
        }

        @Override
        public ProgramModel getDeclaringType() {
            return programModel;
        }

        @Override
        public String getMethodName() {
            if (methodName == null)
                methodName = programModel.getNamingStrategy().programMethodName(this);
            return methodName;
        }

        @Override
        public List<? extends ProgramMethodParameter> getParameters() {
            return null;
        }


        @Override
        public String toString() {
            return String.format("%s.%s()", programModel.getName(), getMethodName());
        }
    }

    public static class CreateKernel extends AbstractProgramMethod {
        private final KernelModel kernelModel;

        public CreateKernel(KernelModel kernelModel) {
            super(Objects.requireNonNull(kernelModel, "kernelModel is null").getProgramModel());
            this.kernelModel = kernelModel;
        }

        public KernelModel getKernelModel() {
            return kernelModel;
        }

        @Override
        public boolean isCreateKernel() {
            return true;
        }

        @Override
        public CreateKernel asCreateKernel() {
            return this;
        }
    }

    public static class CloseProgram extends AbstractProgramMethod {

        public CloseProgram(ProgramModel programModel) {
            super(programModel);
        }

        @Override
        public String getMethodName() {
            return "close";
        }

        @Override
        public boolean isCloseProgram() {
            return true;
        }

        @Override
        public CloseProgram asCloseProgram() {
            return this;
        }
    }

}
