package org.yah.tools.opencl.ndrange;

import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.yah.tools.opencl.enums.deviceinfo.DeviceInfo;
import org.yah.tools.opencl.platform.CLDevice;

import javax.annotation.Nullable;
import java.nio.LongBuffer;

public abstract class AbstractNDRange<THIS extends AbstractNDRange<THIS>> implements NDRange {

    private final int dimensions;
    protected final PointerBuffer globalWorkSizesBuffer;
    protected final PointerBuffer localWorkSizesBuffer;
    protected final PointerBuffer globalWorkOffsetsBuffer;

    protected AbstractNDRange(int dimensions) {
        this.dimensions = dimensions;
        globalWorkSizesBuffer = BufferUtils.createPointerBuffer(dimensions);

        localWorkSizesBuffer = BufferUtils.createPointerBuffer(dimensions);
        localWorkSizesBuffer.limit(0);

        globalWorkOffsetsBuffer = BufferUtils.createPointerBuffer(dimensions);
        globalWorkOffsetsBuffer.limit(0);
    }

    @Override
    public int getDimensions() {
        return dimensions;
    }

    @Override
    public PointerBuffer getGlobalWorkSizes() {
        return globalWorkSizesBuffer;
    }

    @Override
    @Nullable
    public PointerBuffer getLocalWorkSizes() {
        if (localWorkSizesBuffer.hasRemaining())
            return localWorkSizesBuffer;
        return null;
    }

    @Override
    @Nullable
    public PointerBuffer getGlobalWorkOffsets() {
        if (globalWorkOffsetsBuffer.hasRemaining())
            return globalWorkOffsetsBuffer;
        return null;
    }

    public THIS noLocalWorkSizes() {
        localWorkSizesBuffer.limit(0);
        return self();
    }


    public THIS noGlobalWorkOffsets() {
        globalWorkOffsetsBuffer.limit(0);
        return self();
    }

    @Override
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


    protected abstract THIS self();


}
