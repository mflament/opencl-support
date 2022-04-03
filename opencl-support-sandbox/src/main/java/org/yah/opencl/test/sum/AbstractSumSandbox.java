package org.yah.opencl.test.sum;

import org.yah.opencl.test.programs.SumProgram;
import org.yah.opencl.test.programs.kernels.MySumKernel;
import org.yah.tools.opencl.context.CLContext;
import org.yah.tools.opencl.ndrange.NDRange;
import org.yah.tools.opencl.ndrange.NDRange1;

import java.nio.Buffer;

import static org.yah.opencl.test.SandboxSupport.ceilDiv;
import static org.yah.opencl.test.SandboxSupport.timed;

abstract class AbstractSumSandbox<T extends Buffer> implements AutoCloseable {
    private final SumProgram<T> sumProgram;
    private final MySumKernel<T> sumKernel;

    public AbstractSumSandbox(CLContext context) {
        this.sumProgram = createProgram(context);
        this.sumKernel = sumProgram.mySum();
    }

    protected abstract SumProgram<T> createProgram(CLContext context);

    protected abstract T createBuffer(int count);

    protected abstract void randomize(T buffer, double low, double height);

    protected abstract void compare(String message, T actual, T expected);

    protected abstract void localSum(T a, T b, T results);

    public final void run(int runs, int count) {
        T a = createBuffer(count);
        T b = createBuffer(count);
        T results = createBuffer(count);
        T expectedResults = createBuffer(count);

        randomize(a, 0, 10000);
        randomize(b, 0, 10000);

        timed("sum host", () -> localSum(a, b, expectedResults), runs);

        long localWorkSize = 128;
        long groups = ceilDiv(count, localWorkSize);
        NDRange1 range = NDRange.range1().globalWorkSize(groups * localWorkSize).localWorkSize(localWorkSize);

        sumKernel.createA(a).createB(b).createResults(count).setLength(count);
        timed("sum device", () -> sumKernel.invoke(range).readResults(results), runs);

        compare("sum", results, expectedResults);
    }

    @Override
    public void close() {
        sumKernel.close();
        sumProgram.close();
    }

}
