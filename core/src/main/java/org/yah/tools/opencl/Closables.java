package org.yah.tools.opencl;

import java.util.LinkedList;
import java.util.List;

public class Closables implements AutoCloseable {
    private final List<AutoCloseable> closeables = new LinkedList<>();

    public <T extends AutoCloseable> T add(T closeable) {
        closeables.add(closeable);
        // could proxy result to remove on close ... but heee, later
        return closeable;
    }

    @Override
    public void close() {
        for (AutoCloseable closeable : closeables) {
            try {
                closeable.close();
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        }
        closeables.clear();
    }
}
