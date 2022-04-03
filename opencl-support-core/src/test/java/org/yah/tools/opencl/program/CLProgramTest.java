package org.yah.tools.opencl.program;

import org.junit.jupiter.api.Test;
import org.yah.tools.opencl.CLTestSupport;
import org.yah.tools.opencl.CLUtils;
import org.yah.tools.opencl.kernel.CLKernel;
import org.yah.tools.opencl.platform.CLDevice;
import org.yah.tools.opencl.platform.CLPlatform;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class CLProgramTest {

    @Test
    void testNewKernels() {
        CLTestSupport.runWithProgram("program_1.cl", p -> {
            List<CLKernel> kernels = p.newKernels();
            assertEquals(3, kernels.size());
            Set<String> kernelNames = kernels.stream().map(CLKernel::getName).collect(Collectors.toSet());
            assertEquals(new HashSet<>(Arrays.asList("firstKernel", "secondKernel", "thirdKernel")),kernelNames);
        });
    }

    @Test
    void testGetBinaries() {
        CLTestSupport.runWithProgram("program_1.cl", p -> {
            Map<CLDevice, ByteBuffer> binaries = p.getBinaries();
            binaries.forEach((d, b) -> System.out.println(d.getName() + "\n" + CLUtils.readCLString(b)));
        });
    }


    @Test
    void printPlatforms() {
        CLPlatform.platforms().forEach(p -> {
            System.out.println(p.toDetailedString());
        });
    }
}