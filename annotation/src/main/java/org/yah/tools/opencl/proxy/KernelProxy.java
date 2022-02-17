package org.yah.tools.opencl.proxy;

import org.yah.tools.opencl.annotations.KernelArgumentSetter;
import org.yah.tools.opencl.annotations.NDRangeConfigurer;
import org.yah.tools.opencl.cmdqueue.CLCommandQueue;
import org.yah.tools.opencl.cmdqueue.NDRange;
import org.yah.tools.opencl.kernel.CLKernel;

import java.util.Objects;

class KernelProxy {
    private final CLKernel kernel;
    private final NDRange range;
    private final KernelArgumentSetter argumentsSetter;
    private final NDRangeConfigurer rangeConfigurer;
    private final boolean async;

    private KernelProxy(CLKernel kernel, int dimensions,
                        KernelArgumentSetter argumentsSetter,
                        NDRangeConfigurer rangeConfigurer,
                        boolean async) {
        this.kernel = Objects.requireNonNull(kernel, "kernel is null");
        this.range = new NDRange(dimensions);
        this.argumentsSetter = Objects.requireNonNull(argumentsSetter, "argumentsSetter is null");
        this.rangeConfigurer = Objects.requireNonNull(rangeConfigurer, "rangeConfigurer is null");
        this.async = async;
    }

    Object invoke(CLCommandQueue commandQueue, Object[] args) {
        argumentsSetter.accept(kernel, args);
        rangeConfigurer.accept(range);
        range.requestEvent();
        long event = commandQueue.run(kernel, range);
        if (async)
            return event;
        commandQueue.waitForEvent(event);
        return null;
    }

    public static Builder builder(CLKernel kernel) {
        return new Builder(kernel);
    }

    public static final class Builder {
        private final CLKernel kernel;
        private int dimensions;
        private KernelArgumentSetter argumentsSetter;
        private NDRangeConfigurer rangeConfigurer;
        private boolean async;

        private Builder(CLKernel kernel) {
            this.kernel = Objects.requireNonNull(kernel, "kernel is null");
        }

        public Builder withDimensions(int dimensions) {
            this.dimensions = dimensions;
            return this;
        }

        public Builder withArgumentsSetter(KernelArgumentSetter argumentsSetter) {
            this.argumentsSetter = argumentsSetter;
            return this;
        }

        public Builder withRangeConfigurer(NDRangeConfigurer rangeConfigurer) {
            this.rangeConfigurer = rangeConfigurer;
            return this;
        }

        public Builder withAsync(boolean async) {
            this.async = async;
            return this;
        }

        public KernelProxy build() {
            return new KernelProxy(kernel, dimensions, argumentsSetter, rangeConfigurer, async);
        }
    }
}
