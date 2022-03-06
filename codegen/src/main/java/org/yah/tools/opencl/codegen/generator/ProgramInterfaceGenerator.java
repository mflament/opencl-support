package org.yah.tools.opencl.codegen.generator;

import org.yah.tools.opencl.codegen.generator.model.GeneratedProgramInterface;
import org.yah.tools.opencl.codegen.parser.model.ParsedProgram;

public interface ProgramInterfaceGenerator {

    GeneratedProgramInterface generate(ParsedProgram program, String packageName);

}
