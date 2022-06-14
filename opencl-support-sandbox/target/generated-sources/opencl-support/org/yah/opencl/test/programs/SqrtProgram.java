package org.yah.opencl.test.programs;

import org.yah.opencl.test.programs.kernels.MySqrtKernel;

public interface SqrtProgram extends AutoCloseable {

    /**
     * void mySqrt(global double* a,
     *             global double* results,
     *             private int length)
     */
    MySqrtKernel mySqrt();

    @Override
    void close();
}
