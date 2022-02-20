package org.yah.tools.opencl.platform;

import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.yah.tools.opencl.enums.DeviceAddressBits;
import org.yah.tools.opencl.enums.deviceinfo.DeviceInfo;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.lwjgl.opencl.CL10.clGetDeviceInfo;
import static org.yah.tools.opencl.CLException.check;

public class CLDevice {

    public static CLDevice defaultDevice() {
        return CLPlatform.getDefaultPlatform().getDefaultDevice();
    }

    private final long id;
    private final CLPlatform platform;
    private final String name;
    private final DeviceAddressBits addressBits;

    private final ConcurrentMap<DeviceInfo<?>, Object> infosCache = new ConcurrentHashMap<>();

    private final ByteBuffer infoBuffer = BufferUtils.createByteBuffer(8 * 1024);

    public CLDevice(CLPlatform platform, long deviceId) {
        this.id = deviceId;
        this.platform = platform;
        name = getDeviceInfo(DeviceInfo.DEVICE_NAME);
        addressBits = getDeviceInfo(DeviceInfo.DEVICE_ADDRESS_BITS);
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public CLPlatform getPlatform() {
        return platform;
    }

    public DeviceAddressBits getAddressBits() {
        return addressBits;
    }

    @SuppressWarnings("unchecked")
    public <T> T getDeviceInfo(DeviceInfo<T> info) {
        return (T) infosCache.computeIfAbsent(info, this::readDeviceInfo);
    }

    private synchronized <T> T readDeviceInfo(DeviceInfo<T> info) {
        infoBuffer.clear();
        try (MemoryStack stack = MemoryStack.stackPush()) {
            PointerBuffer sizeBuffer = stack.mallocPointer(1);
            check(clGetDeviceInfo(id, info.id(), (ByteBuffer) null, sizeBuffer));
            int size = (int) sizeBuffer.get(0);
            if (size > infoBuffer.remaining())
                throw new BufferOverflowException();
            infoBuffer.limit(size);
            check(clGetDeviceInfo(id, info.id(), infoBuffer, null));
        } catch (RuntimeException e) {
            throw new RuntimeException("Error reading device " + name + " info " + info, e);
        }
        return info.read(this, infoBuffer);
    }

    @Override
    public String toString() {
        return name;
    }

    public String printDeviceInfo() {
        StringBuilder sb = new StringBuilder();
        String lineSeparator = System.lineSeparator();
        sb.append("Platform : ").append(getPlatform().getName()).append(lineSeparator);
        sb.append("Device : ").append(getName()).append(lineSeparator);
        DeviceInfo.DEVICE_INFOS.forEach(info -> {
            Object deviceInfo = getDeviceInfo(info);
            if (deviceInfo instanceof LongBuffer)
                deviceInfo = printBuffer((LongBuffer) deviceInfo);
            else
                deviceInfo = Objects.toString(deviceInfo);
            sb.append("  ").append(info.name()).append(" = ").append(deviceInfo).append(lineSeparator);
        });
        return sb.toString();
    }


    private static String printBuffer(LongBuffer buffer) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < buffer.limit(); i++) {
            if (i > 0) sb.append(" ");
            sb.append(buffer.get(i));
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CLDevice device = (CLDevice) o;
        return id == device.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}