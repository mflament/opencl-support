package org.yah.opencl.test.programs;

import java.nio.Buffer;
import org.yah.opencl.test.programs.kernels.Reduce0Kernel;
import org.yah.opencl.test.programs.kernels.Reduce1Kernel;
import org.yah.opencl.test.programs.kernels.Reduce2Kernel;
import org.yah.opencl.test.programs.kernels.Reduce3Kernel;
import org.yah.opencl.test.programs.kernels.Reduce4Kernel;
import org.yah.opencl.test.programs.kernels.Reduce5Kernel;
import org.yah.opencl.test.programs.kernels.Reduce6Kernel;

public interface OclReductionProgram<T extends Buffer> extends AutoCloseable {

    /**
     * void reduce0(global const T* g_idata,
     *              global T* g_odata,
     *              private uint n,
     *              local T* sdata)
     */
    Reduce0Kernel<T> reduce0();

    /**
     * void reduce1(global const T* g_idata,
     *              global T* g_odata,
     *              private uint n,
     *              local T* sdata)
     */
    Reduce1Kernel<T> reduce1();

    /**
     * void reduce2(global const T* g_idata,
     *              global T* g_odata,
     *              private uint n,
     *              local T* sdata)
     */
    Reduce2Kernel<T> reduce2();

    /**
     * void reduce3(global const T* g_idata,
     *              global T* g_odata,
     *              private uint n,
     *              local T* sdata)
     */
    Reduce3Kernel<T> reduce3();

    /**
     * void reduce4(global const T* g_idata,
     *              global T* g_odata,
     *              private uint n,
     *              local volatile T* sdata)
     */
    Reduce4Kernel<T> reduce4();

    /**
     * void reduce5(global const T* g_idata,
     *              global T* g_odata,
     *              private uint n,
     *              local volatile T* sdata)
     */
    Reduce5Kernel<T> reduce5();

    /**
     * void reduce6(global const T* g_idata,
     *              global T* g_odata,
     *              private uint n,
     *              local volatile T* sdata)
     */
    Reduce6Kernel<T> reduce6();

    @Override
    void close();
}
