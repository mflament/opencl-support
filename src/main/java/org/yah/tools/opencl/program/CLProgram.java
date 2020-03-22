package org.yah.tools.opencl.program;

import static org.lwjgl.opencl.CL10.CL_PROGRAM_BUILD_LOG;
import static org.lwjgl.opencl.CL10.CL_SUCCESS;
import static org.lwjgl.opencl.CL10.clBuildProgram;
import static org.lwjgl.opencl.CL10.clCreateProgramWithSource;
import static org.lwjgl.opencl.CL10.clGetProgramBuildInfo;
import static org.lwjgl.opencl.CL10.clReleaseProgram;
import static org.yah.tools.opencl.CLException.apply;
import static org.yah.tools.opencl.CLException.messageFor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.yah.tools.opencl.CLObject;
import org.yah.tools.opencl.context.CLContext;

public class CLProgram implements CLObject {

    private long id;

    public CLProgram(CLContext context, String source, String options) {
        id = apply(eb -> clCreateProgramWithSource(context.getId(), source, eb));
        int buildret = clBuildProgram(id, context.getDevices(), trimToEmpty(options), null, 0);
        if (buildret != CL_SUCCESS) {
            try (MemoryStack stack = MemoryStack.stackPush()) {
                PointerBuffer sizeBuffer = stack.mallocPointer(1);
                String log = null;
                int ret = clGetProgramBuildInfo(id, context.getDevice(), CL_PROGRAM_BUILD_LOG, (ByteBuffer) null,
                        sizeBuffer);
                if (ret == CL_SUCCESS) {
                    long logSize = sizeBuffer.get(0);
                    if (logSize < Integer.MAX_VALUE) {
                        ByteBuffer logBuffer = stack.malloc((int) logSize);
                        ret = clGetProgramBuildInfo(id, context.getDevice(), CL_PROGRAM_BUILD_LOG, logBuffer, null);
                        if (ret == CL_SUCCESS)
                            log = MemoryUtil.memUTF8Safe(logBuffer);
                    } else
                        log = logSize + " bytes of build logs, really ?";
                }
                if (log == null)
                    log = "Error " + messageFor(ret) + " getting program build log";
                throw new CLBuildException(buildret, source, log);
            }
        }
    }

    private static String trimToEmpty(String s) {
        if (s == null)
            return "";
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

    public static CLProgram fromResource(CLContext context, String options,
            String resourcePath)
            throws IOException {
        ClassLoader cl = CLProgram.class.getClassLoader();
        try (InputStream stream = cl.getResourceAsStream(resourcePath)) {
            if (stream == null)
                throw new FileNotFoundException(resourcePath + " not found");
            return fromStream(context, options, stream);
        }
    }

    public static CLProgram fromStream(CLContext context, String options, InputStream is)
            throws IOException {
        StringBuilder sb = new StringBuilder();
        byte[] buffer = new byte[1024];
        int read;
        while ((read = is.read(buffer)) != -1) {
            sb.append(new String(buffer, 0, read, StandardCharsets.UTF_8));
        }
        return new CLProgram(context, sb.toString(), options);
    }

}
