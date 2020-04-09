package org.yah.tools.opencl.platform;

import static org.lwjgl.opencl.CL10.CL_DEVICE_TYPE_ALL;
import static org.lwjgl.opencl.CL10.CL_DEVICE_TYPE_DEFAULT;
import static org.lwjgl.opencl.CL10.clGetDeviceIDs;
import static org.lwjgl.opencl.CL10.clGetDeviceInfo;
import static org.yah.tools.opencl.CLException.check;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opencl.CL10;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.yah.tools.opencl.CLInfoReader;

public class CLDevice {

    private final long id;

    private final long platformId;

    private final String name;

    private CLDevice(long id, long platformId, String name) {
        this.id = id;
        this.platformId = platformId;
        this.name = name;
    }

    public long getId() { return id; }

    public String getName() { return name; }

    public long getPlatformId() { return platformId; }

    public <T> T getDeviceInfo(DeviceInfo info, CLInfoReader<T> reader) {
        return readDeviceInfo(id, info, reader);
    }

    @Override
    public String toString() {
        return name;
    }

    public static CLDevice defaultDevice(long platformId) {
        IntBuffer deviceCount = BufferUtils.createIntBuffer(1);
        PointerBuffer deviceId = BufferUtils.createPointerBuffer(1);
        check(clGetDeviceIDs(platformId, CL_DEVICE_TYPE_DEFAULT, deviceId, deviceCount));
        return deviceCount.get(0) > 0 ? createDevice(platformId, deviceId.get(0)) : null;
    }

    public static List<CLDevice> platformDevices(long platformId, DeviceType deviceType) {
        int[] numDevices = new int[1];
        int error = clGetDeviceIDs(platformId, deviceType.id(), null, numDevices);
        if (error == CL10.CL_DEVICE_NOT_FOUND)
            return Collections.emptyList();

        PointerBuffer deviceIds = BufferUtils.createPointerBuffer(numDevices[0]);
        check(clGetDeviceIDs(platformId, CL_DEVICE_TYPE_ALL, deviceIds, numDevices));
        List<CLDevice> devices = new ArrayList<>(numDevices[0]);
        for (int i = 0; i < numDevices[0]; i++) {
            devices.add(createDevice(platformId, deviceIds.get(i)));
        }
        return devices;
    }

    public static CLDevice createDevice(long deviceId) {
        long platformId = readDeviceInfo(deviceId, DeviceInfo.DEVICE_PLATFORM, ByteBuffer::getLong);
        String name = readDeviceInfo(deviceId, DeviceInfo.DEVICE_NAME,
                b -> MemoryUtil.memUTF8(MemoryUtil.memAddress(b)));
        return new CLDevice(deviceId, platformId, name);
    }

    private static CLDevice createDevice(long platformId, long deviceId) {
        String name = readDeviceInfo(deviceId, DeviceInfo.DEVICE_NAME,
                b -> MemoryUtil.memUTF8(MemoryUtil.memAddress(b)));
        return new CLDevice(deviceId, platformId, name);
    }

    public static <T> T readDeviceInfo(long deviceId, DeviceInfo info, CLInfoReader<T> reader) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            PointerBuffer sizeBuffer = stack.mallocPointer(1);
            check(clGetDeviceInfo(deviceId, info.id(), (ByteBuffer) null, sizeBuffer));
            ByteBuffer valueBuffer = stack.malloc((int) sizeBuffer.get(0));
            check(clGetDeviceInfo(deviceId, info.id(), valueBuffer, sizeBuffer));
            return reader.apply(valueBuffer);
        } catch (RuntimeException e) {
            throw new RuntimeException("Error reading device " + deviceId + " info " + info, e);
        }
    }

    public static BigInteger getMaxWorkSize(long device) {
        int addressBits = CLDevice.readDeviceInfo(device,
                DeviceInfo.DEVICE_ADDRESS_BITS,
                ByteBuffer::getInt);
        return BigInteger.valueOf(2).pow(addressBits);
    }

    // TOSEE: possible overflow everywhere (size_t > long on 64bits, uint > int ...)
    
    public static int getMaxDimensions(long device) {
        return CLDevice.readDeviceInfo(device, DeviceInfo.DEVICE_MAX_WORK_ITEM_DIMENSIONS,
                ByteBuffer::getInt);
    }

    public static long getMaxWorkGroupSize(long device) {
        return CLDevice.readDeviceInfo(device, DeviceInfo.DEVICE_MAX_WORK_GROUP_SIZE, PointerBuffer::get);
    }

    public static int getMaxComputeUnits(long device) {
        return CLDevice.readDeviceInfo(device, DeviceInfo.DEVICE_MAX_COMPUTE_UNITS,
                ByteBuffer::getInt);
    }

    public static long[] getMaxWorkItemSizes(long device) {
        int maxDimensions = getMaxDimensions(device);
        long[] sizes = new long[maxDimensions];
        CLDevice.readDeviceInfo(device, DeviceInfo.DEVICE_MAX_WORK_ITEM_SIZES, b -> {
            for (int i = 0; i < maxDimensions; i++) {
                sizes[i] = PointerBuffer.get(b);
            }
            return sizes;
        });
        return sizes;
    }
}