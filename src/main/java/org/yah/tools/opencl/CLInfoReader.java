package org.yah.tools.opencl;

import java.nio.ByteBuffer;
import java.util.function.Function;

public interface CLInfoReader<T> extends Function<ByteBuffer, T> {}
