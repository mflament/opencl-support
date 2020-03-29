package org.yah.tools.opencl.sandbox;

import java.io.IOException;

import org.yah.tools.opencl.cmdqueue.CLCommandQueue.KernelNDRange;
import org.yah.tools.opencl.kernel.CLKernel;

public class WorkGroupTest extends AbstractCLSandbox {

    public WorkGroupTest() throws IOException {
        super("wgroup_test.cl");
    }

    public void run() {
        CLKernel kernel = environment.kernel("wgtest");
        KernelNDRange range = environment.kernelRange();
        range.globalWorkSizes(4, 4);
        range.localWorkSizes(2, 2);
        //range.validate();
        environment.run(kernel, range);
        environment.finish();
    }

    public static void main(String[] args) throws IOException {
        try (WorkGroupTest vs = new WorkGroupTest()) {
            vs.run();
        }
    }

}
