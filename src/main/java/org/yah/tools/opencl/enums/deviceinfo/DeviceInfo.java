package org.yah.tools.opencl.enums.deviceinfo;

import org.yah.tools.opencl.CLVersion;
import org.yah.tools.opencl.enums.*;
import org.yah.tools.opencl.platform.CLDevice;

import java.nio.ByteBuffer;

import static org.lwjgl.opencl.CL10.*;
import static org.lwjgl.opencl.CL11.*;
import static org.lwjgl.opencl.CL11.CL_DEVICE_NATIVE_VECTOR_WIDTH_HALF;
import static org.lwjgl.opencl.CL12.*;
import static org.lwjgl.opencl.CL20.*;
import static org.lwjgl.opencl.CL20.CL_DEVICE_SVM_CAPABILITIES;
import static org.lwjgl.opencl.CL21.*;

public enum DeviceInfo implements CLEnum, CLDeviceInfoReader {

    DEVICE_TYPE(CL_DEVICE_TYPE, CLInfoReader.cl_enum(DeviceType.class)),
    DEVICE_VENDOR_ID(CL_DEVICE_VENDOR_ID, CLInfoReader.cl_uint()),

    DEVICE_MAX_COMPUTE_UNITS(CL_DEVICE_MAX_COMPUTE_UNITS, CLInfoReader.cl_uint()),

    DEVICE_MAX_WORK_ITEM_DIMENSIONS(CL_DEVICE_MAX_WORK_ITEM_DIMENSIONS, CLInfoReader.cl_uint()),

    DEVICE_MAX_WORK_GROUP_SIZE(CL_DEVICE_MAX_WORK_GROUP_SIZE, CLDeviceInfoReader.size_t()),

    DEVICE_MAX_WORK_ITEM_SIZES(CL_DEVICE_MAX_WORK_ITEM_SIZES, size_t_array()),

    DEVICE_PREFERRED_VECTOR_WIDTH_CHAR(CL_DEVICE_PREFERRED_VECTOR_WIDTH_CHAR, CLInfoReader.cl_uint()),
    DEVICE_PREFERRED_VECTOR_WIDTH_SHORT(CL_DEVICE_PREFERRED_VECTOR_WIDTH_SHORT, CLInfoReader.cl_uint()),
    DEVICE_PREFERRED_VECTOR_WIDTH_INT(CL_DEVICE_PREFERRED_VECTOR_WIDTH_INT, CLInfoReader.cl_uint()),
    DEVICE_PREFERRED_VECTOR_WIDTH_LONG(CL_DEVICE_PREFERRED_VECTOR_WIDTH_LONG, CLInfoReader.cl_uint()),
    DEVICE_PREFERRED_VECTOR_WIDTH_FLOAT(CL_DEVICE_PREFERRED_VECTOR_WIDTH_FLOAT, CLInfoReader.cl_uint()),
    DEVICE_PREFERRED_VECTOR_WIDTH_DOUBLE(CL_DEVICE_PREFERRED_VECTOR_WIDTH_DOUBLE, CLInfoReader.cl_uint()),

    DEVICE_MAX_CLOCK_FREQUENCY(CL_DEVICE_MAX_CLOCK_FREQUENCY, CLInfoReader.cl_uint()),

    DEVICE_ADDRESS_BITS(CL_DEVICE_ADDRESS_BITS, CLInfoReader.cl_enum(DeviceAddressBits.class)),

    DEVICE_MAX_READ_IMAGE_ARGS(CL_DEVICE_MAX_READ_IMAGE_ARGS, CLInfoReader.cl_uint()),
    DEVICE_MAX_WRITE_IMAGE_ARGS(CL_DEVICE_MAX_WRITE_IMAGE_ARGS, CLInfoReader.cl_uint()),

    /**
     * ulong
     */
    DEVICE_MAX_MEM_ALLOC_SIZE(CL_DEVICE_MAX_MEM_ALLOC_SIZE, CLInfoReader.cl_long()),

    DEVICE_IMAGE2D_MAX_WIDTH(CL_DEVICE_IMAGE2D_MAX_WIDTH, CLDeviceInfoReader.size_t()),

    DEVICE_IMAGE2D_MAX_HEIGHT(CL_DEVICE_IMAGE2D_MAX_HEIGHT, CLDeviceInfoReader.size_t()),

    DEVICE_IMAGE3D_MAX_WIDTH(CL_DEVICE_IMAGE3D_MAX_WIDTH, CLDeviceInfoReader.size_t()),

    DEVICE_IMAGE3D_MAX_HEIGHT(CL_DEVICE_IMAGE3D_MAX_HEIGHT, CLDeviceInfoReader.size_t()),

