package org.yah.opencl.test;

import org.yah.opencl.test.programs.cl.SumProgram;
import org.yah.opencl.test.programs.cl.kernels.SumKernel;
import org.yah.tools.opencl.cmdqueue.NDRange;
import org.yah.tools.opencl.context.CLContext;
import org.yah.tools.opencl.generated.CLGeneratedProgramBuilder;

import java.util.Arrays;
import java.util.Random;

public class SumSandbox {
    public static void main(String[] args) {
        try (CLContext context = CLContext.builder().build();
             SumProgram sumProgram = new CLGeneratedProgramBuilder<>(context, SumProgram.class).build();
             SumKernel sumKernel = sumProgram.sumKernel()) {
            run(sumKernel);
        }
    }

    private static void run(SumKernel kernel) {
        int count = 10;
        int[] a = randomize(count);
        int[] b = randomize(count);
        int[] results = new int[count];
        int[] expectedResults = new int[count];
        localSum(a, b, expectedResults);
        int localWorkSize = 32;
        int globalWorkSize = (int) Math.ceil(localWorkSize / (float) count);
        NDRange range = new NDRange(1).globalWorkSize(globalWorkSize).localWorkSize(localWorkSize);
        kernel.invoke(range, a, b, results, a.length);
        if (Arrays.equals(results, expectedResults))
            System.out.println("match");
        else
            System.out.println("mismatch");
    }

    private static void localSum(int[] a, int[] b, int[] results) {
        for (int i = 0; i < results.length; i++) {
            results[i] = a[i] + b[i];
        }
    }

    private static int[] randomize(int count) {
        int[] results = new int[count];
        for (int i = 0; i < count; i++) {
            results[i] = RANDOM.nextInt(1000000);
        }
        return results;
    }

    private static final Random RANDOM = new Random();

}
