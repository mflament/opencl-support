package org.yah.tools.opencl.sandbox;

import java.io.IOException;
import java.util.Random;

import org.yah.tools.opencl.CLEnvironment;

public abstract class AbstractCLSandbox implements AutoCloseable {

    protected final CLEnvironment environment;

    protected final Random random;

    public AbstractCLSandbox(String sourceResource) throws IOException {
        this(CLEnvironment.builder().withSourceResource(sourceResource).build());
    }

    public AbstractCLSandbox(CLEnvironment environment) {
        this.environment = environment;
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

}
