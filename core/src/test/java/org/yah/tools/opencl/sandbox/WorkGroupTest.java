package org.yah.tools.opencl.sandbox;

import org.yah.tools.opencl.cmdqueue.NDRange;
import org.yah.tools.opencl.kernel.CLKernel;

public class WorkGroupTest extends AbstractCLSandbox {

    public WorkGroupTest() {
        super("wgroup_test.cl");
    }

    public void shutdown() {
        close();
    }

    public void run() {
        CLKernel kernel = program.newKernel("wgtest");
        NDRange range = new NDRange(1).globalWorkSize(16).localWorkSize(4);
        range.validate(program.getDevices());
        commandQueue.run(kernel, range);
        commandQueue.finish();
    }

    public void run2() {
        CLKernel kernel = program.newKernel("testArgs");
        //kernel.setArg(0, new int[]{42, 50, 64});
        kernel.setArg(0, new int[]{42});

        NDRange range = new NDRange(1).globalWorkSize(1).localWorkSize(1);
        range.validate(program.getDevices());
        commandQueue.run(kernel, range);
        commandQueue.finish();
    }

    public static void main(String[] args) {
        //new WorkGroupTest().run();
        new WorkGroupTest().run2();
    }
}
