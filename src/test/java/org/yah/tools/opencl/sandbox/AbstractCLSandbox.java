package org.yah.tools.opencl.sandbox;

import org.lwjgl.BufferUtils;
import org.yah.tools.opencl.Closables;
import org.yah.tools.opencl.cmdqueue.CLCommandQueue;
import org.yah.tools.opencl.context.CLContext;
import org.yah.tools.opencl.enums.BufferProperties;
import org.yah.tools.opencl.mem.CLBuffer;
import org.yah.tools.opencl.program.CLProgram;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public abstract class AbstractCLSandbox implements AutoCloseable {

    protected final CLContext context;
    protected final CLProgram program;
    protected final CLCommandQueue commandQueue;

    private final Closables managedResources = new Closables();

    public AbstractCLSandbox(String sourceFile) throws IOException {
        context = manage(new CLContext());
        program = manage(context.buildProgram().withFile("classpath:" + sourceFile).build());
        commandQueue = manage(context.buildCommandQueue().build());
    }

    @Override
    public void close() {
        managedResources.close();
    }

    protected final <T extends AutoCloseable> T manage(T closable) {
        return managedResources.add(closable);
    }

    protected final CLBuffer mem(FloatBuffer buffer, BufferProperties... properties) {
        return context.buildBuffer().withProperties(properties).build(buffer);
    }

    protected final CLBuffer mem(int capacity, BufferProperties... properties) {
        return context.buildBuffer().withProperties(properties).build(capacity);
    }

    private static final int WARMUP = 10;
    private static final int RUNS = 50;

    protected static BenchmarkResult benchmark(BenchmarkTask task) {
        try (task) {
            return benchmark((Runnable) task);
        }
    }

    protected static BenchmarkResult benchmark(Runnable action) {
        for (int i = 0; i < WARMUP; i++) {
            action.run();
        }
        long start;
        double min = Double.MAX_VALUE, max = Double.MIN_VALUE, total = 0;
        for (int i = 0; i < RUNS; i++) {
            start = System.nanoTime();
            action.run();
            long elapsed = TimeUnit.MILLISECONDS.convert(System.nanoTime() - start, TimeUnit.NANOSECONDS);
            total += elapsed;
            min = Math.min(min, elapsed);
            max = Math.max(max, elapsed);
        }
        return new BenchmarkResult(min, max, total / RUNS);
    }

    protected FloatBuffer randomFloats(int valuesCount) {
        final FloatBuffer values;
        values = BufferUtils.createFloatBuffer(valuesCount);
        final ThreadLocal<Random> randoms = ThreadLocal.withInitial(Random::new);
        IntStream.range(0, valuesCount).parallel().forEach(i -> values.put(i, randoms.get().nextFloat()));
        return values;
    }

    protected static int div(int a, float b) {
        return (int) Math.ceil(a / b);
    }

    protected static boolean compare(float a, float b) {
        return Math.abs(a - b) < 0.01f;
    }

    protected static int getAvailableProcessors() {
        return Runtime.getRuntime().availableProcessors();
    }

    private static void close(AutoCloseable closeable) {
        try {
            closeable.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected static boolean parallelFor(int count, int threads, ParallelTask task) {
        return parallelFor(0, count, threads, task);
    }

    protected static boolean parallelFor(int start, int end, int threads, ParallelTask task) {
        if (threads <= 0)
            throw new IllegalArgumentException("invalid threads " + threads);

        int count = end - start;
        if (count <= 0)
            return true;

        threads = Math.min(threads, count);
        int chunk = count / threads;
        var runnables = new ArrayList<Runnable>(threads);
        var latch = new CountDownLatch(threads - 1);
        for (int i = 0; i < threads - 1; i++) {
            int index = i;
            int threadStart = start + i * chunk;
            int threadEnd = threadStart + chunk;
            new Thread(() -> {
                task.run(index, threadStart, threadEnd);
                latch.countDown();
            }, "parallel-for-" + i).start();
        }

        int index = threads - 1;
        int threadStart = start + index * chunk;
        int threadEnd = threadStart + chunk + (count % threads);
        task.run(index, threadStart, threadEnd);

        try {
            latch.await();
            return true;
        } catch (InterruptedException e) {
            return false;
        }
    }

    @FunctionalInterface
    protected interface ParallelTask {
        void run(int index, int start, int end);
    }

    protected static class BenchmarkTask implements AutoCloseable, Runnable {
        private final Runnable run;
        private final Runnable close;

        public BenchmarkTask(Runnable run) {
            this(run, null);
        }

        public BenchmarkTask(Runnable run, @Nullable Runnable close) {
            this.run = run;
            this.close = close;
        }

        @Override
        public void run() {
            run.run();
        }

        @Override
        public void close() {
            if (close != null)
                close.run();
        }

    }
}
