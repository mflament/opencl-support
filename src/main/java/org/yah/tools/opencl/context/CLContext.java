package org.yah.tools.opencl.context;

import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryUtil;
import org.yah.tools.opencl.CLException;
import org.yah.tools.opencl.CLObject;
import org.yah.tools.opencl.cmdqueue.CLCommandQueue;
import org.yah.tools.opencl.enums.BufferProperties;
import org.yah.tools.opencl.enums.ContextProperty;
import org.yah.tools.opencl.enums.DeviceType;
import org.yah.tools.opencl.mem.CLBuffer;
import org.yah.tools.opencl.platform.CLDevice;
import org.yah.tools.opencl.platform.CLPlatform;
import org.yah.tools.opencl.program.CLProgram;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.EnumMap;
import java.util.List;
import java.util.Objects;

import static org.lwjgl.opencl.CL10.clCreateContext;
import static org.lwjgl.opencl.CL10.clReleaseContext;

public class CLContext implements CLObject {

    public static class ContextPropertiesMap extends EnumMap<ContextProperty, Long> {
        private static final long serialVersionUID = 1L;

        public ContextPropertiesMap() {
            super(ContextProperty.class);
        }

        /**
         * a list of context property names and their corresponding values. Each
         * property name is immediately followed by the corresponding desired value. The
         * list is terminated with 0
         */
        public PointerBuffer toPointerBuffer() {
            PointerBuffer buffer = BufferUtils.createPointerBuffer(size() * 2 + 1);
            forEach((k, v) -> buffer.put(k.id()).put(v));
            buffer.put(0);
            buffer.flip();
            return buffer;
        }
    }

    @FunctionalInterface
    public interface ErrorHandler {
        void onError(String message, ByteBuffer privateInfo);

        ErrorHandler DEFAULT = (message, privateInfo) -> {
            throw new RuntimeException(message);
        };
    }

    private long id;
    private final CLPlatform platform;
    private final List<CLDevice> devices;
    private final ErrorHandler errorHandler;

    public CLContext() {
        this(null, null, null, null);
    }

    public CLContext(@Nullable CLPlatform platform,
                     @Nullable List<CLDevice> devices,
                     @Nullable ErrorHandler errorHandler,
                     @Nullable ContextPropertiesMap properties) {
        if (platform == null)
            platform = CLPlatform.getDefaultPlatform();
        this.platform = Objects.requireNonNull(platform, "platform is null");
        this.errorHandler = Objects.requireNonNullElse(errorHandler, ErrorHandler.DEFAULT);

        if (devices == null || devices.isEmpty()) {
            devices = platform.getDevices(DeviceType.DEVICE_TYPE_DEFAULT);
            if (devices.isEmpty())
                throw new IllegalStateException("no default devices for platform " + platform);
        } else {
            for (CLDevice device : devices) {
                if (device.getPlatform() != platform)
                    throw new IllegalArgumentException("Device " + device + " is not from platform " + platform);
            }
            devices = List.copyOf(devices);
        }
        this.devices = devices;

        ContextPropertiesMap props = new ContextPropertiesMap();
        if (properties != null)
            props.putAll(properties);
        props.put(ContextProperty.CONTEXT_PLATFORM, platform.getId());
        PointerBuffer propertiesBuffer = props.toPointerBuffer();

        PointerBuffer deviceBuffer = PointerBuffer.allocateDirect(devices.size());
        this.devices.forEach(device -> deviceBuffer.put(device.getId()));
        deviceBuffer.flip();

        id = CLException.apply(eb -> clCreateContext(propertiesBuffer, deviceBuffer, this::onError, 0, eb));
    }

    @Override
    public long getId() {
        return id;
    }

    public CLPlatform getPlatform() {
        return platform;
    }

    public List<CLDevice> getDevices() {
        return devices;
    }

    public CLProgram.Builder buildProgram() {
        return buildProgram(devices);
    }

    public CLProgram.Builder buildProgram(List<CLDevice> devices) {
        checkDevices(devices);
        return CLProgram.builder(this, devices);
    }

    public CLCommandQueue.Builder buildCommandQueue() {
        return buildCommandQueue(null);
    }

    public CLCommandQueue.Builder buildCommandQueue(@Nullable CLDevice device) {
        if (device != null)
            checkDevice(device);
        else
            device = devices.get(0);
        return CLCommandQueue.builder(this, device);
    }

    public CLBuffer.Builder buildBuffer() {
        return new CLBuffer.Builder(this);
    }

    @Override
    public void close() {
        if (id != 0) {
            clReleaseContext(id);
            id = 0;
        }
    }

    private void checkDevices(List<CLDevice> devices) {
        devices.forEach(this::checkDevice);
    }

    private void checkDevice(CLDevice device) {
        if (!this.devices.contains(device))
            throw new IllegalArgumentException("device " + device + " is not in context " + id);
    }

    private void onError(long errinfo, long private_info, long cb, long user_data) {
        if (errorHandler != null) {
            String message = MemoryUtil.memUTF8(errinfo);
            ByteBuffer infoBuffer = null;
            if (private_info != 0) {
                infoBuffer = MemoryUtil.memByteBuffer(private_info, (int) cb);
            }
            errorHandler.onError(message, infoBuffer);
        }
    }

}
