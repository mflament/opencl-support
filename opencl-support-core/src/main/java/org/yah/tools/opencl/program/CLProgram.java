package org.yah.tools.opencl.program;

import org.apache.commons.io.IOUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.yah.tools.opencl.CLException;
import org.yah.tools.opencl.CLObject;
import org.yah.tools.opencl.CLUtils;
import org.yah.tools.opencl.context.CLContext;
import org.yah.tools.opencl.enums.CLEnum;
import org.yah.tools.opencl.enums.ProgramBinaryType;
import org.yah.tools.opencl.enums.deviceinfo.DeviceInfo;
import org.yah.tools.opencl.kernel.CLKernel;
import org.yah.tools.opencl.platform.CLDevice;

import javax.annotation.Nullable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.lwjgl.opencl.CL12.*;
import static org.yah.tools.opencl.CLException.apply;
import static org.yah.tools.opencl.CLException.check;

public class CLProgram implements CLObject {

    public static final String CLASSPATH_PREFIX = "classpath:";

    public static String addResourcePathPrefix(String file) {
        if (!file.startsWith(CLASSPATH_PREFIX))
            return CLASSPATH_PREFIX + file;
        return file;
    }

    @Nullable
    public static String getResourcePath(String file) {
        if (file.startsWith(CLASSPATH_PREFIX))
            return file.substring(CLASSPATH_PREFIX.length());
        return null;
    }

    public static String getProgramPath(String file) {
        if (file.startsWith(CLASSPATH_PREFIX))
            file = file.substring(CLASSPATH_PREFIX.length());
        return CLUtils.toStandardPath(file);
    }


    private final long id;
    private final CLContext context;
    private final CLCompilerOptions compilerOptions;
    private final List<CLDevice> devices;
    private final int maxDimensions;

    protected CLProgram(CLProgram from) {
        this.id = from.id;
        this.context = from.context;
        this.devices = from.devices;
        this.compilerOptions = new CLCompilerOptions(from.compilerOptions);
        this.maxDimensions = from.maxDimensions;
    }

    private CLProgram(long id, CLContext context, CLCompilerOptions compilerOptions) {
        this.id = id;
        this.context = context;
        this.devices = getProgramDevices(id, context);
        this.compilerOptions = new CLCompilerOptions(compilerOptions);
        maxDimensions = devices.stream()
                .mapToInt(device -> device.getDeviceInfo(DeviceInfo.DEVICE_MAX_WORK_ITEM_DIMENSIONS))
                .min()
                .orElse(0);
    }

    public CLCompilerOptions getCompilerOptions() {
        return compilerOptions;
    }

    public CLContext getContext() {
        return context;
    }

    public List<CLDevice> getDevices() {
        return devices;
    }

