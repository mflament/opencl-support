package org.yah.tools.opencl.cmdqueue;

import org.lwjgl.PointerBuffer;
import org.yah.tools.opencl.cmdqueue.CLCommandQueue;
import org.yah.tools.opencl.enums.deviceinfo.DeviceInfo;
import org.yah.tools.opencl.kernel.CLKernel;
import org.yah.tools.opencl.platform.CLDevice;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class NDRange extends CLCommandQueue.EventsParams {

    private final int maxDimensions;
    private final PointerBuffer globalWorkSizesBuffer;
    private final PointerBuffer localWorkSizesBuffer;
    private final PointerBuffer globalWorkOffsetsBuffer;
    private int dimensions;

    public NDRange(int maxDimensions) {
        this.maxDimensions = maxDimensions;
        this.globalWorkSizesBuffer = PointerBuffer.allocateDirect(maxDimensions);
        this.localWorkSizesBuffer = PointerBuffer.allocateDirect(maxDimensions);
        this.globalWorkOffsetsBuffer = PointerBuffer.allocateDirect(maxDimensions);
    }

    public NDRange(int maxDimensions, long[] globalWorkSize) {
        this(maxDimensions);
        set(globalWorkSize);
    }

    public int getDimensions() {
        return dimensions;
    }

    public PointerBuffer globalWorkSizes() {
        return globalWorkSizesBuffer;
    }

    public PointerBuffer globalWorkOffsets() {
        if (globalWorkOffsetsBuffer != null && globalWorkOffsetsBuffer.hasRemaining())
            return globalWorkOffsetsBuffer;
        return null;
    }

    public PointerBuffer localWorkSizes() {
        if (localWorkSizesBuffer != null && localWorkSizesBuffer.hasRemaining())
            return localWorkSizesBuffer;
        return null;
    }

    public void set(long[] globalWorkSize) {
        set(globalWorkSize, null, null);
    }

    public void set(long[] globalWorkSize, long[] localWorkSize) {
        set(globalWorkSize, localWorkSize, null);
    }

    public void set(long[] globalWorkSize, @Nullable long[] localWorkSize, @Nullable long[] globalOffset) {
        if (globalWorkSize.length > maxDimensions)
            throw new IllegalArgumentException("invalid dimensions " + globalWorkSize.length);

        dimensions = globalWorkSize.length;
        globalWorkSizesBuffer.limit(dimensions);
        for (int i = 0; i < dimensions; i++)
            globalWorkSizesBuffer.put(i, globalWorkSize[i]);

        if (localWorkSize != null) {
            localWorkSizesBuffer.limit(dimensions);
            for (int i = 0; i < dimensions; i++)
                localWorkSizesBuffer.put(i, localWorkSize[i]);
        } else
            localWorkSizesBuffer.limit(0);

        if (globalOffset != null) {
            globalWorkOffsetsBuffer.limit(dimensions);
            for (int i = 0; i < dimensions; i++)
                globalWorkOffsetsBuffer.put(i, globalOffset[i]);
        } else
            globalWorkOffsetsBuffer.limit(0);
    }

    public void validate(Collection<CLDevice> devices) {
        devices.forEach(this::validate);
    }

    public void validate(CLDevice device) {
        int max = device.getDeviceInfo(DeviceInfo.DEVICE_MAX_WORK_ITEM_DIMENSIONS);
        if (dimensions > max)
            throw new IllegalStateException(String.format("Invalid dimensions : %d > %d", dimensions, max));

        if (localWorkSizesBuffer.hasRemaining()) {
            long[] maxSize = device.getDeviceInfo(DeviceInfo.DEVICE_MAX_WORK_ITEM_SIZES);
            long workGroupSize = 1;
            for (int i = 0; i < dimensions; i++) {
                long size = localWorkSizesBuffer.get(i);
                if (size > maxSize[i])
                    throw new IllegalStateException(String.format("Invalid local work size : %d > %d", size, maxSize[i]));
                workGroupSize *= size;
            }

            long maxl = device.getDeviceInfo(DeviceInfo.DEVICE_MAX_WORK_GROUP_SIZE);
            if (workGroupSize > maxl)
                throw new IllegalStateException(String.format("Invalid work group size : %d > %d", workGroupSize, maxl));
        }
    }


}
