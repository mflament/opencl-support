package org.yah.tools.opencl.codegen.model.program;

import org.yah.tools.opencl.codegen.model.MethodModel;
import org.yah.tools.opencl.codegen.parser.model.ParsedProgram;

import java.util.List;

import static org.yah.tools.opencl.codegen.model.program.ProgramMethods.CloseProgram;
import static org.yah.tools.opencl.codegen.model.program.ProgramMethods.CreateKernel;

public interface ProgramMethod extends MethodModel {

    @Override
    ProgramModel getDeclaringType();

    List<? extends ProgramMethodParameter> getParameters();

    default ParsedProgram getParsedProgram() {
        return getDeclaringType().getParsedProgram();
    }

    default boolean isCreateKernel() {
        return false;
    }

    default CreateKernel asCreateKernel() {
        throw new UnsupportedOperationException();
    }

    default boolean isCloseProgram() {
        return false;
    }

    default CloseProgram asCloseProgram() {
        throw new UnsupportedOperationException();
    }

}
