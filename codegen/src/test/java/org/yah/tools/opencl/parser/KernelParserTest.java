package org.yah.tools.opencl.parser;

import org.yah.tools.opencl.context.CLContext;
import org.yah.tools.opencl.kernel.CLKernel;
import org.yah.tools.opencl.program.CLProgram;

import java.util.List;

public class KernelParserTest {

    public static void main(String[] args) {
        try (CLContext context = new CLContext()) {
            CLProgram program = context.programBuilder()
                    .withFile("classpath:/program_1.cl")
                    .withOptions("-cl-kernel-arg-info")
                    .build();
            List<CLKernel> kernels = program.newKernels();
        }
    }
}
