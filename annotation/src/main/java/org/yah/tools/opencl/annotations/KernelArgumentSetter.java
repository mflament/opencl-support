package org.yah.tools.opencl.annotations;

import org.yah.tools.opencl.kernel.CLKernel;

import java.util.function.BiConsumer;

@FunctionalInterface
public interface KernelArgumentSetter extends BiConsumer<CLKernel, Object[]> {
}
