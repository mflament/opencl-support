package org.yah.tools.opencl;

public interface CLObject extends AutoCloseable {

    long getId();

    @Override
    default void close() {}
}
