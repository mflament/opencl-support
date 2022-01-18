package org.yah.tools.opencl.program;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.yah.tools.opencl.CLException;
import org.yah.tools.opencl.CLObject;
import org.yah.tools.opencl.context.CLContext;
import org.yah.tools.opencl.kernel.CLKernel;
import org.yah.tools.opencl.platform.CLDevice;
import org.yah.tools.opencl.platform.CLPlatform;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.lwjgl.opencl.CL10.*;
import static org.yah.tools.opencl.CLException.apply;

public class CLProgram implements CLObject {
    private final long id;
    private final CLContext context;
    private final List<CLDevice> devices;

    private CLProgram(long id, CLContext context, List<CLDevice> devices) {
        this.id = id;
        this.context = context;
        this.devices = devices;
    }

    public CLContext getContext() {
        return context;
    }

    public List<CLDevice> getDevices() {
        return devices;
    }

    public CLKernel kernel(String name) {
        return new CLKernel(this, name);
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void close() {
        clReleaseProgram(id);
    }

    public static Builder builder(CLContext context, List<CLDevice> devices) {
        return new Builder(context, devices);
    }

    private static String trimToEmpty(String s) {
        if (s == null)
            return "";
        return s.trim();
    }

    /**
     * @noinspection UnusedReturnValue
     */
    public static final class Builder {
        private final CLContext context;
        private final List<CLDevice> devices;

        private final byte[] buffer = new byte[5 * 1024];
        private final ClassLoader classLoader = Builder.class.getClassLoader();
        private final List<String> sources = new ArrayList<>();
        private String options = "";

        public Builder(CLContext context, List<CLDevice> devices) {
            this.context = Objects.requireNonNull(context, "context is null");
            this.devices = List.copyOf(Objects.requireNonNull(devices, "devices is null"));
        }

        public Builder withOptions(String options) {
            this.options = options;
            return this;
        }

        public Builder withSource(String source) {
            sources.add(source);
            return this;
        }

        public Builder withFile(String file) throws IOException {
            sources.add(loadSource(file));
            return this;
        }

        public CLProgram build() {
            if (sources.isEmpty())
                throw new IllegalStateException("No sources");

            long id = apply(eb -> clCreateProgramWithSource(context.getId(), sources.toArray(String[]::new), eb));
            PointerBuffer device_list = PointerBuffer.allocateDirect(devices.size());
            for (CLDevice device : devices)
                device_list.put(device.getId());
            device_list.flip();

            int buildret = clBuildProgram(id, device_list, trimToEmpty(options), null, 0);
            if (buildret != CL_SUCCESS)
                throw new CLBuildException(buildret, createLog(id, devices));

            return new CLProgram(id, context, devices);
        }

        private String createLog(long programId, List<CLDevice> devices) {
            final StringBuilder sb = new StringBuilder();
            try (MemoryStack stack = MemoryStack.stackPush()) {
                int[] buffer = new int[1];
                for (CLDevice device : devices) {
                    long deviceId = device.getId();
                    CLException.check(clGetProgramBuildInfo(programId, deviceId, CL_PROGRAM_BUILD_STATUS, buffer, null));
                    if (buffer[0] == CL_BUILD_ERROR) {
                        PointerBuffer sizeBuffer = stack.mallocPointer(1);
                        CLException.check(clGetProgramBuildInfo(programId, deviceId, CL_PROGRAM_BUILD_LOG, (ByteBuffer) null, sizeBuffer));
                        ByteBuffer logBuffer = stack.malloc((int) sizeBuffer.get(0));
                        CLException.check(clGetProgramBuildInfo(programId, deviceId, CL_PROGRAM_BUILD_LOG, logBuffer, null));
                        sb.append("Device ").append(device).append(" build error :").append(System.lineSeparator());
                        sb.append(MemoryUtil.memUTF8Safe(logBuffer));
                    }
                }
            }
            return sb.toString();
        }

        private String loadSource(String file) throws IOException {
            try (InputStream is = openStream(file)) {
                return new String(is.readAllBytes(), StandardCharsets.UTF_8);
            }
        }

        private static final String CLASSPATH_PREFIX = "classpath:";

        private static InputStream openStream(String file) throws IOException {
            if  (file.startsWith(CLASSPATH_PREFIX)) {
                String resource = file.substring(CLASSPATH_PREFIX.length());
                URL url = CLPlatform.class.getClassLoader().getResource(resource);
                if (url == null)
                    throw new FileNotFoundException("Classpath resource " + resource + " was not found");
                return url.openStream();
            } else {
                return Files.newInputStream(Path.of(file));
            }
        }
    }
}
