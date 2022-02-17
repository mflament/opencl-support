package org.yah.tools.opencl.annotations;

import org.yah.tools.opencl.cmdqueue.NDRange;

import java.util.function.Consumer;

public interface NDRangeConfigurer extends Consumer<NDRange> {
    int getDimensions();
}