    public int getMaxDimensions() {
        return maxDimensions;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void close() {
        clReleaseProgram(id);
    }

    public ProgramBinaryType getBinaryType(long device) {
        int[] type = new int[1];
        check(clGetProgramBuildInfo(id, device, CL_PROGRAM_BINARY_TYPE, type, null));
        return CLEnum.get(type[0], ProgramBinaryType.values());
    }

    public String getSource() {
        return CLUtils.readSizedString((sb, bb) -> clGetProgramInfo(id, CL_PROGRAM_SOURCE, bb, sb));
    }

    public Map<CLDevice, ByteBuffer> getBinaries() {
        PointerBuffer sizesBuffer = BufferUtils.createPointerBuffer(devices.size());
        check(clGetProgramInfo(id, CL_PROGRAM_BINARY_SIZES, sizesBuffer, null));

        PointerBuffer bufferPointers = BufferUtils.createPointerBuffer(devices.size());
        List<ByteBuffer> deviceBinaries = new ArrayList<>(devices.size());
        IntStream.range(0, devices.size())
                .mapToLong(sizesBuffer::get)
                .mapToObj(size -> BufferUtils.createByteBuffer((int) size))
                .forEach(buffer -> {
                    deviceBinaries.add(buffer);
                    bufferPointers.put(MemoryUtil.memAddress(buffer));
                });
        bufferPointers.flip();
        clGetProgramInfo(id, CL_PROGRAM_BINARIES, bufferPointers, null);

        Map<CLDevice, ByteBuffer> binaries = new LinkedHashMap<>(devices.size());
        for (int i = 0; i < devices.size(); i++) {
            binaries.put(devices.get(i), deviceBinaries.get(i));
        }
        return binaries;
    }

    public List<String> getKernelNames() {
        if (getNumKernels() > 0) {
            String names = CLUtils.readSizedString((sb, bb) -> clGetProgramInfo(id, CL_PROGRAM_KERNEL_NAMES, bb, sb));
            return Arrays.asList(names.split(";"));
        }
        return Collections.emptyList();
    }

    public int getNumKernels() {
        PointerBuffer pb = BufferUtils.createPointerBuffer(1);
        clGetProgramInfo(id, CL_PROGRAM_NUM_KERNELS, pb, null);
        return (int) pb.get(0);
    }

    public List<CLKernel> newKernels() {
        int[] numKernels = new int[1];
        check(clCreateKernelsInProgram(id, null, numKernels));
        PointerBuffer kernelsBuffer = BufferUtils.createPointerBuffer(numKernels[0]);
        check(clCreateKernelsInProgram(id, kernelsBuffer, (int[]) null));
        return IntStream.range(0, numKernels[0])
                .mapToLong(kernelsBuffer::get)
                .mapToObj(kernelId -> new CLKernel(this, kernelId))
                .collect(Collectors.toList());
    }

    public CLKernel newKernel(String name) {
        return new CLKernel(this, name);
    }

    public static Builder builder(CLContext context) {
        return new Builder(context);
    }

    private static List<CLDevice> getProgramDevices(long id, CLContext context) {
        int[] numDevices = new int[1];
        check(clGetProgramInfo(id, CL_PROGRAM_NUM_DEVICES, numDevices, null));
        PointerBuffer deviceIds = PointerBuffer.allocateDirect(numDevices[0]);
        check(clGetProgramInfo(id, CL_PROGRAM_DEVICES, deviceIds, null));
        return IntStream.range(0, numDevices[0])
                .mapToLong(deviceIds::get)
                .mapToObj(deviceId -> context.findDevice(deviceId).orElseThrow(IllegalStateException::new))
                .collect(Collectors.toList());
    }

    /**
     * @noinspection UnusedReturnValue
     */
    public static final class Builder {

        private final CLContext context;
        private final List<CLDevice> devices = new ArrayList<>();
        private final List<String> sources = new ArrayList<>();
        private CLCompilerOptions compilerOptions;

        public Builder(CLContext context) {
            this.context = Objects.requireNonNull(context, "context is null");
        }

        public Builder withCompilerOptions(String options) {
            this.compilerOptions = CLCompilerOptions.parse(options);
            return this;
        }

        public Builder withCompilerOptions(@Nullable CLCompilerOptions options) {
            this.compilerOptions = options;
            return this;
        }

        public Builder withSource(String source) {
            sources.add(source);
            return this;
        }

        public Builder addDevice(CLDevice device) {
            devices.add(device);
            return this;
        }

        public Builder withDevice(CLDevice device) {
            devices.clear();
            return addDevice(device);
        }

        public Builder withDevices(Collection<CLDevice> devices) {
            this.devices.clear();
            this.devices.addAll(devices);
            return this;
        }

        public Builder withFile(String file) {
            sources.add(loadSource(file));
            return this;
        }

        public Builder withResource(String resource) {
            withFile(addResourcePathPrefix(resource));
            return this;
        }

        public CLProgram build() {
            if (sources.isEmpty())
                throw new IllegalStateException("No sources");

            long id = apply(eb -> clCreateProgramWithSource(context.getId(), sources.toArray(new String[0]), eb));
            List<CLDevice> programDevices = devices.isEmpty() ? context.getDevices() : devices;
            PointerBuffer device_list = PointerBuffer.allocateDirect(programDevices.size());
            for (CLDevice device : programDevices)
                device_list.put(device.getId());
            device_list.flip();

            if (compilerOptions == null)
                compilerOptions = new CLCompilerOptions();

            int buildret = clBuildProgram(id, device_list, compilerOptions.toString(), null, 0);
            if (buildret != CL_SUCCESS)
                throw new CLBuildException(buildret, createLog(id, programDevices));
            return new CLProgram(id, context, compilerOptions);
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
                        sb.append(CLUtils.readCLString(logBuffer));
                    }
                }
            }
            return sb.toString();
        }

    }

    public static String loadSource(String path) {
        try (InputStream is = openStream(path)) {
            return IOUtils.toString(is, StandardCharsets.UTF_8.displayName());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static InputStream openStream(String path) throws IOException {
        String resourcePath = getResourcePath(path);
        if (resourcePath != null) {
            ClassLoader classLoader = CLProgram.class.getClassLoader();
            URL url = classLoader.getResource(resourcePath);
            if (url == null)
                throw new FileNotFoundException("Classpath resource " + resourcePath + " was not found");
            return url.openStream();
        } else {
            return Files.newInputStream(Paths.get(path));
        }
    }
}
