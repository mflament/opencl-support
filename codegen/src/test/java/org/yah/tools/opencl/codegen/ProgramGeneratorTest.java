package org.yah.tools.opencl.codegen;

import org.junit.jupiter.api.Test;
import org.yah.tools.opencl.codegen.generator.ProgramGenerator;
import org.yah.tools.opencl.codegen.generator.ProgramGeneratorRequest;
import org.yah.tools.opencl.context.CLContext;

import java.io.IOException;
import java.nio.file.Paths;


class ProgramGeneratorTest {

    public static final String TEST_PROGRAM_PATH = "classpath:cl/test.cl";
    public static final String TYPED_PROGRAM_PATH = "classpath:cl/typed.cl";

    @Test
    void testGenerator() throws IOException {
        try (CLContext context = CLContext.builder().build()) {
            ProgramGenerator generator = new ProgramGenerator(context, Paths.get("target/generator-test"), null);
            ProgramGeneratorRequest request = ProgramGeneratorRequest.builder()
                    .withBasePackage("org.yah.tools.opencl.test")
                    .withProgramPath(TEST_PROGRAM_PATH)
                    .build();
            generator.generate(request);
        }
    }

    @Test
    void testTypedGenerator() throws IOException {
        try (CLContext context = CLContext.builder().build()) {
            ProgramGenerator generator = new ProgramGenerator(context, Paths.get("target/generator-test"), null);
            ProgramGeneratorRequest request = ProgramGeneratorRequest.builder()
                    .withBasePackage("org.yah.tools.opencl.test")
                    .withProgramPath(TYPED_PROGRAM_PATH)
                    .withTypeParametersConfig(TypeParametersConfig.builder()
                            .withNames("T")
                            .withTypes("int")
                            .withTypes("float")
                            .build())
                    .build();
            generator.generate(request);
        }
    }
}
