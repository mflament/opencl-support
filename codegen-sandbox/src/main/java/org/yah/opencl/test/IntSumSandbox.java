package org.yah.opencl.test;

import org.lwjgl.BufferUtils;

import org.yah.opencl.test.programs.SumProgramInt;
import org.yah.opencl.test.programs.cl.CLSumProgramInt;
import org.yah.opencl.test.programs.kernels.MySumKernelInt;
import org.yah.tools.opencl.ndrange.NDRange;
import org.yah.tools.opencl.ndrange.NDRange1;
import org.yah.tools.opencl.context.CLContext;

import java.nio.IntBuffer;

import static org.yah.opencl.test.SandboxSupport.*;

public class IntSumSandbox implements AutoCloseable {

    private static final int SIZE = 100_000_000;
    public static final int RUNS = 100;

    public static void main(String[] args) throws Exception {
        runInCOntext(context -> new IntSumSandbox(context).run());
    }

    private final SumProgramInt sumProgram;
    private final MySumKernelInt sumKernel;

    public IntSumSandbox(CLContext context) {
        this.sumProgram = new CLSumProgramInt(context);
        this.sumKernel = sumProgram.createMySumKernelInt();
    }

    private void run() {
        int count = SIZE;
        IntBuffer a = BufferUtils.createIntBuffer(count);
        IntBuffer b = BufferUtils.createIntBuffer(count);
        IntBuffer results = BufferUtils.createIntBuffer(count);
        IntBuffer expectedResults = BufferUtils.createIntBuffer(count);

        randomize(a, 0, 10000);
        randomize(b, 0, 10000);

        timed("sum host", () -> localSum(a, b, expectedResults), RUNS);

        long localWorkSize = 128;
        long groups = ceilDiv(count, localWorkSize);
        NDRange1 range = NDRange.range1().globalWorkSize(groups * localWorkSize).localWorkSize(localWorkSize);

        sumKernel.createA(a).createB(b).createResults(count).setLength(count);
        timed("sum device", () -> sumKernel.invoke(range).readResults(results), RUNS);

        compare("sum", results, expectedResults);
    }

    @Override
    public void close() {
        sumKernel.close();
        sumProgram.close();
    }

    private static void localSum(IntBuffer a, IntBuffer b, IntBuffer results) {
        parallelFor(a, i -> results.put(i, a.get(i) + b.get(i)));
    }

}
