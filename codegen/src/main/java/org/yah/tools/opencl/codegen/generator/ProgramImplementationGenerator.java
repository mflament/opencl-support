package org.yah.tools.opencl.codegen.generator;

import org.yah.tools.opencl.codegen.generator.model.GeneratedKernelImplementation;
import org.yah.tools.opencl.codegen.generator.model.GeneratedProgramImplementation;
import org.yah.tools.opencl.codegen.generator.model.GeneratedProgramInterface;

public interface ProgramImplementationGenerator {

    GeneratedProgramImplementation generate(GeneratedProgramInterface generatedInterface);

}
