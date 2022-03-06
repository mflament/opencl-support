package org.yah.tools.opencl.codegen.parser;

import org.junit.jupiter.api.Test;
import org.yah.tools.opencl.codegen.CLTestSupport;
import org.yah.tools.opencl.codegen.parser.impl.DefaultProgramParser;
import org.yah.tools.opencl.codegen.parser.model.ParsedProgram;
import org.yah.tools.opencl.generated.ProgramMetadata;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.yah.tools.opencl.codegen.CLTestSupport.BASE_DIR;
import static org.yah.tools.opencl.codegen.CLTestSupport.PROGRAM_PATH;

class ProgramParserTest {

    private static final Set<String> KERNEL_NAMES = new HashSet<>(Arrays.asList("first_kernel", "second_kernel", "third_kernel"));

    @Test
    void parse() {
        CLTestSupport.runWithContext(ctx -> {
            DefaultProgramParser programParser = new DefaultProgramParser(ctx, BASE_DIR, Collections.singleton(BASE_DIR.toString()));
            try (ParsedProgram parsedProgram = programParser.parse(PROGRAM_PATH, "-w")) {
                assertEquals(3, parsedProgram.getKernels().size());
                assertEquals(KERNEL_NAMES, parsedProgram.getKernels().stream().map(ParsedKernel::getName).collect(Collectors.toSet()));

                ProgramMetadata metadata = parsedProgram.getMetadata();
                assertEquals("classpath:cl/test.cl", metadata.getProgramFile());
                assertEquals("-w", metadata.getCompilerOptions());
            }

            programParser = new DefaultProgramParser(ctx, BASE_DIR, Collections.emptySet());
            try (ParsedProgram parsedProgram = programParser.parse(PROGRAM_PATH, null)) {
                assertEquals(3, parsedProgram.getKernels().size());
                assertEquals(KERNEL_NAMES, parsedProgram.getKernels().stream().map(ParsedKernel::getName).collect(Collectors.toSet()));

                ProgramMetadata metadata = parsedProgram.getMetadata();
                assertEquals("cl/test.cl", metadata.getProgramFile());
                assertNull(metadata.getCompilerOptions());
            }
        });
    }
}