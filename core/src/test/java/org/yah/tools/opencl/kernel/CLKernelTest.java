package org.yah.tools.opencl.kernel;


import org.junit.jupiter.api.Test;
import org.yah.tools.opencl.CLTestSupport;
import org.yah.tools.opencl.enums.KernelArgAccessQualifier;
import org.yah.tools.opencl.enums.KernelArgAddressQualifier;
import org.yah.tools.opencl.enums.KernelArgTypeQualifier;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.yah.tools.opencl.enums.KernelArgAddressQualifier.GLOBAL;
import static org.yah.tools.opencl.enums.KernelArgTypeQualifier.CONST;

class CLKernelTest {

    public static final String PROGRAM_1 = "program_1.cl";
    public static final String KERNEL_1 = "firstKernel";
    public static final String KERNEL_2 = "secondKernel";

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

    @Test
    void testGetArgInfo() {
        withKernel(KERNEL_1, k -> {
            CLKernelArgInfo argInfo = k.getArgInfo(0);
            assertArgInfo(k.getArgInfo(0), "gInts", "int*", CONST, GLOBAL, null);
        });
    }

    private static void assertArgInfo(CLKernelArgInfo argInfo, String expectedName,
                                      String expectedType, KernelArgTypeQualifier expectedTypeQualifier,
                                      KernelArgAddressQualifier expectedAddress,
                                      KernelArgAccessQualifier expectedAccess) {
        assertEquals(expectedName, argInfo.getArgName());
        assertEquals(expectedType, argInfo.getTypeName());
        assertEquals(expectedTypeQualifier, argInfo.getTypeQualifier());
        assertEquals(expectedAddress, argInfo.getAddressQualifier());
        assertEquals(expectedAccess, argInfo.getAccessQualifier());
    }

    private static void withKernel(String kernelName, Consumer<CLKernel> test) {
        CLTestSupport.runWithProgram(PROGRAM_1, p -> {
            try (CLKernel kernel = p.newKernel(kernelName)) {
                test.accept(kernel);
            }
        });
    }
}