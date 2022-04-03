package org.yah.opencl.test.sum;

import static org.yah.opencl.test.SandboxSupport.*;

public class SumSandbox {

    private static final int SIZE = 100_000_000;
//    private static final int SIZE = 100;
    public static final int RUNS = 100;

    public static void main(String[] args) throws Exception {
        runInContext(context -> {
            new IntSumSandbox(context).run(RUNS, SIZE);
            new FloatSumSandbox(context).run(RUNS, SIZE);
        });
    }

}
