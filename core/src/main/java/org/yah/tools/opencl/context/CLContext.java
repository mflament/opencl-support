package org.yah.tools.opencl.context;

import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opencl.CLContextCallbackI;
import org.lwjgl.system.MemoryUtil;
import org.yah.tools.opencl.CLException;
import org.yah.tools.opencl.CLObject;
import org.yah.tools.opencl.CLUtils;
import org.yah.tools.opencl.cmdqueue.CLCommandQueue;
import org.yah.tools.opencl.enums.ContextProperty;
import org.yah.tools.opencl.enums.DeviceType;
import org.yah.tools.opencl.mem.CLBuffer;
import org.yah.tools.opencl.platform.CLDevice;
import org.yah.tools.opencl.platform.CLPlatform;
import org.yah.tools.opencl.program.CLProgram;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;
import java.util.*;

import static org.lwjgl.opencl.CL10.clCreateContext;
import static org.lwjgl.opencl.CL10.clReleaseContext;

public class CLContext implements CLObject {

    @FunctionalInterface
    public interface ErrorHandler {
        void onError(String message, ByteBuffer privateInfo);
    }

    private final long id;
    private final CLPlatform platform;
    private final List<CLDevice> devices;

    protected CLContext(CLContext from) {
        id = from.id;
        platform = from.platform;
        devices = from.devices;
    }

    private CLContext(long id, CLPlatform platform, List<CLDevice> devices) {
        this.id = id;
        this.platform = Objects.requireNonNull(platform, "platform is null");
        Objects.requireNonNull(devices, "devices is null");
        if (devices.isEmpty()) throw new IllegalStateException("no devices");
        this.devices = CLUtils.copyOf(devices);
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

    public CLDevice getFirstDevice() {
        return devices.get(0);
    }

    public Optional<CLDevice> findDevice(long deviceId) {
        return devices.stream().filter(d -> d.getId() == deviceId).findFirst();
    }

    public CLProgram.Builder programBuilder() {
        return CLProgram.builder(this);
    }

    public CLCommandQueue.Builder buildCommandQueue() {
        return CLCommandQueue.builder(this);
    }

    public CLBuffer.Builder buildBuffer() {
        return new CLBuffer.Builder(this);
    }

    @Override
    public void close() {
        clReleaseContext(id);
    }

    public void checkDevices(List<CLDevice> devices) {
        devices.forEach(this::checkDevice);
    }

    public void checkDevice(CLDevice device) {
        if (!this.devices.contains(device))
            throw new IllegalArgumentException("device " + device + " is not in context " + id);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private CLPlatform platform;
        private ErrorHandler errorHandler;
        private final List<CLDevice> devices = new ArrayList<>();

        private final ContextPropertiesMap contextProperties = new ContextPropertiesMap();

        private Builder() {
        }

        public Builder withPlatform(CLPlatform platform) {
            this.platform = platform;
            return this;
        }

        public Builder withDevice(CLDevice device) {
            this.devices.add(device);
            return this;
        }

        public Builder withErrorHandler(ErrorHandler errorHandler) {
            this.errorHandler = errorHandler;
            return this;
        }

        public CLContext build() {
            if (devices.isEmpty()) {
                if (platform == null) platform = CLPlatform.getDefaultPlatform();
                devices.addAll(platform.getDevices(DeviceType.DEVICE_TYPE_DEFAULT));
            }
            if (platform == null)
                platform = devices.get(0).getPlatform();

            contextProperties.put(ContextProperty.CONTEXT_PLATFORM, platform.getId());
            PointerBuffer propertiesBuffer = contextProperties.toPointerBuffer();
            PointerBuffer deviceBuffer = PointerBuffer.allocateDirect(devices.size());
            devices.forEach(device -> deviceBuffer.put(device.getId()));
            deviceBuffer.flip();

            if (errorHandler == null) {
                errorHandler = (message, privateInfo) -> {
                    throw new RuntimeException(message);
                };
            }
            long id = CLException.apply(eb -> clCreateContext(propertiesBuffer, deviceBuffer,
                    new ErrorHandlerCallback(errorHandler), 0, eb));
            return new CLContext(id, platform, devices);
        }

    }

    private static final class ErrorHandlerCallback implements CLContextCallbackI {
        private final ErrorHandler errorHandler;

        public ErrorHandlerCallback(ErrorHandler errorHandler) {
            this.errorHandler = errorHandler;
        }

        @Override
        public void invoke(long errinfo, long private_info, long cb, long user_data) {
            String message = MemoryUtil.memUTF8(errinfo);
            ByteBuffer infoBuffer = null;
            if (private_info != 0) {
                infoBuffer = MemoryUtil.memByteBuffer(private_info, (int) cb);
            }
            errorHandler.onError(message, infoBuffer);
        }
    }

    private static class ContextPropertiesMap extends EnumMap<ContextProperty, Long> {
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

}
