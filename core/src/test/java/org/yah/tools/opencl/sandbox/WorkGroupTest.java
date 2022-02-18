package org.yah.tools.opencl.sandbox;

import java.io.IOException;

import org.junit.After;
import org.junit.Test;
import org.yah.tools.opencl.cmdqueue.NDRange;
import org.yah.tools.opencl.kernel.CLKernel;

public class WorkGroupTest extends AbstractCLSandbox {

    public WorkGroupTest() throws IOException {
        super("wgroup_test.cl");
    }

    @After
    public void shutdown() {
        close();
    }

    @Test
    public void run() {
        CLKernel kernel = program.newKernel("wgtest");
        NDRange range = new NDRange(1).globalWorkSize(16).localWorkSize(4);
        range.validate(program.getDevices());
        commandQueue.run(kernel, range);
        commandQueue.finish();
    }

}
