package org.yah.tools.opencl.codegen;

import org.junit.jupiter.api.Test;
import org.yah.tools.opencl.codegen.generator.ProgramGenerator;
import org.yah.tools.opencl.codegen.generator.ProgramGeneratorRequest;
import org.yah.tools.opencl.codegen.naming.DefaultNamingStrategy;
import org.yah.tools.opencl.codegen.parser.CLTypeVariables;
import org.yah.tools.opencl.context.CLContext;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;


class ProgramGeneratorTest {

    public static final String TEST_PROGRAM_PATH = "classpath:cl/simple.cl";
    public static final String TYPED_PROGRAM_PATH = "classpath:cl/typed.cl";

    @Test
    void withoutTypeParameters() throws IOException {
        try (CLContext context = CLContext.builder().build()) {
            ProgramGenerator generator = new ProgramGenerator(context, Paths.get("target/generator-test"), DefaultNamingStrategy.get());
            ProgramGeneratorRequest request = ProgramGeneratorRequest.builder()
                    .withBasePackage("org.yah.tools.opencl.simple")
                    .withProgramPath(TEST_PROGRAM_PATH)
                    .build();
            generator.generate(request);
        }
    }

    @Test
    void withTypeParameters() throws IOException {
        try (CLContext context = CLContext.builder().build()) {
            ProgramGenerator generator = new ProgramGenerator(context, Paths.get("target/generator-test"), DefaultNamingStrategy.get());
            ProgramGeneratorRequest request = ProgramGeneratorRequest.builder()
                    .withBasePackage("org.yah.tools.opencl.typed")
                    .withProgramPath(TYPED_PROGRAM_PATH)
                    .withTypeVariables(CLTypeVariables.parse(Arrays.asList("T=int,V=float", "T=float,V=float", "T=double,V=double")))
                    .build();
            generator.generate(request);
        }
    }
}
