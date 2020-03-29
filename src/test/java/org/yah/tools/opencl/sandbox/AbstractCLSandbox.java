package org.yah.tools.opencl.sandbox;

import java.io.IOException;
import java.util.Random;

import org.yah.tools.opencl.CLEnvironment;

public abstract class AbstractCLSandbox implements AutoCloseable {

    protected final CLEnvironment environment;

    protected final Random random;

    public AbstractCLSandbox(String sourceResource) throws IOException {
        this(sourceResource, null);
    }

    public AbstractCLSandbox(String sourceResource, String options) throws IOException {
        environment = new CLEnvironment(sourceResource, options);
        Long seed = parseSeed(System.getProperty("seed"));
        random = seed != null ? new Random(seed) : new Random();
    }

    private Long parseSeed(String property) {
        if (property == null)
            return null;
        try {
            return Long.parseLong(property);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void close() {
        environment.close();
    }
    


    /**
     * @see http://graphics.stanford.edu/~seander/bithacks.html#RoundUpPowerOf2
     */
    public static final int nextPowerOfTwo(int n) {
        n--;
        n |= n >> 1;
        n |= n >> 2;
        n |= n >> 4;
        n |= n >> 8;
        n |= n >> 16;
        n++;
        return n;
    }
}
