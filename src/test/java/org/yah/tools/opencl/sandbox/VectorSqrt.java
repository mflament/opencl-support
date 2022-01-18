package org.yah.tools.opencl.sandbox;

import org.lwjgl.BufferUtils;
import org.yah.tools.opencl.CLUtils;
import org.yah.tools.opencl.cmdqueue.NDRange;
import org.yah.tools.opencl.kernel.CLKernel;
import org.yah.tools.opencl.mem.CLBuffer;

import java.io.IOException;
import java.nio.FloatBuffer;

import static org.yah.tools.opencl.enums.BufferProperties.*;
import static org.yah.tools.opencl.enums.BufferProperties.MEM_HOST_READ_ONLY;

public class VectorSqrt extends AbstractCLSandbox {

    private final FloatBuffer values;
    private final CLKernel kernel;

    public VectorSqrt(int size) throws IOException {
        super("vector_sqrt.cl");
        kernel = program.kernel("vector_sqrt");
        values = randomFloats(size);
    }

    @Override
    public void close() {
        super.close();
    }

    private BenchmarkTask cpusqrt(FloatBuffer results, int threads) {
        int size = values.capacity();
        return new BenchmarkTask(() -> {
            parallelFor(values.capacity(), threads, (thread, start, end) -> {
                for (int i = start; i < end; i++) {
                    results.put(i, (float) Math.sqrt(values.get(i)));
                }
            });
        });
    }

    private BenchmarkTask clsqrt(FloatBuffer results) {
        int workGroupSize = 512;
        int count = values.capacity();
        int workItems = Math.max(workGroupSize, CLUtils.nextPowerOfTwo(count));
//        int workGroups = div(workItems, workGroupSize);

        CLBuffer cl_values = manage(mem(values, MEM_COPY_HOST_PTR, MEM_READ_ONLY, MEM_HOST_WRITE_ONLY));
        CLBuffer cl_results = manage(mem(count * Float.BYTES, MEM_ALLOC_HOST_PTR, MEM_WRITE_ONLY, MEM_HOST_READ_ONLY));

        NDRange range = new NDRange(kernel.getMaxDimensions());
        range.set(new long[]{workItems}, new long[]{workGroupSize});
        return new BenchmarkTask(() -> {
            range.requestEvent();
            kernel.setArg(0, cl_values);
            kernel.setArg(1, cl_results);
            kernel.setArg(2, count);
            long event = commandQueue.run(kernel, range);
            commandQueue.waitForEvent(event);
            commandQueue.read(cl_results, results);
            commandQueue.finish();
        }, () -> {
            cl_results.close();
            cl_values.close();
        });
    }

    private static void benchmark(int size) throws IOException {
        int threads = getAvailableProcessors();
        System.out.println("Populating values");
        FloatBuffer results = BufferUtils.createFloatBuffer(size);

        try (VectorSqrt vs = new VectorSqrt(size)) {
            BenchmarkResult result;
            System.out.println("Starting benchmarks");

            result = benchmark(vs.cpusqrt(results, threads));
            System.out.printf("cpusqrt(%d floats, %d threads): %s%n", size, threads, result);

            result = benchmark(vs.clsqrt(results));
            System.out.printf("clsqrt(%d floats): %s%n", size, result);
        }
    }

    public static void main(String[] args) throws IOException {
        int size = 268435456; // 1 Gb of floats
        //int size = 1000; // 1 Gb of floats
        benchmark(size);
        test(size);
    }

    private static void test(int size) throws IOException {
        try (VectorSqrt vs = new VectorSqrt(size)) {
            FloatBuffer clResults = BufferUtils.createFloatBuffer(size);
            vs.clsqrt(clResults).run();

            FloatBuffer cpuResults = FloatBuffer.allocate(size);
            vs.cpusqrt(cpuResults, getAvailableProcessors()).run();

            for (int i = 0; i < size; i++) {
                if (!compare(clResults.get(i), cpuResults.get(i)))
                    throw new IllegalStateException("failed");
            }
        }
    }

}
