package org.yah.tools.opencl.sandbox;

import org.lwjgl.BufferUtils;
import org.yah.tools.opencl.ndrange.NDRange;
import org.yah.tools.opencl.ndrange.NDRange1;
import org.yah.tools.opencl.kernel.CLKernel;
import org.yah.tools.opencl.mem.CLBuffer;

import java.nio.FloatBuffer;

import static org.yah.tools.opencl.enums.BufferProperty.*;

/**
 * @author Yah
 */
@SuppressWarnings("unused")
public class VectorSum extends AbstractCLSandbox {
    private static final float EPSILON = 1E-4f;

    private final FloatBuffer values;

    public VectorSum(int valuesCount) {
        super("vector_sum.cl");
        values = randomFloats(valuesCount);
    }

    public float clsum() {
        int count = values.capacity();
        int workGroupSize = 128;

        int workItems = Math.max(div(count, 2), workGroupSize);
        int workGroups = div(workItems, workGroupSize);

        CLBuffer cl_values = manage(mem(values, MEM_COPY_HOST_PTR, MEM_HOST_WRITE_ONLY, MEM_READ_ONLY));

        FloatBuffer results = BufferUtils.createFloatBuffer(workGroups);
        CLBuffer cl_results = manage(mem(results.capacity(), MEM_ALLOC_HOST_PTR, MEM_READ_WRITE, MEM_HOST_READ_ONLY));

        CLKernel kernel = program.newKernel("sum");
        NDRange1 range = NDRange.range1();
        range.globalWorkSize(workItems).localWorkSize(workGroupSize);

        kernel.setArg(0, cl_values);
        kernel.setArg(1, cl_results);
        kernel.setArg(2, count);
        kernel.setArg(3, workGroupSize * Float.BYTES);

        commandQueue.run(kernel, range);

        commandQueue.read(cl_results, results);
        commandQueue.finish();

        return 0;
    }

    private float cpusum(int threads) {
        FloatBuffer results = FloatBuffer.allocate(threads);
        boolean interrupted = parallelFor(values.capacity(), threads,
                (i, start, end) ->  results.put(i, cpusum(values, start, end)));
        if (!interrupted) {
            return cpusum(results, 0, results.capacity());
        }
        return 0;
    }

    public static float cpusum(FloatBuffer values, int startIndex, int endIndex) {
        float res = 0;
        for (int i = startIndex; i < endIndex; i++) {
            res += values.get(i);
        }
        return res;
    }

    private static void test(int size) {
        try (VectorSum vs = new VectorSum(size)) {
            float ssum = vs.cpusum(1);
            float psum = vs.cpusum(getAvailableProcessors());
            float clsum = vs.clsum();
//            float clsum = 0;
            boolean same = compare(ssum, psum) && compare(ssum, clsum);
            if (!same)
                System.err.printf("ssum=%f , psum=%f, clsum= %f", ssum, psum, clsum);
            else
                System.out.println("success");
        }
    }

    public static void main(String[] args) {
        //int size = 268435456; // 1 Gb of floats
        int size = 10; // 1 Gb of floats
        //benchmark(size);
        test(size);
    }
}