    DEVICE_IMAGE3D_MAX_DEPTH(CL_DEVICE_IMAGE3D_MAX_DEPTH, CLDeviceInfoReader.size_t()),

    DEVICE_IMAGE_SUPPORT(CL_DEVICE_IMAGE_SUPPORT, CLInfoReader.cl_bool()),

    DEVICE_MAX_PARAMETER_SIZE(CL_DEVICE_MAX_PARAMETER_SIZE, CLDeviceInfoReader.size_t()),

    DEVICE_MAX_SAMPLERS(CL_DEVICE_MAX_SAMPLERS, CLInfoReader.cl_uint()),

    DEVICE_MEM_BASE_ADDR_ALIGN(CL_DEVICE_MEM_BASE_ADDR_ALIGN, CLInfoReader.cl_uint()),

    DEVICE_GLOBAL_MEM_CACHELINE_SIZE(CL_DEVICE_GLOBAL_MEM_CACHELINE_SIZE, CLInfoReader.cl_uint()),

    DEVICE_GLOBAL_MEM_CACHE_SIZE(CL_DEVICE_GLOBAL_MEM_CACHE_SIZE, CLInfoReader.cl_long()),

    DEVICE_GLOBAL_MEM_SIZE(CL_DEVICE_GLOBAL_MEM_SIZE, CLInfoReader.cl_long()),

    DEVICE_MAX_CONSTANT_ARGS(CL_DEVICE_MAX_CONSTANT_ARGS, CLInfoReader.cl_uint()),

    // ulong
    DEVICE_LOCAL_MEM_SIZE(CL_DEVICE_LOCAL_MEM_SIZE, CLInfoReader.cl_long()),

    DEVICE_ERROR_CORRECTION_SUPPORT(CL_DEVICE_ERROR_CORRECTION_SUPPORT, CLInfoReader.cl_bool()),

    DEVICE_PROFILING_TIMER_RESOLUTION(CL_DEVICE_PROFILING_TIMER_RESOLUTION, CLDeviceInfoReader.size_t()),

    DEVICE_ENDIAN_LITTLE(CL_DEVICE_ENDIAN_LITTLE, CLInfoReader.cl_bool()),

    DEVICE_AVAILABLE(CL_DEVICE_AVAILABLE, CLInfoReader.cl_bool()),

    DEVICE_COMPILER_AVAILABLE(CL_DEVICE_COMPILER_AVAILABLE, CLInfoReader.cl_bool()),

    DEVICE_EXECUTION_CAPABILITIES(CL_DEVICE_EXECUTION_CAPABILITIES, CLInfoReader.cl_bitfield(DeviceExecutionCapability.class)),

    DEVICE_QUEUE_PROPERTIES(CL_DEVICE_QUEUE_PROPERTIES, CLInfoReader.cl_bitfield(CommandQueueProperty.class)),

    DEVICE_NAME(CL_DEVICE_NAME, CLInfoReader.cl_string()),

    DEVICE_VENDOR(CL_DEVICE_VENDOR, CLInfoReader.cl_string()),

    DRIVER_VERSION(CL_DRIVER_VERSION, CLInfoReader.cl_string()),

    DEVICE_OPENCL_C_VERSION(CL_DEVICE_OPENCL_C_VERSION, CLInfoReader.cl_string(), CLVersion.CL11),

    DEVICE_PROFILE(CL_DEVICE_PROFILE, CLInfoReader.cl_string()),

    DEVICE_VERSION(CL_DEVICE_VERSION, CLInfoReader.cl_string()),

    DEVICE_EXTENSIONS(CL_DEVICE_EXTENSIONS, CLInfoReader.cl_string()),

    DEVICE_LINKER_AVAILABLE(CL_DEVICE_LINKER_AVAILABLE, CLInfoReader.cl_bool(), CLVersion.CL12),

    DEVICE_BUILT_IN_KERNELS(CL_DEVICE_BUILT_IN_KERNELS, CLInfoReader.cl_string(), CLVersion.CL12),

    DEVICE_HOST_UNIFIED_MEMORY(CL_DEVICE_HOST_UNIFIED_MEMORY, CLInfoReader.cl_bool(), CLVersion.CL11),

    DEVICE_PLATFORM(CL_DEVICE_PLATFORM, CLInfoReader.cl_long()),

    DEVICE_PREFERRED_VECTOR_WIDTH_HALF(CL_DEVICE_PREFERRED_VECTOR_WIDTH_HALF, CLInfoReader.cl_uint(), CLVersion.CL11),

    DEVICE_NATIVE_VECTOR_WIDTH_CHAR(CL_DEVICE_NATIVE_VECTOR_WIDTH_CHAR, CLInfoReader.cl_uint(), CLVersion.CL11),

