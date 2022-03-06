package org.yah.tools.opencl.codegen.parser;

import org.yah.tools.opencl.codegen.parser.model.ParsedProgram;

public interface ProgramParser {

    ParsedProgram parse(String filePath);

}
