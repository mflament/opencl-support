package org.yah.opencl.test;

import org.lwjgl.BufferUtils;
import org.yah.opencl.test.programs.SumProgramFloat;
import org.yah.opencl.test.programs.cl.CLSumProgramFloat;
import org.yah.opencl.test.programs.kernels.MySumKernelFloat;
import org.yah.tools.opencl.ndrange.NDRange;
import org.yah.tools.opencl.ndrange.NDRange1;
import org.yah.tools.opencl.context.CLContext;

import java.nio.FloatBuffer;

import static org.yah.opencl.test.SandboxSupport.*;

public class FloatSumSandbox implements AutoCloseable {

    private static final int SIZE = 100_000_000;
    public static final int RUNS = 100;

    public static void main(String[] args) throws Exception {
        runInCOntext(context -> new FloatSumSandbox(context).run());
    }

    private final SumProgramFloat sumProgram;
    private final MySumKernelFloat sumKernel;

    public FloatSumSandbox(CLContext context) {
        this.sumProgram = new CLSumProgramFloat(context);
        this.sumKernel = sumProgram.createMySumKernelFloat();
    }

    private void run() {
        int count = SIZE;
        FloatBuffer a = BufferUtils.createFloatBuffer(count);
        FloatBuffer b = BufferUtils.createFloatBuffer(count);
        FloatBuffer results = BufferUtils.createFloatBuffer(count);
        FloatBuffer expectedResults = BufferUtils.createFloatBuffer(count);

        randomize(a, 0, 10000);
        randomize(b, 0, 10000);

        timed("sum host", () -> localSum(a, b, expectedResults), RUNS);

        NDRange1 range = NDRange.range1().globalWorkSize(count);
        sumKernel.createA(a).createB(b).createResults(count).setLength(count);
        timed("sum device", () -> sumKernel.invoke(range).readResults(results), RUNS);

        compare("sum", results, expectedResults);
    }

    @Override
    public void close() {
        sumKernel.close();
        sumProgram.close();
    }

    private static void localSum(FloatBuffer a, FloatBuffer b, FloatBuffer results) {
        parallelFor(a, i -> results.put(i, a.get(i) + b.get(i)));
    }

}