    DEVICE_NATIVE_VECTOR_WIDTH_SHORT(CL_DEVICE_NATIVE_VECTOR_WIDTH_SHORT, CLInfoReader.cl_uint(), CLVersion.CL11),

    DEVICE_NATIVE_VECTOR_WIDTH_INT(CL_DEVICE_NATIVE_VECTOR_WIDTH_INT, CLInfoReader.cl_uint(), CLVersion.CL11),

    DEVICE_NATIVE_VECTOR_WIDTH_LONG(CL_DEVICE_NATIVE_VECTOR_WIDTH_LONG, CLInfoReader.cl_uint(), CLVersion.CL11),

    DEVICE_NATIVE_VECTOR_WIDTH_FLOAT(CL_DEVICE_NATIVE_VECTOR_WIDTH_FLOAT, CLInfoReader.cl_uint(), CLVersion.CL11),

    DEVICE_NATIVE_VECTOR_WIDTH_DOUBLE(CL_DEVICE_NATIVE_VECTOR_WIDTH_DOUBLE, CLInfoReader.cl_uint(), CLVersion.CL11),

    DEVICE_NATIVE_VECTOR_WIDTH_HALF(CL_DEVICE_NATIVE_VECTOR_WIDTH_HALF, CLInfoReader.cl_uint(), CLVersion.CL11),

    DEVICE_IMAGE_MAX_BUFFER_SIZE(CL_DEVICE_IMAGE_MAX_BUFFER_SIZE, CLDeviceInfoReader.size_t(), CLVersion.CL12),

    DEVICE_IMAGE_MAX_ARRAY_SIZE(CL_DEVICE_IMAGE_MAX_ARRAY_SIZE, CLDeviceInfoReader.size_t(), CLVersion.CL12),

    DEVICE_PARENT_DEVICE(CL_DEVICE_PARENT_DEVICE, CLInfoReader.cl_long(), CLVersion.CL12),

    DEVICE_PARTITION_MAX_SUB_DEVICES(CL_DEVICE_PARTITION_MAX_SUB_DEVICES, CLInfoReader.cl_uint(), CLVersion.CL12),

    DEVICE_REFERENCE_COUNT(CL_DEVICE_REFERENCE_COUNT, CLInfoReader.cl_uint(), CLVersion.CL12),

    DEVICE_PREFERRED_INTEROP_USER_SYNC(CL_DEVICE_PREFERRED_INTEROP_USER_SYNC, CLInfoReader.cl_bool(), CLVersion.CL12),

    DEVICE_PRINTF_BUFFER_SIZE(CL_DEVICE_PRINTF_BUFFER_SIZE, CLDeviceInfoReader.size_t(), CLVersion.CL12),

    DEVICE_QUEUE_ON_HOST_PROPERTIES(CL_DEVICE_QUEUE_ON_HOST_PROPERTIES, CLInfoReader.cl_bitfield(CommandQueueProperty.class), CLVersion.CL20),

    DEVICE_MAX_READ_WRITE_IMAGE_ARGS(CL_DEVICE_MAX_READ_WRITE_IMAGE_ARGS, CLInfoReader.cl_uint(), CLVersion.CL20),

    DEVICE_MAX_GLOBAL_VARIABLE_SIZE(CL_DEVICE_MAX_GLOBAL_VARIABLE_SIZE, CLDeviceInfoReader.size_t(), CLVersion.CL20),

    DEVICE_QUEUE_ON_DEVICE_PROPERTIES(CL_DEVICE_QUEUE_ON_DEVICE_PROPERTIES, CLInfoReader.cl_bitfield(CommandQueueProperty.class), CLVersion.CL20),

    DEVICE_QUEUE_ON_DEVICE_PREFERRED_SIZE(CL_DEVICE_QUEUE_ON_DEVICE_PREFERRED_SIZE, CLInfoReader.cl_uint(), CLVersion.CL20),

    DEVICE_QUEUE_ON_DEVICE_MAX_SIZE(CL_DEVICE_QUEUE_ON_DEVICE_MAX_SIZE, CLInfoReader.cl_uint(), CLVersion.CL20),

    DEVICE_MAX_ON_DEVICE_QUEUES(CL_DEVICE_MAX_ON_DEVICE_QUEUES, CLInfoReader.cl_uint(), CLVersion.CL20),

    DEVICE_MAX_ON_DEVICE_EVENTS(CL_DEVICE_MAX_ON_DEVICE_EVENTS, CLInfoReader.cl_uint(), CLVersion.CL20),

    DEVICE_GLOBAL_VARIABLE_PREFERRED_TOTAL_SIZE(CL_DEVICE_GLOBAL_VARIABLE_PREFERRED_TOTAL_SIZE, CLDeviceInfoReader.size_t(), CLVersion.CL20),

