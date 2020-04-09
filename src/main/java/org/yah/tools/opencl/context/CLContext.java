package org.yah.tools.opencl.context;

import static org.lwjgl.opencl.CL10.clCreateContext;
import static org.lwjgl.opencl.CL10.clReleaseContext;

import java.nio.ByteBuffer;
import java.util.EnumMap;

import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryUtil;
import org.yah.tools.opencl.CLException;
import org.yah.tools.opencl.CLObject;
import org.yah.tools.opencl.platform.CLDevice;
import org.yah.tools.opencl.platform.CLPlaform;
import org.yah.tools.opencl.platform.DeviceInfo;

public class CLContext implements CLObject {

    public static class ContextPropertiesMap extends EnumMap<ContextProperties, Long> {
        private static final long serialVersionUID = 1L;

        public ContextPropertiesMap() {
            super(ContextProperties.class);
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
    }

    private long id;

    private final long platform;

    private final ErrorHandler errorHandler;

    private final PointerBuffer devices;

    public CLContext(long platform,
            ContextPropertiesMap properties,
            ErrorHandler errorHandler,
            long... deviceIds) {
        this.platform = platform;
        devices = BufferUtils.createPointerBuffer(deviceIds.length);
        for (long deviceId : deviceIds) {
            devices.put(deviceId);
        }
        devices.flip();
        id = CLException.apply(eb -> clCreateContext(properties.toPointerBuffer(), devices,
                this::onError, 0,
                eb));
        this.errorHandler = errorHandler;
    }

    @Override
    public long getId() { return id; }

    public long getPlatform() { return platform; }

    public PointerBuffer getDevices() { return devices; }

    public long getDevice() { return devices.get(0); }

    @Override
    public void close() {
        if (id != 0) {
            clReleaseContext(id);
            id = 0;
        }
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

    private static CLContext create(long platformId, ErrorHandler errorHandler,
            long... deviceIds) {
        ContextPropertiesMap props = new ContextPropertiesMap();
        props.put(ContextProperties.CONTEXT_PLATFORM, platformId);
        return new CLContext(platformId, props, errorHandler, deviceIds);
    }

    public static CLContext fromDevice(long device, ErrorHandler errorHandler) {
        long platform = CLDevice.readDeviceInfo(device, DeviceInfo.DEVICE_PLATFORM,
                ByteBuffer::getLong);
        return create(platform, errorHandler, device);
    }

    public static CLContext fromPlatform(long platformId, ErrorHandler errorHandler) {
        CLDevice device = CLDevice.defaultDevice(platformId);
        if (device == null)
            throw new IllegalStateException("No default device found for platform");
        return create(platformId, errorHandler, device.getId());
    }

    public static CLContext createDefault(ErrorHandler errorHandler) {
        CLPlaform defaultPlatform = CLPlaform.defaultPlatform();
        if (defaultPlatform == null)
            throw new IllegalStateException("No default platform found");
        CLDevice defaultDevice = defaultPlatform.getDefaultDevice();
        if (defaultDevice == null)
            throw new IllegalStateException(
                    "No default device found for platform " + defaultPlatform.getName());
        return create(defaultPlatform.getId(), errorHandler, defaultDevice.getId());
    }

}
