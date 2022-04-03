package org.yah.tools.opencl.kernel;


import org.junit.jupiter.api.Test;
import org.yah.tools.opencl.CLTestSupport;
import org.yah.tools.opencl.enums.CLBitfield;
import org.yah.tools.opencl.enums.KernelArgAccessQualifier;
import org.yah.tools.opencl.enums.KernelArgAddressQualifier;
import org.yah.tools.opencl.enums.KernelArgTypeQualifier;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.yah.tools.opencl.enums.KernelArgAddressQualifier.*;
import static org.yah.tools.opencl.enums.KernelArgTypeQualifier.CONST;

class CLKernelTest {

    public static final String PROGRAM_1 = "program_1.cl";
    public static final String KERNEL_1 = "firstKernel";
    public static final String KERNEL_2 = "secondKernel";
    public static final String KERNEL_3 = "thirdKernel";

    @Test
    void testGetNumArgs() {
        withKernel(KERNEL_1, k -> assertEquals(5, k.getNumArgs()));
        withKernel(KERNEL_2, k -> assertEquals(3, k.getNumArgs()));
    }

    @Test
    void testGetAttributes() {
        withKernel(KERNEL_1, k -> assertEquals("", k.getAttributes()));
        withKernel(KERNEL_2, k -> assertEquals("work_group_size_hint(1,1,1)", k.getAttributes()));
    }

    private static void withKernel(String kernelName, Consumer<CLKernel> test) {
        CLTestSupport.runWithProgram(PROGRAM_1, p -> {
            try (CLKernel kernel = p.newKernel(kernelName)) {
                test.accept(kernel);
            }
        });
    }
}