package org.yah.tools.opencl.sandbox;

import java.io.IOException;

import org.yah.tools.opencl.cmdqueue.NDRange;
import org.yah.tools.opencl.kernel.CLKernel;

public class WorkGroupTest extends AbstractCLSandbox {

    public WorkGroupTest() throws IOException {
        super("wgroup_test.cl");
    }

    public void run() {
        CLKernel kernel = program.kernel("wgtest");
        NDRange range = new NDRange(1);
        range.globalWorkSize(16).localWorkSize(4);
        range.validate(program.getDevices());
        commandQueue.run(kernel, range);
        commandQueue.finish();
    }

    public static void main(String[] args) throws IOException {
        try (WorkGroupTest vs = new WorkGroupTest()) {
            vs.run();
        }
    }

}
