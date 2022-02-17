package org.yah.tools.opencl.parser;

import org.yah.tools.opencl.parser.model.ParsedProgram;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public interface ProgramParser {

    ParsedProgram parse(String source);

    ParsedProgram parse(Path file, Charset charset) throws IOException;

    default ParsedProgram parse(Path file) throws IOException {
        return parse(file, StandardCharsets.UTF_8);
    }

    ParsedProgram parse(InputStream is, Charset charset) throws IOException;

    default ParsedProgram parse(InputStream is) throws IOException {
        return parse(is, StandardCharsets.UTF_8);
    }

}
