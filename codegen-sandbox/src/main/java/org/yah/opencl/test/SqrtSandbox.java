package org.yah.opencl.test;

import org.lwjgl.BufferUtils;
import org.yah.opencl.test.programs.SqrtProgram;
import org.yah.opencl.test.programs.cl.CLSqrtProgram;
import org.yah.opencl.test.programs.cl.kernels.CLMySqrtKernel;
import org.yah.opencl.test.programs.kernels.MySqrtKernel;
import org.yah.tools.opencl.context.CLContext;
import org.yah.tools.opencl.ndrange.NDRange;
import org.yah.tools.opencl.ndrange.NDRange1;

import java.nio.DoubleBuffer;

import static org.yah.opencl.test.SandboxSupport.*;

public class SqrtSandbox implements AutoCloseable {

    private static final int SIZE = 100_000_000;
    public static final int RUNS = 10;

    public static void main(String[] args) throws Exception {
        runInCOntext(context -> new SqrtSandbox(context).run());
    }

    private final SqrtProgram sqrtProgram;
    private final MySqrtKernel sqrtKernel;

    public SqrtSandbox(CLContext context) {
        this.sqrtProgram = new CLSqrtProgram(context);
        this.sqrtKernel = sqrtProgram.createMySqrtKernel();
    }

    private void run() {
        int count = SIZE;
        DoubleBuffer a = BufferUtils.createDoubleBuffer(count);
        DoubleBuffer results = BufferUtils.createDoubleBuffer(count);
        DoubleBuffer expectedResults = BufferUtils.createDoubleBuffer(count);

        randomize(a, 0, 100);

        timed("sqrt host", () -> localSqrt(a, expectedResults), RUNS);

        CLMySqrtKernel clSqrtKernel = (CLMySqrtKernel) this.sqrtKernel;

        NDRange1 range = NDRange.range1().globalWorkSize(count);
        sqrtKernel.createA(a)
                .createResults(count)
                .setLength(count);
        timed("sqrt device", () -> sqrtKernel.invoke(range).readResults(results), RUNS);

        compare("sqrt", results, expectedResults);
        System.out.println("success");
    }

    @Override
    public void close() {
        sqrtKernel.close();
        sqrtProgram.close();
    }

    private static void localSqrt(DoubleBuffer a, DoubleBuffer results) {
        parallelFor(a, i -> results.put(i, Math.sqrt(a.get(i))));
    }

}
