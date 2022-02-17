package org.yah.tools.opencl.parser.clang;

import org.yah.tools.opencl.parser.ProgramParser;
import org.yah.tools.opencl.parser.model.ParsedProgram;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Path;

public class ClangProgramParser implements ProgramParser {

    @Override
    public ParsedProgram parse(Path file, Charset charset) throws IOException {
        return null;
    }

    @Override
    public ParsedProgram parse(InputStream is, Charset charset) throws IOException {
        return null;
    }

    @Override
    public ParsedProgram parse(String source) {
        return null;
    }

}
