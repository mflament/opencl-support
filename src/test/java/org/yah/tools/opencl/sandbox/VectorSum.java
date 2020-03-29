/**
 * 
 */
package org.yah.tools.opencl.sandbox;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.IntFunction;

import org.lwjgl.BufferUtils;
import org.yah.tools.opencl.cmdqueue.CLCommandQueue.KernelNDRange;
import org.yah.tools.opencl.kernel.CLKernel;
import org.yah.tools.opencl.mem.BufferProperties;
import org.yah.tools.opencl.mem.CLBuffer;

/**
 * @author Yah
 *
 */
@SuppressWarnings("unused")
public class VectorSum extends AbstractCLSandbox {

    private static final int MAX_CONCURRENCY = 32;

    private static final float EPSILON = 1E-4f;

    protected final Object values;
    protected final int size;

    private final int workGroupSize;
    private final CLBuffer clvalues;

    private final ByteBuffer resultBuffer;
    private final CLBuffer clresult;

    private final KernelNDRange range;
    private final CLKernel kernel;

    private final ExecutorService executor;

    private final IntFunction<Object> arrayFactory;
    private final ResultsReader resultsReader;
    private final int elementBytes;

    private final CpuSumTask<?>[] cpuTasks = new CpuSumTask[MAX_CONCURRENCY];

    @FunctionalInterface
    private interface ResultsReader {
        double read(ByteBuffer buffer);
    }

    public VectorSum(String type, int size, int wgSize) throws IOException {
        super("vector_sum.cl", "-DTYPE=" + type);

        this.size = size;
        workGroupSize = nextPowerOfTwo(wgSize);
        int workItems = div(size, 2);
        int workGroups = div(workItems, workGroupSize);

        IntFunction<CpuSumTask<?>> taskFactory;
        ByteBuffer buffer;
        if (type == null || type == "float") {
            arrayFactory = s -> new float[s];
            elementBytes = Float.BYTES;
            taskFactory = FloatSumTask::new;
            resultsReader = b -> b.getFloat(0);
        } else if (type == "double") {
            arrayFactory = s -> new double[s];
            elementBytes = Double.BYTES;
            taskFactory = DoubleSumTask::new;
            resultsReader = b -> b.getDouble(0);
        } else if (type == "int") {
            arrayFactory = s -> new int[s];
            elementBytes = Integer.BYTES;
            taskFactory = IntSumTask::new;
            resultsReader = b -> b.getInt(0);
        } else
            throw new IllegalArgumentException("Invalid type " + type);

        values = arrayFactory.apply(size);
        buffer = BufferUtils.createByteBuffer(size * elementBytes);
        randomize(values, buffer);
        clvalues = environment.mem(buffer, BufferProperties.MEM_COPY_HOST_PTR,
                BufferProperties.MEM_HOST_WRITE_ONLY,
                BufferProperties.MEM_READ_ONLY);

        resultBuffer = BufferUtils.createByteBuffer(workGroups * elementBytes);
        clresult = environment.mem(resultBuffer.capacity(), BufferProperties.MEM_ALLOC_HOST_PTR,
                BufferProperties.MEM_READ_WRITE,
                BufferProperties.MEM_HOST_READ_ONLY);

        kernel = environment.kernel("sum");
        range = environment.kernelRange();
        executor = Executors.newFixedThreadPool(MAX_CONCURRENCY);

        for (int i = 0; i < cpuTasks.length; i++) {
            cpuTasks[i] = taskFactory.apply(i);
        }
    }

    @Override
    public void close() {
        executor.shutdown();
        super.close();
    }

    private void randomize(Object values, ByteBuffer buffer) {
        Class<?> componentType = values.getClass().getComponentType();
        if (componentType == Double.TYPE) {
            randomize((double[]) values, buffer);
        } else if (values.getClass().getComponentType() == Float.TYPE) {
            randomize((float[]) values, buffer);
        } else if (values.getClass().getComponentType() == Integer.TYPE) {
            randomize((int[]) values, buffer);
        }
    }

    private void randomize(double[] values, ByteBuffer buffer) {
        for (int i = 0; i < values.length; i++) {
            values[i] = random.nextGaussian();
            buffer.putDouble(values[i]);
        }
        buffer.flip();
    }

    private void randomize(float[] values, ByteBuffer buffer) {
        for (int i = 0; i < values.length; i++) {
            values[i] = (float) random.nextGaussian();
            buffer.putFloat(values[i]);
        }
        buffer.flip();
    }

    private void randomize(int[] values, ByteBuffer buffer) {
        for (int i = 0; i < values.length; i++) {
            values[i] = random.nextInt(2000) - 1000;
            buffer.putInt(values[i]);
        }
        buffer.flip();
    }

    public double clsum() {
        CLBuffer inputs = clvalues;
        int size = this.size;
        long event = 0;
        while (size > 1) {
            int workItems = div(size, 2);
            int workGroups = div(workItems, workGroupSize);
            workItems = workGroups * workGroupSize;
            event = clsum(inputs, workItems, size, event);
            size = workGroups;
            inputs = clresult;
        }
        range.reset().setEventWaitList(event);
        environment.read(clresult, resultBuffer);
        environment.finish();

        return resultsReader.read(resultBuffer);
    }

