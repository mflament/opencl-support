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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.yah.tools.opencl.CLObject;
import org.yah.tools.opencl.context.CLContext;

public class CLProgram implements CLObject {
    
    private final CLContext context;
    
    private long id;

    private CLProgram(CLContext context, long id) {
        this.context = context;
        this.id = id;
    }

    public void build(String options, long... devices) {
        PointerBuffer device_list = getDevices(devices);
        int buildret = clBuildProgram(id, device_list, trimToEmpty(options), null, 0);
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
                throw new CLBuildException(buildret, log);
            }
        }
    }

    private PointerBuffer getDevices(long[] devices) {
        if (devices.length == 0)
            return context.getDevices();
        else {
            PointerBuffer device_list = BufferUtils.createPointerBuffer(devices.length);
            for (long device : devices) {
                device_list.put(device);
            }
            return device_list.flip();
        }
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

    public static Builder builder() {
        return new Builder();
    }

    private static String trimToEmpty(String s) {
        if (s == null)
            return "";
        return s.trim();
    }

    /** @noinspection UnusedReturnValue*/
    public static final class Builder {
        private final byte[] buffer = new byte[5 * 1024];
        private final ClassLoader classLoader = Builder.class.getClassLoader();
        private final List<String> sources = new ArrayList<>();
        private CLContext context;
        private String options = "";

        public Builder withContext(CLContext context) {
            this.context = context;
            return this;
        }

        public Builder withOptions(String options) {
            this.options = options;
            return this;
        }

        public Builder withSourceFiles(String... files) throws IOException {
            return withSourcePaths(Arrays.stream(files).map(Paths::get).toArray(Path[]::new));
        }

        public Builder withSourcePaths(Path... files) throws IOException {
            sources.clear();
            return addSourceFiles(files);
        }

        public Builder withSourceResource(String... resources) throws IOException {
            sources.clear();
            return addSourceResource(resources);
        }

        public Builder addSourceFiles(Path... files) throws IOException {
            for (Path file : files) {
                sources.add(loadSource(file));
            }
            return this;
        }

        public Builder addSourceResource(String... resources) throws IOException {
            for (String resource : resources) {
                sources.add(loadSource(resource));
            }
            return this;
        }

        public CLProgram build() {
            if (context == null)
                throw new IllegalStateException("Context not set");
            long id = apply(
                    eb -> clCreateProgramWithSource(context.getId(), sources.toArray(new String[0]), eb));
            CLProgram program = new CLProgram(context, id);
            program.build(options);
            return program;
        }

        private String loadSource(Path file) throws IOException {
            try (InputStream is = Files.newInputStream(file)) {
                return readFully(is);
            }
        }

        private String loadSource(String resource) throws IOException {
            try (InputStream is = classLoader.getResourceAsStream(resource)) {
                if (is == null)
                    throw new FileNotFoundException("classpath:" + resource);
                return readFully(is);
            }
        }

        private String readFully(InputStream is) throws IOException {
            int read;
            StringBuilder sb = new StringBuilder(5 * 1024);
            while ((read = is.read(buffer)) != -1) {
                sb.append(new String(buffer, 0, read, StandardCharsets.UTF_8));
            }
            return sb.toString();
        }

    }
}
