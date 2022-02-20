package org.yah.tools.opencl.program;

import org.junit.jupiter.api.Test;
import org.yah.tools.opencl.CLTestSupport;
import org.yah.tools.opencl.CLUtils;
import org.yah.tools.opencl.kernel.CLKernel;
import org.yah.tools.opencl.platform.CLDevice;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class CLProgramTest {

    @Test
    void testNewKernels() {
        CLTestSupport.runWithProgram("program_1.cl", p -> {
            List<CLKernel> kernels = p.newKernels();
            assertEquals(2, kernels.size());
            assertTrue(kernels.stream().anyMatch(k -> k.getName().equals("firstKernel")));
            assertTrue(kernels.stream().anyMatch(k -> k.getName().equals("secondKernel")));
        });
    }

    @Test
    void testGetBinaries() {
        CLTestSupport.runWithProgram("program_1.cl", p -> {
            Map<CLDevice, ByteBuffer> binaries = p.getBinaries();
            binaries.forEach((d, b) -> System.out.println(d.getName() + "\n" + CLUtils.readCLString(b)));
        });
    }
}