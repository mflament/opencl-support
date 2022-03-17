package org.yah.tools.opencl.codegen;

import org.junit.jupiter.api.Test;
import org.yah.tools.opencl.codegen.generator.ProgramGenerator;
import org.yah.tools.opencl.context.CLContext;
import org.yah.tools.opencl.program.CLCompilerOptions;
import org.yah.tools.opencl.program.CLProgram;

import java.io.IOException;
import java.nio.file.Paths;


class ProgramGeneratorTest {

    public static final String PROGRAM_PATH = "classpath:cl/test.cl";

    @Test
    void testGenerator() throws IOException {
        try (CLContext context = CLContext.builder().build()) {
            ProgramGenerator generator = new ProgramGenerator(Paths.get("target/generator-test"), null);
            try (CLProgram program = context.programBuilder()
                    .withFile(PROGRAM_PATH)
                    .withCompilerOptions(new CLCompilerOptions().withKernelArgInfo().withOptDisable().withClStd("CL2.0"))
                    .build()) {
                generator.generate(program, PROGRAM_PATH, "org.yah.tools.opencl.test");
            }
        }
    }
}