    private long clsum(CLBuffer inputs, int workItems, int size, long waitFor) {
        int groupSize = Math.min(workGroupSize, workItems);
        range.globalWorkSizes(workItems);
        range.localWorkSizes(groupSize);
        if (waitFor != 0)
            range.setEventWaitList(waitFor);
        range.requestEvent();
        kernel.setArg(0, inputs);
        kernel.setArg(1, clresult);
        kernel.setArg(2, size);
        kernel.setArgSize(3, elementBytes * groupSize);
        return environment.run(kernel, range);
    }

    public double cpusum(int concurrency) {
        if (concurrency > 1) {
            int chunkSize = size / concurrency;
            int offset = 0;
            Object results = arrayFactory.apply(concurrency - 1);
            CountDownLatch latch = new CountDownLatch(concurrency - 1);
            CpuSumTask<?> task;
            for (int i = 0; i < concurrency - 1; i++, offset += chunkSize) {
                task = cpuTasks[i];
                task.prepare(values, offset, chunkSize, results, latch);
                executor.submit(task);
            }

            task = cpuTasks[concurrency - 1];
            double res = task.sum(values, offset, chunkSize + size % concurrency);
            try {
                latch.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            task = cpuTasks[0];
            res += task.sum(results, 0, concurrency - 1);
            return res;
        } else {
            CpuSumTask<?> task = cpuTasks[0];
            return task.sum(values, 0, size);
        }
    }

    private static abstract class CpuSumTask<T> implements Runnable {
        protected final int index;
        private int offset;
        private int length;
        private Object values;
        private Object results;

        private CountDownLatch latch;

        public CpuSumTask(int index) {
            this.index = index;
        }

        public void prepare(Object values, int offset, int length, Object results, CountDownLatch latch) {
            this.values = values;
            this.offset = offset;
            this.length = length;
            this.latch = latch;
            this.results = results;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            try {
                sum((T) values, offset, length, (T) results);
            } catch (RuntimeException e) {
                e.printStackTrace();
            } finally {
                latch.countDown();
            }
        }

        @SuppressWarnings("unchecked")
        public final double sum(Object values, int offset, int length) {
            return sum((T) values, offset, length, null);
        }

        protected abstract double sum(T values, int offset, int length, T results);

    }

    private static class DoubleSumTask extends CpuSumTask<double[]> {

        public DoubleSumTask(int index) {
            super(index);
        }

        @Override
        public double sum(double[] values, int offset, int length, double[] results) {
            double total = 0;
            for (int i = offset; i < offset + length; i++) {
                total += values[i];
            }
            if (results != null)
                results[index] = total;
            return total;
        }
    }

    private static class FloatSumTask extends CpuSumTask<float[]> {

        public FloatSumTask(int index) {
            super(index);
        }

        @Override
        public double sum(float[] values, int offset, int length, float[] results) {
            float total = 0;
            for (int i = offset; i < offset + length; i++) {
                total += values[i];
            }
            if (results != null)
                results[index] = total;
            return total;
        }
    }

    private static class IntSumTask extends CpuSumTask<int[]> {

        public IntSumTask(int index) {
            super(index);
        }

        @Override
        public double sum(int[] values, int offset, int length, int[] results) {
            int total = 0;
            for (int i = offset; i < offset + length; i++) {
                total += values[i];
            }
            if (results != null)
                results[index] = total;
            return total;
        }
    }

    private static int div(int a, int b) {
        return a / b + (a % b == 0 ? 0 : 1);
    }

    private static final int WARMUP = 1000;
    private static final int RUNS = 1000;
    private static final double NS_MS = 1.0 / TimeUnit.NANOSECONDS.convert(1, TimeUnit.MILLISECONDS);

    private static double benchmark(Runnable action)
            throws IOException {
        for (int i = 0; i < WARMUP; i++) {
            action.run();
        }
        long start;
        double total = 0;
        for (int i = 0; i < RUNS; i++) {
            start = System.nanoTime();
            action.run();
            total += (System.nanoTime() - start) * NS_MS;
        }
        return total / RUNS;
    }

    public static void main(String[] args) throws IOException {
        int size = 1024 * 1024;
        int workGroupSize = 64;
        benchmark(size, workGroupSize);
        // test(36, workGroupSize);
    }

    private static void benchmark(int size, int workGroupSize) throws IOException {
        try (VectorSum vs = new VectorSum("int", size, workGroupSize)) {
            double avg = benchmark(() -> vs.clsum());
            System.out.println(String.format("clsum(%d,%d): %fms", size, workGroupSize, avg));

            avg = benchmark(() -> vs.cpusum(1));
            System.out.println(String.format("cpu1sum(%d,%d): %fms", size, workGroupSize, avg));

            avg = benchmark(() -> vs.cpusum(2));
            System.out.println(String.format("cpu2sum(%d,%d): %fms", size, workGroupSize, avg));

            avg = benchmark(() -> vs.cpusum(4));
            System.out.println(String.format("cpu4sum(%d,%d): %fms", size, workGroupSize, avg));
        }
    }

    private static void test(int size, int workGroupSize) throws IOException {
        try (VectorSum vs = new VectorSum("int", size, workGroupSize)) {
            double clsum = vs.clsum();
            double ssum = vs.cpusum(1);
            double psum = vs.cpusum(4);

            @SuppressWarnings("resource")
            PrintStream ps = Math.abs(clsum - psum) < EPSILON && Math.abs(clsum - ssum) < EPSILON ? System.out
                    : System.err;
            ps.println(clsum);
            ps.println(ssum);
            ps.println(psum);
        }
    }
}
