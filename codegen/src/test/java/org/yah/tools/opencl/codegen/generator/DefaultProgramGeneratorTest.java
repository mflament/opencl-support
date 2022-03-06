package org.yah.tools.opencl.codegen.generator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.yah.tools.opencl.codegen.CLTestSupport;
import org.yah.tools.opencl.codegen.parser.model.ParsedProgram;
import org.yah.tools.opencl.codegen.parser.impl.DefaultProgramParser;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

import static org.yah.tools.opencl.codegen.CLTestSupport.BASE_DIR;
import static org.yah.tools.opencl.codegen.CLTestSupport.PROGRAM_PATH;

class DefaultProgramGeneratorTest {

    public static final Path OUTPUT_DIRECTORY = Paths.get("target/test/generator_test");

    @Test
    void generate() {
        CLTestSupport.runWithContext(ctx -> {
            DefaultProgramParser programParser = new DefaultProgramParser(ctx, BASE_DIR, Collections.singleton(BASE_DIR.toString()));
            ParsedProgram parsedProgram = programParser.parse(PROGRAM_PATH, "-w");
            DefaultProgramGenerator generator = new DefaultProgramGenerator(OUTPUT_DIRECTORY, null);
            try {
                generator.generate("org.yah.tools.opencl.codegen.test", parsedProgram);
            } catch (IOException e) {
                Assertions.fail(e.getMessage());
            }
        });
    }
}