package org.yah.opencl.test;

import org.yah.tools.opencl.context.CLContext;

import java.nio.Buffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Random;
import java.util.function.*;
import java.util.stream.IntStream;

public final class SandboxSupport {

    private SandboxSupport() {
    }

    private static final double EPSILON = 10e-3f;

    public static void runInContext(Consumer<CLContext> consumer) throws Exception {
        runAndClose(() -> CLContext.builder().build(), consumer);
    }

    public static <K extends AutoCloseable> void runAndClose(Supplier<K> supplier, Consumer<K> consumer) throws Exception {
        try (K kernel = supplier.get()) {
            consumer.accept(kernel);
        }
    }

    public static void compare(String name, IntBuffer expected, IntBuffer actual) {
        compare(name, expected, actual, SandboxSupport::compareValue, IntBuffer::get);
    }

    public static void compare(String name, FloatBuffer expected, FloatBuffer actual) {
        compare(name, expected, actual, SandboxSupport::compareValue, FloatBuffer::get);
    }

    public static void compare(String name, DoubleBuffer expected, DoubleBuffer actual) {
        compare(name, expected, actual, SandboxSupport::compareValue, DoubleBuffer::get);
    }

    public static long ceilDiv(long a, long b) {
        return (long) Math.ceil(a / (float) b);
    }

    public static <B extends Buffer> void compare(String name, B expected, B actual, BiPredicate<B, B> comparator, Function<B, ?> getValue) {
        if (expected.remaining() != actual.remaining())
            throw new IllegalArgumentException(name + " size mismatch " + expected.remaining() + " != " + actual.remaining());

        for (int i = 0; i < expected.remaining(); i++) {
            actual.position(i);
            expected.position(i);
            if (!comparator.test(actual, expected))
                throw new IllegalArgumentException(String.format("%s value[%d] mismatch %s != %s", name, i,
                        getValue.apply(expected), getValue.apply(actual)));
        }
        actual.flip();
        expected.flip();
        System.out.println(name + " matched");
    }

    private static boolean compareValue(IntBuffer expected, IntBuffer actual) {
        return expected.get() == actual.get();
    }

    private static boolean compareValue(FloatBuffer expected, FloatBuffer actual) {
        return Math.abs(expected.get() - actual.get()) < EPSILON;
    }

    private static boolean compareValue(DoubleBuffer expected, DoubleBuffer actual) {
        return Math.abs(expected.get() - actual.get()) < EPSILON;
    }

    public static void timed(String name, Runnable operation) {
        timed(name, operation, 1);
    }

    public static void timed(String name, Runnable operation, int runs) {
        long min = Long.MAX_VALUE, max = Long.MIN_VALUE, total = 0;
        for (int i = 0; i < runs; i++) {
            long start = System.currentTimeMillis();
            operation.run();
            long elapsed = System.currentTimeMillis() - start;
            min = Math.min(min, elapsed);
            max = Math.max(max, elapsed);
            total += elapsed;
        }
        System.out.printf("%s: total: %d avg: %d; min: %d; max: %d (ms)%n", name, total, total / runs, min, max);
    }

    public static void randomize(IntBuffer buffer, int origin, int bound) {
        parallelReduce(buffer, randomCollector((random, index) -> buffer.put(index, random.nextInt(origin, bound))));
    }

    public static void randomize(FloatBuffer buffer, float origin, float bound) {
        parallelReduce(buffer, randomCollector((random, index) -> buffer.put(index, random.nextFloat(origin, bound))));
    }

    public static void randomize(DoubleBuffer buffer, double origin, double bound) {
        parallelReduce(buffer, randomCollector((random, index) -> buffer.put(index, random.nextDouble(origin, bound))));
    }

    private static ParallelForCollector<Random> randomCollector(ObjIntConsumer<Random> accumulator) {
        return new ParallelForCollector<>(Random::new, accumulator);
    }


    public static void parallelFor(Buffer buffer, IntConsumer handler) {
        parallelFor(buffer.position(), buffer.remaining(), handler);
    }

    public static void parallelFor(int start, int length, IntConsumer handler) {
        IntStream.range(start, start + length).parallel().forEach(handler);
    }

    public static <C> C parallelReduce(Buffer buffer, ParallelForCollector<C> collector) {
        return parallelReduce(buffer.position(), buffer.remaining(), collector);
    }

    public static <C> C parallelReduce(int start, int length, ParallelForCollector<C> collector) {
        return IntStream.range(start, start + length).parallel()
                .collect(collector.threadContextSupplier, collector.accumulator, collector.contextMerger);
    }

    public static final class ParallelForCollector<C> {

        public static final BiConsumer<?, ?> NO_OP_MERGER = (a, b) -> {
            // no op
        };

        private final Supplier<C> threadContextSupplier;
        private final ObjIntConsumer<C> accumulator;
        private final BiConsumer<C, C> contextMerger;

        public ParallelForCollector(Supplier<C> threadContextSupplier, ObjIntConsumer<C> accumulator, BiConsumer<C, C> contextMerger) {
            this.threadContextSupplier = threadContextSupplier;
            this.accumulator = accumulator;
            this.contextMerger = contextMerger;
        }

        @SuppressWarnings("unchecked")
        public ParallelForCollector(Supplier<C> threadContextSupplier, ObjIntConsumer<C> accumulator) {
            this(threadContextSupplier, accumulator, (BiConsumer<C, C>) NO_OP_MERGER);
        }
    }

}
