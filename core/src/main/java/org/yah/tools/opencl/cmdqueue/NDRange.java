package org.yah.tools.opencl.cmdqueue;

import org.lwjgl.PointerBuffer;
import org.yah.tools.opencl.enums.deviceinfo.DeviceInfo;
import org.yah.tools.opencl.platform.CLDevice;

import javax.annotation.Nullable;
import java.nio.LongBuffer;
import java.util.Collection;

public final class NDRange extends CLEventsBuffer {

    private final int dimensions;
    private final PointerBuffer globalWorkSizesBuffer;
    @Nullable
    private PointerBuffer localWorkSizesBuffer;
    @Nullable
    private PointerBuffer globalWorkOffsetsBuffer;

    public NDRange(int dimensions) {
        this.dimensions = dimensions;
        this.globalWorkSizesBuffer = PointerBuffer.allocateDirect(dimensions);
    }

    public int getDimensions() {
        return dimensions;
    }

    public PointerBuffer getGlobalWorkSizesBuffer() {
        return globalWorkSizesBuffer;
    }

    @Nullable
    public PointerBuffer getLocalWorkSizesBuffer() {
        return localWorkSizesBuffer;
    }

    @Nullable
    public PointerBuffer getGlobalWorkOffsetsBuffer() {
        return globalWorkOffsetsBuffer;
    }

    public NDRange globalWorkSize(int x) {
        globalWorkSizesBuffer.put(0, x);
        return this;
    }

    public NDRange globalWorkSize(int x, int y) {
        globalWorkSizesBuffer.put(0, x);
        globalWorkSizesBuffer.put(1, y);
        return this;
    }

    public NDRange globalWorkSize(int x, int y, int z) {
        globalWorkSizesBuffer.put(0, x);
        globalWorkSizesBuffer.put(1, y);
        globalWorkSizesBuffer.put(2, z);
        return this;
    }

    public NDRange noLocalWorkSize() {
        localWorkSizesBuffer = null;
        return this;
    }

    public NDRange localWorkSize(int x) {
        localWorkSizesBuffer().put(0, x);
        return this;
    }

    public NDRange localWorkSizesBuffer(int x, int y) {
        localWorkSizesBuffer().put(0, x);
        localWorkSizesBuffer().put(1, y);
        return this;
    }

    public NDRange localWorkSizesBuffer(int x, int y, int z) {
        localWorkSizesBuffer().put(0, x);
        localWorkSizesBuffer().put(1, y);
        localWorkSizesBuffer().put(2, z);
        return this;
    }

    public NDRange noGlobalWorkOffsetsBuffer() {
        globalWorkOffsetsBuffer = null;
        return this;
    }

    public NDRange globalWorkOffsetsBuffer(int x) {
        globalWorkOffsetsBuffer().put(0, x);
        return this;
    }

    public NDRange globalWorkOffsetsBuffer(int x, int y) {
        globalWorkOffsetsBuffer().put(0, x);
        globalWorkOffsetsBuffer().put(1, y);
        return this;
    }

    public NDRange globalWorkOffsetsBuffer(int x, int y, int z) {
        globalWorkOffsetsBuffer().put(0, x);
        globalWorkOffsetsBuffer().put(1, y);
        globalWorkOffsetsBuffer().put(2, z);
        return this;
    }

    private PointerBuffer localWorkSizesBuffer() {
        if (localWorkSizesBuffer == null) localWorkSizesBuffer = PointerBuffer.allocateDirect(dimensions);
        return localWorkSizesBuffer;
    }

    private PointerBuffer globalWorkOffsetsBuffer() {
        if (globalWorkOffsetsBuffer == null) globalWorkOffsetsBuffer = PointerBuffer.allocateDirect(dimensions);
        return globalWorkOffsetsBuffer;
    }

    public void validate(Collection<CLDevice> devices) {
        devices.forEach(this::validate);
    }

    public void validate(CLDevice device) {
        int max = device.getDeviceInfo(DeviceInfo.DEVICE_MAX_WORK_ITEM_DIMENSIONS);
        if (dimensions > max)
            throw new IllegalStateException(String.format("Invalid dimensions : %d > %d", dimensions, max));

        if (localWorkSizesBuffer != null) {
            LongBuffer maxSizes = device.getDeviceInfo(DeviceInfo.DEVICE_MAX_WORK_ITEM_SIZES);
            long workGroupSize = 1;
            for (int i = 0; i < dimensions; i++) {
                long size = localWorkSizesBuffer.get(i);
                long maxSize = maxSizes.get(i);
                if (size > maxSize)
                    throw new IllegalStateException(String.format("Invalid local work size : %d > %d", size, maxSize));
                workGroupSize *= size;
            }

            long maxl = device.getDeviceInfo(DeviceInfo.DEVICE_MAX_WORK_GROUP_SIZE);
            if (workGroupSize > maxl)
                throw new IllegalStateException(String.format("Invalid work group size : %d > %d", workGroupSize, maxl));
        }
    }


}
