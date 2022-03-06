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

    @Test
    void testGetArgInfo() {
        withKernel(KERNEL_1, k -> {
            assertArgInfo(k.getArgInfo(0), "gInts", "int*", CLBitfield.of(CONST), GLOBAL, KernelArgAccessQualifier.NONE);
            assertArgInfo(k.getArgInfo(1), "lInts", "int*", CLBitfield.empty(), LOCAL, KernelArgAccessQualifier.NONE);
            assertArgInfo(k.getArgInfo(2), "pInt", "int", CLBitfield.empty(), PRIVATE, KernelArgAccessQualifier.NONE);
            assertArgInfo(k.getArgInfo(3), "roImage", "image2d_t", CLBitfield.empty(), GLOBAL, KernelArgAccessQualifier.READ_ONLY);
            assertArgInfo(k.getArgInfo(4), "rwImage", "image2d_t", CLBitfield.empty(), GLOBAL, KernelArgAccessQualifier.WRITE_ONLY);
        });

        withKernel(KERNEL_2, k -> {
            assertArgInfo(k.getArgInfo(0), "arg0", "uint16*", CLBitfield.of(CONST), CONSTANT, KernelArgAccessQualifier.NONE);
            assertArgInfo(k.getArgInfo(1), "arg1", "int8*", CLBitfield.empty(), GLOBAL, KernelArgAccessQualifier.NONE);
            assertArgInfo(k.getArgInfo(2), "arg2", "float3*", CLBitfield.empty(), LOCAL, KernelArgAccessQualifier.NONE);
        });

        withKernel(KERNEL_3, k -> {
            CLKernelArgInfo info = k.getArgInfo(0);
            System.out.println(info);
        });

    }

    private static void assertArgInfo(CLKernelArgInfo argInfo, String expectedName,
                                      String expectedType, CLBitfield<KernelArgTypeQualifier> expectedTypeQualifiers,
                                      KernelArgAddressQualifier expectedAddress,
                                      KernelArgAccessQualifier expectedAccess) {
        assertEquals(expectedName, argInfo.getArgName());
        assertEquals(expectedType, argInfo.getTypeName());
        assertEquals(expectedTypeQualifiers, argInfo.getTypeQualifiers());
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