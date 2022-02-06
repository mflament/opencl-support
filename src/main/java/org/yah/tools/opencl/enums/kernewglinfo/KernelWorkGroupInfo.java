package org.yah.tools.opencl.enums.kernewglinfo;

import org.yah.tools.opencl.CLVersion;
import org.yah.tools.opencl.CLDeviceInfoReader;
import org.yah.tools.opencl.enums.CLEnum;
import org.yah.tools.opencl.CLInfoReader;
import org.yah.tools.opencl.platform.CLDevice;

import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.lwjgl.opencl.CL12.*;

public class KernelWorkGroupInfo<T> implements CLEnum, CLDeviceInfoReader<T> {

    public static final KernelWorkGroupInfo<Long> KERNEL_WORK_GROUP_SIZE = new KernelWorkGroupInfo<>("KERNEL_WORK_GROUP_SIZE", CL_KERNEL_WORK_GROUP_SIZE, CLDeviceInfoReader.size_t());
    public static final KernelWorkGroupInfo<LongBuffer> KERNEL_COMPILE_WORK_GROUP_SIZE = new KernelWorkGroupInfo<>("KERNEL_COMPILE_WORK_GROUP_SIZE", CL_KERNEL_COMPILE_WORK_GROUP_SIZE, CLDeviceInfoReader.size_t_array());
    public static final KernelWorkGroupInfo<Long> KERNEL_LOCAL_MEM_SIZE = new KernelWorkGroupInfo<>("KERNEL_LOCAL_MEM_SIZE", CL_KERNEL_LOCAL_MEM_SIZE, CLInfoReader.cl_long());
    public static final KernelWorkGroupInfo<LongBuffer> KERNEL_PREFERRED_WORK_GROUP_SIZE_MULTIPLE = new KernelWorkGroupInfo<>("KERNEL_PREFERRED_WORK_GROUP_SIZE_MULTIPLE", CL_KERNEL_PREFERRED_WORK_GROUP_SIZE_MULTIPLE, CLDeviceInfoReader.size_t_array());
    public static final KernelWorkGroupInfo<LongBuffer> KERNEL_PRIVATE_MEM_SIZE = new KernelWorkGroupInfo<>("KERNEL_PRIVATE_MEM_SIZE", CL_KERNEL_PRIVATE_MEM_SIZE, CLDeviceInfoReader.size_t_array());
    public static final KernelWorkGroupInfo<LongBuffer> KERNEL_GLOBAL_WORK_SIZE = new KernelWorkGroupInfo<>("KERNEL_GLOBAL_WORK_SIZE", CL_KERNEL_GLOBAL_WORK_SIZE, CLDeviceInfoReader.size_t_array(), CLVersion.CL12);

    public static List<KernelWorkGroupInfo<?>> KERNEL_WORK_GROUP_INFOS = Collections.unmodifiableList(Arrays.asList(
            KERNEL_GLOBAL_WORK_SIZE,
            KERNEL_WORK_GROUP_SIZE,
            KERNEL_COMPILE_WORK_GROUP_SIZE,
            KERNEL_LOCAL_MEM_SIZE,
            KERNEL_PREFERRED_WORK_GROUP_SIZE_MULTIPLE,
            KERNEL_PRIVATE_MEM_SIZE
    ));

    private final String name;
    private final int id;
    private final CLVersion version;
    private final CLDeviceInfoReader<T> reader;

    private KernelWorkGroupInfo(String name, int id, CLDeviceInfoReader<T> reader) {
        this(name, id, reader, CLVersion.CL10);
    }

    private KernelWorkGroupInfo(String name, int id, CLDeviceInfoReader<T> reader, CLVersion version) {
        this.name = name;
        this.id = id;
        this.version = version;
        this.reader = reader;
    }

    public String name() {
        return name;
    }

    @Override
    public int id() {
        return id;
    }

    @Override
    public CLVersion version() {
        return version;
    }

    @Override
    public T read(CLDevice device, ByteBuffer buffer) {
        return reader.read(device, buffer);
    }

    @Override
    public String toString() {
        return name;
    }
}
