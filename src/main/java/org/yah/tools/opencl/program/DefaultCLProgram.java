package org.yah.tools.opencl.program;

import static org.lwjgl.opencl.CL10.clBuildProgram;
import static org.lwjgl.opencl.CL10.clCreateProgramWithSource;
import static org.lwjgl.opencl.CL10.clReleaseProgram;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.yah.tools.opencl.CLException;
import org.yah.tools.opencl.context.CLContext;

public class DefaultCLProgram implements CLProgram {

    private long id;

    public DefaultCLProgram(CLContext context, String source, String options) {
        CLException.run(eb -> {
            id = clCreateProgramWithSource(context.getId(), source, eb);
            CLException.check(clBuildProgram(id, context.getDevices(), trimToEmpty(options), null, 0));
        });
    }
    
    private static String trimToEmpty(String s) {
        if (s == null) return "";
        return s.trim();
    }

    @Override
    public long getId() { return id; }

    @Override
    public void close() {
        if (id != 0) {
            clReleaseProgram(id);
            id = 0;
        }
    }

    public static DefaultCLProgram fromResource(CLContext context, String options, String resourcePath)
            throws IOException {
        ClassLoader cl = DefaultCLProgram.class.getClassLoader();
        try (InputStream stream = cl.getResourceAsStream(resourcePath)) {
            return fromStream(context, options, stream);
        }
    }

    public static DefaultCLProgram fromStream(CLContext context, String options, InputStream is)
            throws IOException {
        StringBuilder sb = new StringBuilder();
        byte[] buffer = new byte[1024];
        int read;
        while ((read = is.read(buffer)) != -1) {
            sb.append(new String(buffer, 0, read, StandardCharsets.UTF_8));
        }
        return new DefaultCLProgram(context, sb.toString(), options);
    }

}
