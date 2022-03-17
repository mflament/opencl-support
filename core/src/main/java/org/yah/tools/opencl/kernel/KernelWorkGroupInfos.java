package org.yah.tools.opencl.kernel;

import org.yah.tools.opencl.enums.wglinfo.KernelWorkGroupInfo;
import org.yah.tools.opencl.platform.CLDevice;

import java.nio.LongBuffer;
import java.util.Arrays;
import java.util.Objects;

public final class KernelWorkGroupInfos {

    public static KernelWorkGroupInfos create(CLKernel kernel, CLDevice device) {
        return new KernelWorkGroupInfos(
                safeGet(kernel, device, KernelWorkGroupInfo.KERNEL_WORK_GROUP_SIZE),
                safeGetArray(kernel, device, KernelWorkGroupInfo.KERNEL_COMPILE_WORK_GROUP_SIZE),
                safeGet(kernel, device, KernelWorkGroupInfo.KERNEL_LOCAL_MEM_SIZE),
                safeGetArray(kernel, device, KernelWorkGroupInfo.KERNEL_PREFERRED_WORK_GROUP_SIZE_MULTIPLE),
                safeGetArray(kernel, device, KernelWorkGroupInfo.KERNEL_PRIVATE_MEM_SIZE),
                safeGetArray(kernel, device, KernelWorkGroupInfo.KERNEL_GLOBAL_WORK_SIZE)
        );
    }

    private final long workGroupSize;
    private final long[] compileWorkGroupSize;
    private final long localMemSize;
    private final long[] preferredWorkGroupSizeMultiple;
    private final long[] privateMemSize;
    private final long[] globalWorkSize;

    public KernelWorkGroupInfos(long workGroupSize, long[] compileWorkGroupSize, long localMemSize,
                                long[] preferredWorkGroupSizeMultiple, long[] privateMemSize, long[] globalWorkSize) {
        this.workGroupSize = workGroupSize;
        this.compileWorkGroupSize = Objects.requireNonNull(compileWorkGroupSize, "compileWorkGroupSize is null");
        this.localMemSize = localMemSize;
        this.preferredWorkGroupSizeMultiple = Objects.requireNonNull(preferredWorkGroupSizeMultiple, "preferredWorkGroupSizeMultiple is null");
        this.privateMemSize = Objects.requireNonNull(privateMemSize, "privateMemSize is null");
        this.globalWorkSize = Objects.requireNonNull(globalWorkSize, "globalWorkSize is null");
    }

    public long getWorkGroupSize() {
        return workGroupSize;
    }

    public long[] getCompileWorkGroupSize() {
        return compileWorkGroupSize;
    }

    public long getLocalMemSize() {
        return localMemSize;
    }

    public long[] getPreferredWorkGroupSizeMultiple() {
        return preferredWorkGroupSizeMultiple;
    }

    public long[] getPrivateMemSize() {
        return privateMemSize;
    }

    public long[] getGlobalWorkSize() {
        return globalWorkSize;
    }

    @Override
    public String toString() {
        return "KernelWorkGroupInfos{" +
                "workGroupSize=" + workGroupSize +
                ", compileWorkGroupSize=" + Arrays.toString(compileWorkGroupSize) +
                ", localMemSize=" + localMemSize +
                ", preferredWorkGroupSizeMultiple=" + Arrays.toString(preferredWorkGroupSizeMultiple) +
                ", privateMemSize=" + Arrays.toString(privateMemSize) +
                ", globalWorkSize=" + Arrays.toString(globalWorkSize) +
                '}';
    }

    private static long safeGet(CLKernel kernel, CLDevice device, KernelWorkGroupInfo<Long> info) {
        try {
            return kernel.getKernelWorkGroupInfo(device, info);
        } catch (Exception e) {
            return 0;
        }
    }

    private static long[] safeGetArray(CLKernel kernel, CLDevice device, KernelWorkGroupInfo<LongBuffer> info) {
        try {
            return toArray(kernel.getKernelWorkGroupInfo(device, info));
        } catch (Exception e) {
            return new long[0];
        }
    }

    private static long[] toArray(LongBuffer buffer) {
        int size = buffer.remaining();
        long[] array = new long[size];
        int offset = buffer.position();
        for (int i = 0; i < size; i++)
            array[i] = buffer.get(offset + i);
        return array;
    }
}
