package org.yah.tools.opencl.codegen.generator;

import org.yah.tools.opencl.codegen.generator.model.old.ProgramImplementation;
import org.yah.tools.opencl.codegen.generator.model.old.ProgramInterface;

@FunctionalInterface
public interface ImplementationGenerator {

    ProgramImplementation generateImplementation(ProgramInterface programInterface);

}