    DEVICE_MAX_PIPE_ARGS(CL_DEVICE_MAX_PIPE_ARGS, CLInfoReader.cl_uint(), CLVersion.CL20),

    DEVICE_PIPE_MAX_ACTIVE_RESERVATIONS(CL_DEVICE_PIPE_MAX_ACTIVE_RESERVATIONS, CLInfoReader.cl_uint(), CLVersion.CL20),

    DEVICE_PIPE_MAX_PACKET_SIZE(CL_DEVICE_PIPE_MAX_PACKET_SIZE, CLInfoReader.cl_uint(), CLVersion.CL20),

    DEVICE_PREFERRED_PLATFORM_ATOMIC_ALIGNMENT(CL_DEVICE_PREFERRED_PLATFORM_ATOMIC_ALIGNMENT, CLInfoReader.cl_uint(), CLVersion.CL20),

    DEVICE_PREFERRED_GLOBAL_ATOMIC_ALIGNMENT(CL_DEVICE_PREFERRED_GLOBAL_ATOMIC_ALIGNMENT, CLInfoReader.cl_uint(), CLVersion.CL20),

    DEVICE_PREFERRED_LOCAL_ATOMIC_ALIGNMENT(CL_DEVICE_PREFERRED_LOCAL_ATOMIC_ALIGNMENT, CLInfoReader.cl_uint(), CLVersion.CL20),

    DEVICE_IL_VERSION(CL_DEVICE_IL_VERSION, CLInfoReader.cl_string(), CLVersion.CL21),

    DEVICE_MAX_NUM_SUB_GROUPS(CL_DEVICE_MAX_NUM_SUB_GROUPS, CLInfoReader.cl_uint(), CLVersion.CL21),

    DEVICE_SUB_GROUP_INDEPENDENT_FORWARD_PROGRESS(CL_DEVICE_SUB_GROUP_INDEPENDENT_FORWARD_PROGRESS, CLInfoReader.cl_bool(), CLVersion.CL21),

    DEVICE_SINGLE_FP_CONFIG(CL_DEVICE_SINGLE_FP_CONFIG, CLInfoReader.cl_bitfield(DeviceFPConfig.class)),

    DEVICE_DOUBLE_FP_CONFIG(CL_DEVICE_DOUBLE_FP_CONFIG, CLInfoReader.cl_bitfield(DeviceFPConfig.class), CLVersion.CL12),

    DEVICE_GLOBAL_MEM_CACHE_TYPE(CL_DEVICE_GLOBAL_MEM_CACHE_TYPE, CLInfoReader.cl_enum(GlobalMemCacheType.class)),

    DEVICE_LOCAL_MEM_TYPE(CL_DEVICE_LOCAL_MEM_TYPE, CLInfoReader.cl_enum(LocalMemType.class)),

    DEVICE_PARTITION_AFFINITY_DOMAIN(CL_DEVICE_PARTITION_AFFINITY_DOMAIN, CLInfoReader.cl_bitfield(DeviceAffinityDomain.class), CLVersion.CL12),

    DEVICE_SVM_CAPABILITIES(CL_DEVICE_SVM_CAPABILITIES, CLInfoReader.cl_bitfield(DeviceSVMCapability.class), CLVersion.CL20),

    // unhandled

    // usigned long long
    DEVICE_MAX_CONSTANT_BUFFER_SIZE(CL_DEVICE_MAX_CONSTANT_BUFFER_SIZE, (device, buffer) -> buffer),

    DEVICE_PARTITION_TYPE(CL_DEVICE_PARTITION_TYPE, (device, buffer) -> buffer, CLVersion.CL12),

    DEVICE_PARTITION_PROPERTIES(CL_DEVICE_PARTITION_PROPERTIES, (device, buffer) -> buffer, CLVersion.CL12);

    private final int id;
    private final CLVersion version;
    private final CLDeviceInfoReader reader;

    DeviceInfo(int id, CLDeviceInfoReader reader) {
        this(id, reader, CLVersion.CL10);
    }

    DeviceInfo(int id, CLDeviceInfoReader reader, CLVersion version) {
        this.id = id;
        this.version = version;
        this.reader = reader;
    }

    public Object read(CLDevice device, ByteBuffer buffer) {
        return reader.read(device, buffer);
    }

    @Override
    public int id() {
        return id;
    }

    @Override
    public CLVersion version() {
        return version;
    }

    private static CLDeviceInfoReader size_t_array() {
        CLDeviceInfoReader sizeReader = CLDeviceInfoReader.size_t();
        return (device, buffer) -> {
            int count = buffer.remaining() / device.getAddressBits().bytes();
            long[] ptrs = new long[count];
            for (int i = 0; i < count; i++)
                ptrs[i] = (long) sizeReader.read(device, buffer);
            return ptrs;
        };
    }
}
