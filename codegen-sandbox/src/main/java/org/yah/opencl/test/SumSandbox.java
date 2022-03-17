package org.yah.opencl.test;

import org.lwjgl.BufferUtils;
import org.yah.opencl.test.programs.SumProgram;
import org.yah.opencl.test.programs.cl.CLSumProgram;
import org.yah.opencl.test.programs.kernels.SumKernel;
import org.yah.tools.opencl.cmdqueue.NDRange;
import org.yah.tools.opencl.context.CLContext;

import java.nio.IntBuffer;
import java.util.Random;

public class SumSandbox {
    public static void main(String[] args) {
        try (CLContext context = CLContext.builder().build();
             SumProgram sumProgram = new CLSumProgram(context);
             SumKernel sumKernel = sumProgram.createSumKernel()) {
            run(sumKernel);
        }
    }

    private static void run(SumKernel kernel) {
        int count = 1_000_000;
        IntBuffer a = randomize(count);
        IntBuffer b = randomize(count);
        IntBuffer results = BufferUtils.createIntBuffer(count);
        IntBuffer expectedResults = BufferUtils.createIntBuffer(count);

        localSum(a, b, expectedResults);
        compare(expectedResults, expectedResults);

        int localWorkSize = 32;
        int groups = (int) Math.ceil(count / (float)localWorkSize);
        NDRange range = new NDRange(1).globalWorkSize(groups * localWorkSize).localWorkSize(localWorkSize);

        kernel.createA(a).createB(b).createResults(count).setLength(count)
                .invoke(range)
                .readResults(results);

        compare(results, expectedResults);
        System.out.println("success");
    }

    private static void compare(IntBuffer expected, IntBuffer actual) {
        if (expected.remaining() != actual.remaining())
            throw new IllegalArgumentException("size mismatch " + expected.remaining() + " != " + actual.remaining());
        for (int i = 0; i < expected.remaining(); i++) {
            int expectedValue = expected.get(i);
            int actualValue = actual.get(i);
            if (expectedValue != actualValue)
                throw new IllegalArgumentException(String.format("value[%d] mismatch %d != %d", i,  expectedValue, actualValue));
        }
    }

    private static void localSum(IntBuffer a, IntBuffer b, IntBuffer results) {
        for (int i = 0; i < results.remaining(); i++) {
            results.put(i, a.get(i) + b.get(i));
        }
    }

    private static IntBuffer randomize(int count) {
        IntBuffer results = BufferUtils.createIntBuffer(count);
        for (int i = 0; i < count; i++) {
            results.put(i, RANDOM.nextInt(1000000));
        }
        return results;
    }

    private static final Random RANDOM = new Random();

}
