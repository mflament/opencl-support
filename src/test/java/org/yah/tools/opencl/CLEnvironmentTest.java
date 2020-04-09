package org.yah.tools.opencl;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.junit.Test;
import org.lwjgl.BufferUtils;
import org.yah.tools.opencl.kernel.CLKernel;
import org.yah.tools.opencl.mem.BufferProperties;
import org.yah.tools.opencl.mem.CLBuffer;

public class CLEnvironmentTest {

    @Test
    public void test() throws IOException {
        try (CLEnvironment environment = CLEnvironment.builder().withSourceResource("test.cl").build()) {
            CLKernel kernel = environment.kernel("sum");
            int size = 10000000;
            ByteBuffer buffer = BufferUtils.createByteBuffer(size * Float.BYTES);
            createInputs(buffer.asFloatBuffer(), 0, 1);
            CLBuffer a = environment.mem(buffer,
                    BufferProperties.MEM_READ_ONLY,
                    BufferProperties.MEM_COPY_HOST_PTR,
                    BufferProperties.MEM_HOST_WRITE_ONLY);

            createInputs(buffer.asFloatBuffer(), size - 1, -1);
            CLBuffer b = environment.mem(buffer,
                    BufferProperties.MEM_READ_ONLY,
                    BufferProperties.MEM_COPY_HOST_PTR,
                    BufferProperties.MEM_HOST_WRITE_ONLY);

            CLBuffer res = environment.mem(size * Float.BYTES,
                    BufferProperties.MEM_WRITE_ONLY, BufferProperties.MEM_HOST_READ_ONLY);

            kernel.setArg(0, a);
            kernel.setArg(1, b);
            kernel.setArg(2, res);
            kernel.setArg(3, size);

            environment.run(kernel, new long[] { size });

            environment.read(res, buffer);

            for (int i = 0; i < size; i++) {
                assertEquals(size - 1, buffer.getFloat(), 0);
            }
        }
    }

    private void createInputs(FloatBuffer target, int start, int step) {
        target.position(0);
        int size = target.remaining();
        int v = start;
        for (int i = 0; i < size; i++) {
            target.put(v);
            v += step;
        }
        target.flip();
    }

}
