package org.yah.tools.opencl.codegen.generator.model;

import org.yah.tools.opencl.codegen.parser.model.ParsedProgram;

public class GeneratedProgramInterface extends GeneratedType<ParsedProgram, GeneratedProgramInterface> {

    public GeneratedProgramInterface(ParsedProgram generatedFrom, String packageName, String simpleName) {
        super(generatedFrom, packageName, simpleName, true);
    }

    @Override
    protected GeneratedProgramInterface getThis() {
        return this;
    }
}
