package org.yah.tools.opencl.codegen.parser;

import org.yah.tools.opencl.codegen.parser.model.ParsedProgram;
import org.yah.tools.opencl.program.CLProgram;

public interface ProgramParser {

    ParsedProgram parse(CLProgram program, String filePath);

}
