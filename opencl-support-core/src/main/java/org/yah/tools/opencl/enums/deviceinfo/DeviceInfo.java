package org.yah.tools.opencl.enums.deviceinfo;

import org.yah.tools.opencl.CLDeviceInfoReader;
import org.yah.tools.opencl.CLInfoReader;
import org.yah.tools.opencl.CLVersion;
import org.yah.tools.opencl.enums.*;
import org.yah.tools.opencl.platform.CLDevice;

import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.util.*;

import static org.lwjgl.opencl.CL21.*;

public class DeviceInfo<T> implements CLEnum, CLDeviceInfoReader<T> {

    public static final DeviceInfo<DeviceType> DEVICE_TYPE = new DeviceInfo<>("DEVICE_TYPE", CL_DEVICE_TYPE, CLInfoReader.cl_enum(DeviceType.class));

    public static final DeviceInfo<Integer> DEVICE_VENDOR_ID = new DeviceInfo<>("DEVICE_VENDOR_ID", CL_DEVICE_VENDOR_ID, CLInfoReader.cl_uint());

    public static final DeviceInfo<Integer> DEVICE_MAX_COMPUTE_UNITS = new DeviceInfo<>("DEVICE_MAX_COMPUTE_UNITS", CL_DEVICE_MAX_COMPUTE_UNITS, CLInfoReader.cl_uint());

    public static final DeviceInfo<Integer> DEVICE_MAX_WORK_ITEM_DIMENSIONS = new DeviceInfo<>("DEVICE_MAX_WORK_ITEM_DIMENSIONS", CL_DEVICE_MAX_WORK_ITEM_DIMENSIONS, CLInfoReader.cl_uint());

    public static final DeviceInfo<Long> DEVICE_MAX_WORK_GROUP_SIZE = new DeviceInfo<>("DEVICE_MAX_WORK_GROUP_SIZE", CL_DEVICE_MAX_WORK_GROUP_SIZE, CLDeviceInfoReader.size_t());

    public static final DeviceInfo<LongBuffer> DEVICE_MAX_WORK_ITEM_SIZES = new DeviceInfo<>("DEVICE_MAX_WORK_ITEM_SIZES", CL_DEVICE_MAX_WORK_ITEM_SIZES, CLDeviceInfoReader.size_t_array());

    public static final DeviceInfo<Integer> DEVICE_PREFERRED_VECTOR_WIDTH_CHAR = new DeviceInfo<>("DEVICE_PREFERRED_VECTOR_WIDTH_CHAR", CL_DEVICE_PREFERRED_VECTOR_WIDTH_CHAR, CLInfoReader.cl_uint());

    public static final DeviceInfo<Integer> DEVICE_PREFERRED_VECTOR_WIDTH_SHORT = new DeviceInfo<>("DEVICE_PREFERRED_VECTOR_WIDTH_SHORT", CL_DEVICE_PREFERRED_VECTOR_WIDTH_SHORT, CLInfoReader.cl_uint());

    public static final DeviceInfo<Integer> DEVICE_PREFERRED_VECTOR_WIDTH_INT = new DeviceInfo<>("DEVICE_PREFERRED_VECTOR_WIDTH_INT", CL_DEVICE_PREFERRED_VECTOR_WIDTH_INT, CLInfoReader.cl_uint());

    public static final DeviceInfo<Integer> DEVICE_PREFERRED_VECTOR_WIDTH_LONG = new DeviceInfo<>("DEVICE_PREFERRED_VECTOR_WIDTH_LONG", CL_DEVICE_PREFERRED_VECTOR_WIDTH_LONG, CLInfoReader.cl_uint());

    public static final DeviceInfo<Integer> DEVICE_PREFERRED_VECTOR_WIDTH_FLOAT = new DeviceInfo<>("DEVICE_PREFERRED_VECTOR_WIDTH_FLOAT", CL_DEVICE_PREFERRED_VECTOR_WIDTH_FLOAT, CLInfoReader.cl_uint());

    public static final DeviceInfo<Integer> DEVICE_PREFERRED_VECTOR_WIDTH_DOUBLE = new DeviceInfo<>("DEVICE_PREFERRED_VECTOR_WIDTH_DOUBLE", CL_DEVICE_PREFERRED_VECTOR_WIDTH_DOUBLE, CLInfoReader.cl_uint());

    public static final DeviceInfo<Integer> DEVICE_MAX_CLOCK_FREQUENCY = new DeviceInfo<>("DEVICE_MAX_CLOCK_FREQUENCY", CL_DEVICE_MAX_CLOCK_FREQUENCY, CLInfoReader.cl_uint());

    public static final DeviceInfo<DeviceAddressBits> DEVICE_ADDRESS_BITS = new DeviceInfo<>("DEVICE_ADDRESS_BITS", CL_DEVICE_ADDRESS_BITS, CLInfoReader.cl_enum(DeviceAddressBits.class));

    public static final DeviceInfo<Integer> DEVICE_MAX_READ_IMAGE_ARGS = new DeviceInfo<>("DEVICE_MAX_READ_IMAGE_ARGS", CL_DEVICE_MAX_READ_IMAGE_ARGS, CLInfoReader.cl_uint());

    public static final DeviceInfo<Integer> DEVICE_MAX_WRITE_IMAGE_ARGS = new DeviceInfo<>("DEVICE_MAX_WRITE_IMAGE_ARGS", CL_DEVICE_MAX_WRITE_IMAGE_ARGS, CLInfoReader.cl_uint());

    /**
     * ulong
     */
    public static final DeviceInfo<Long> DEVICE_MAX_MEM_ALLOC_SIZE = new DeviceInfo<>("DEVICE_MAX_MEM_ALLOC_SIZE", CL_DEVICE_MAX_MEM_ALLOC_SIZE, CLInfoReader.cl_long());

    public static final DeviceInfo<Long> DEVICE_IMAGE2D_MAX_WIDTH = new DeviceInfo<>("DEVICE_IMAGE2D_MAX_WIDTH", CL_DEVICE_IMAGE2D_MAX_WIDTH, CLDeviceInfoReader.size_t());

    public static final DeviceInfo<Long> DEVICE_IMAGE2D_MAX_HEIGHT = new DeviceInfo<>("DEVICE_IMAGE2D_MAX_HEIGHT", CL_DEVICE_IMAGE2D_MAX_HEIGHT, CLDeviceInfoReader.size_t());

    public static final DeviceInfo<Long> DEVICE_IMAGE3D_MAX_WIDTH = new DeviceInfo<>("DEVICE_IMAGE3D_MAX_WIDTH", CL_DEVICE_IMAGE3D_MAX_WIDTH, CLDeviceInfoReader.size_t());

    public static final DeviceInfo<Long> DEVICE_IMAGE3D_MAX_HEIGHT = new DeviceInfo<>("DEVICE_IMAGE3D_MAX_HEIGHT", CL_DEVICE_IMAGE3D_MAX_HEIGHT, CLDeviceInfoReader.size_t());

    public static final DeviceInfo<Long> DEVICE_IMAGE3D_MAX_DEPTH = new DeviceInfo<>("DEVICE_IMAGE3D_MAX_DEPTH", CL_DEVICE_IMAGE3D_MAX_DEPTH, CLDeviceInfoReader.size_t());

    public static final DeviceInfo<Boolean> DEVICE_IMAGE_SUPPORT = new DeviceInfo<>("DEVICE_IMAGE_SUPPORT", CL_DEVICE_IMAGE_SUPPORT, CLInfoReader.cl_bool());

    public static final DeviceInfo<Long> DEVICE_MAX_PARAMETER_SIZE = new DeviceInfo<>("DEVICE_MAX_PARAMETER_SIZE", CL_DEVICE_MAX_PARAMETER_SIZE, CLDeviceInfoReader.size_t());

    public static final DeviceInfo<Integer> DEVICE_MAX_SAMPLERS = new DeviceInfo<>("DEVICE_MAX_SAMPLERS", CL_DEVICE_MAX_SAMPLERS, CLInfoReader.cl_uint());

    public static final DeviceInfo<Integer> DEVICE_MEM_BASE_ADDR_ALIGN = new DeviceInfo<>("DEVICE_MEM_BASE_ADDR_ALIGN", CL_DEVICE_MEM_BASE_ADDR_ALIGN, CLInfoReader.cl_uint());

    public static final DeviceInfo<Integer> DEVICE_GLOBAL_MEM_CACHELINE_SIZE = new DeviceInfo<>("DEVICE_GLOBAL_MEM_CACHELINE_SIZE", CL_DEVICE_GLOBAL_MEM_CACHELINE_SIZE, CLInfoReader.cl_uint());

    public static final DeviceInfo<Long> DEVICE_GLOBAL_MEM_CACHE_SIZE = new DeviceInfo<>("DEVICE_GLOBAL_MEM_CACHE_SIZE", CL_DEVICE_GLOBAL_MEM_CACHE_SIZE, CLInfoReader.cl_long());

    public static final DeviceInfo<Long> DEVICE_GLOBAL_MEM_SIZE = new DeviceInfo<>("DEVICE_GLOBAL_MEM_SIZE", CL_DEVICE_GLOBAL_MEM_SIZE, CLInfoReader.cl_long());

    public static final DeviceInfo<Integer> DEVICE_MAX_CONSTANT_ARGS = new DeviceInfo<>("DEVICE_MAX_CONSTANT_ARGS", CL_DEVICE_MAX_CONSTANT_ARGS, CLInfoReader.cl_uint());

    // ulong
    public static final DeviceInfo<Long> DEVICE_LOCAL_MEM_SIZE = new DeviceInfo<>("DEVICE_LOCAL_MEM_SIZE", CL_DEVICE_LOCAL_MEM_SIZE, CLInfoReader.cl_long());

    public static final DeviceInfo<Boolean> DEVICE_ERROR_CORRECTION_SUPPORT = new DeviceInfo<>("DEVICE_ERROR_CORRECTION_SUPPORT", CL_DEVICE_ERROR_CORRECTION_SUPPORT, CLInfoReader.cl_bool());

    public static final DeviceInfo<Long> DEVICE_PROFILING_TIMER_RESOLUTION = new DeviceInfo<>("DEVICE_PROFILING_TIMER_RESOLUTION", CL_DEVICE_PROFILING_TIMER_RESOLUTION, CLDeviceInfoReader.size_t());

    public static final DeviceInfo<Boolean> DEVICE_ENDIAN_LITTLE = new DeviceInfo<>("DEVICE_ENDIAN_LITTLE", CL_DEVICE_ENDIAN_LITTLE, CLInfoReader.cl_bool());

    public static final DeviceInfo<Boolean> DEVICE_AVAILABLE = new DeviceInfo<>("DEVICE_AVAILABLE", CL_DEVICE_AVAILABLE, CLInfoReader.cl_bool());

    public static final DeviceInfo<Boolean> DEVICE_COMPILER_AVAILABLE = new DeviceInfo<>("DEVICE_COMPILER_AVAILABLE", CL_DEVICE_COMPILER_AVAILABLE, CLInfoReader.cl_bool());

    public static final DeviceInfo<CLBitfield<DeviceExecutionCapability>> DEVICE_EXECUTION_CAPABILITIES = new DeviceInfo<>("DEVICE_EXECUTION_CAPABILITIES", CL_DEVICE_EXECUTION_CAPABILITIES, CLInfoReader.cl_bitfield(DeviceExecutionCapability.class));

    public static final DeviceInfo<CLBitfield<CommandQueueProperty>> DEVICE_QUEUE_PROPERTIES = new DeviceInfo<>("DEVICE_QUEUE_PROPERTIES", CL_DEVICE_QUEUE_PROPERTIES, CLInfoReader.cl_bitfield(CommandQueueProperty.class));

    public static final DeviceInfo<String> DEVICE_NAME = new DeviceInfo<>("DEVICE_NAME", CL_DEVICE_NAME, CLInfoReader.cl_string());

    public static final DeviceInfo<String> DEVICE_VENDOR = new DeviceInfo<>("DEVICE_VENDOR", CL_DEVICE_VENDOR, CLInfoReader.cl_string());

    public static final DeviceInfo<String> DRIVER_VERSION = new DeviceInfo<>("DRIVER_VERSION", CL_DRIVER_VERSION, CLInfoReader.cl_string());

    public static final DeviceInfo<String> DEVICE_OPENCL_C_VERSION = new DeviceInfo<>("DEVICE_OPENCL_C_VERSION", CL_DEVICE_OPENCL_C_VERSION, CLInfoReader.cl_string(), CLVersion.CL11);

    public static final DeviceInfo<String> DEVICE_PROFILE = new DeviceInfo<>("DEVICE_PROFILE", CL_DEVICE_PROFILE, CLInfoReader.cl_string());

    public static final DeviceInfo<String> DEVICE_VERSION = new DeviceInfo<>("DEVICE_VERSION", CL_DEVICE_VERSION, CLInfoReader.cl_string());

    public static final DeviceInfo<String> DEVICE_EXTENSIONS = new DeviceInfo<>("DEVICE_EXTENSIONS", CL_DEVICE_EXTENSIONS, CLInfoReader.cl_string());

    public static final DeviceInfo<Boolean> DEVICE_LINKER_AVAILABLE = new DeviceInfo<>("DEVICE_LINKER_AVAILABLE", CL_DEVICE_LINKER_AVAILABLE, CLInfoReader.cl_bool(), CLVersion.CL12);

    public static final DeviceInfo<String> DEVICE_BUILT_IN_KERNELS = new DeviceInfo<>("DEVICE_BUILT_IN_KERNELS", CL_DEVICE_BUILT_IN_KERNELS, CLInfoReader.cl_string(), CLVersion.CL12);

    public static final DeviceInfo<Boolean> DEVICE_HOST_UNIFIED_MEMORY = new DeviceInfo<>("DEVICE_HOST_UNIFIED_MEMORY", CL_DEVICE_HOST_UNIFIED_MEMORY, CLInfoReader.cl_bool(), CLVersion.CL11);

    public static final DeviceInfo<Long> DEVICE_PLATFORM = new DeviceInfo<>("DEVICE_PLATFORM", CL_DEVICE_PLATFORM, CLInfoReader.cl_long());

    public static final DeviceInfo<Integer> DEVICE_PREFERRED_VECTOR_WIDTH_HALF = new DeviceInfo<>("DEVICE_PREFERRED_VECTOR_WIDTH_HALF", CL_DEVICE_PREFERRED_VECTOR_WIDTH_HALF, CLInfoReader.cl_uint(), CLVersion.CL11);

    public static final DeviceInfo<Integer> DEVICE_NATIVE_VECTOR_WIDTH_CHAR = new DeviceInfo<>("DEVICE_NATIVE_VECTOR_WIDTH_CHAR", CL_DEVICE_NATIVE_VECTOR_WIDTH_CHAR, CLInfoReader.cl_uint(), CLVersion.CL11);

    public static final DeviceInfo<Integer> DEVICE_NATIVE_VECTOR_WIDTH_SHORT = new DeviceInfo<>("DEVICE_NATIVE_VECTOR_WIDTH_SHORT", CL_DEVICE_NATIVE_VECTOR_WIDTH_SHORT, CLInfoReader.cl_uint(), CLVersion.CL11);

    public static final DeviceInfo<Integer> DEVICE_NATIVE_VECTOR_WIDTH_INT = new DeviceInfo<>("DEVICE_NATIVE_VECTOR_WIDTH_INT", CL_DEVICE_NATIVE_VECTOR_WIDTH_INT, CLInfoReader.cl_uint(), CLVersion.CL11);

    public static final DeviceInfo<Integer> DEVICE_NATIVE_VECTOR_WIDTH_LONG = new DeviceInfo<>("DEVICE_NATIVE_VECTOR_WIDTH_LONG", CL_DEVICE_NATIVE_VECTOR_WIDTH_LONG, CLInfoReader.cl_uint(), CLVersion.CL11);

    public static final DeviceInfo<Integer> DEVICE_NATIVE_VECTOR_WIDTH_FLOAT = new DeviceInfo<>("DEVICE_NATIVE_VECTOR_WIDTH_FLOAT", CL_DEVICE_NATIVE_VECTOR_WIDTH_FLOAT, CLInfoReader.cl_uint(), CLVersion.CL11);

    public static final DeviceInfo<Integer> DEVICE_NATIVE_VECTOR_WIDTH_DOUBLE = new DeviceInfo<>("DEVICE_NATIVE_VECTOR_WIDTH_DOUBLE", CL_DEVICE_NATIVE_VECTOR_WIDTH_DOUBLE, CLInfoReader.cl_uint(), CLVersion.CL11);

    public static final DeviceInfo<Integer> DEVICE_NATIVE_VECTOR_WIDTH_HALF = new DeviceInfo<>("DEVICE_NATIVE_VECTOR_WIDTH_HALF", CL_DEVICE_NATIVE_VECTOR_WIDTH_HALF, CLInfoReader.cl_uint(), CLVersion.CL11);

    public static final DeviceInfo<Long> DEVICE_IMAGE_MAX_BUFFER_SIZE = new DeviceInfo<>("DEVICE_IMAGE_MAX_BUFFER_SIZE", CL_DEVICE_IMAGE_MAX_BUFFER_SIZE, CLDeviceInfoReader.size_t(), CLVersion.CL12);

    public static final DeviceInfo<Long> DEVICE_IMAGE_MAX_ARRAY_SIZE = new DeviceInfo<>("DEVICE_IMAGE_MAX_ARRAY_SIZE", CL_DEVICE_IMAGE_MAX_ARRAY_SIZE, CLDeviceInfoReader.size_t(), CLVersion.CL12);

    public static final DeviceInfo<Long> DEVICE_PARENT_DEVICE = new DeviceInfo<>("DEVICE_PARENT_DEVICE", CL_DEVICE_PARENT_DEVICE, CLInfoReader.cl_long(), CLVersion.CL12);

    public static final DeviceInfo<Integer> DEVICE_PARTITION_MAX_SUB_DEVICES = new DeviceInfo<>("DEVICE_PARTITION_MAX_SUB_DEVICES", CL_DEVICE_PARTITION_MAX_SUB_DEVICES, CLInfoReader.cl_uint(), CLVersion.CL12);

    public static final DeviceInfo<Integer> DEVICE_REFERENCE_COUNT = new DeviceInfo<>("DEVICE_REFERENCE_COUNT", CL_DEVICE_REFERENCE_COUNT, CLInfoReader.cl_uint(), CLVersion.CL12);

    public static final DeviceInfo<Boolean> DEVICE_PREFERRED_INTEROP_USER_SYNC = new DeviceInfo<>("DEVICE_PREFERRED_INTEROP_USER_SYNC", CL_DEVICE_PREFERRED_INTEROP_USER_SYNC, CLInfoReader.cl_bool(), CLVersion.CL12);

    public static final DeviceInfo<Long> DEVICE_PRINTF_BUFFER_SIZE = new DeviceInfo<>("DEVICE_PRINTF_BUFFER_SIZE", CL_DEVICE_PRINTF_BUFFER_SIZE, CLDeviceInfoReader.size_t(), CLVersion.CL12);

    public static final DeviceInfo<CLBitfield<CommandQueueProperty>> DEVICE_QUEUE_ON_HOST_PROPERTIES = new DeviceInfo<>("DEVICE_QUEUE_ON_HOST_PROPERTIES", CL_DEVICE_QUEUE_ON_HOST_PROPERTIES, CLInfoReader.cl_bitfield(CommandQueueProperty.class), CLVersion.CL20);

    public static final DeviceInfo<Integer> DEVICE_MAX_READ_WRITE_IMAGE_ARGS = new DeviceInfo<>("DEVICE_MAX_READ_WRITE_IMAGE_ARGS", CL_DEVICE_MAX_READ_WRITE_IMAGE_ARGS, CLInfoReader.cl_uint(), CLVersion.CL20);

    public static final DeviceInfo<Long> DEVICE_MAX_GLOBAL_VARIABLE_SIZE = new DeviceInfo<>("DEVICE_MAX_GLOBAL_VARIABLE_SIZE", CL_DEVICE_MAX_GLOBAL_VARIABLE_SIZE, CLDeviceInfoReader.size_t(), CLVersion.CL20);

    public static final DeviceInfo<CLBitfield<CommandQueueProperty>> DEVICE_QUEUE_ON_DEVICE_PROPERTIES = new DeviceInfo<>("DEVICE_QUEUE_ON_DEVICE_PROPERTIES", CL_DEVICE_QUEUE_ON_DEVICE_PROPERTIES, CLInfoReader.cl_bitfield(CommandQueueProperty.class), CLVersion.CL20);

    public static final DeviceInfo<Integer> DEVICE_QUEUE_ON_DEVICE_PREFERRED_SIZE = new DeviceInfo<>("DEVICE_QUEUE_ON_DEVICE_PREFERRED_SIZE", CL_DEVICE_QUEUE_ON_DEVICE_PREFERRED_SIZE, CLInfoReader.cl_uint(), CLVersion.CL20);

    public static final DeviceInfo<Integer> DEVICE_QUEUE_ON_DEVICE_MAX_SIZE = new DeviceInfo<>("DEVICE_QUEUE_ON_DEVICE_MAX_SIZE", CL_DEVICE_QUEUE_ON_DEVICE_MAX_SIZE, CLInfoReader.cl_uint(), CLVersion.CL20);

    public static final DeviceInfo<Integer> DEVICE_MAX_ON_DEVICE_QUEUES = new DeviceInfo<>("DEVICE_MAX_ON_DEVICE_QUEUES", CL_DEVICE_MAX_ON_DEVICE_QUEUES, CLInfoReader.cl_uint(), CLVersion.CL20);

    public static final DeviceInfo<Integer> DEVICE_MAX_ON_DEVICE_EVENTS = new DeviceInfo<>("DEVICE_MAX_ON_DEVICE_EVENTS", CL_DEVICE_MAX_ON_DEVICE_EVENTS, CLInfoReader.cl_uint(), CLVersion.CL20);

    public static final DeviceInfo<Long> DEVICE_GLOBAL_VARIABLE_PREFERRED_TOTAL_SIZE = new DeviceInfo<>("DEVICE_GLOBAL_VARIABLE_PREFERRED_TOTAL_SIZE", CL_DEVICE_GLOBAL_VARIABLE_PREFERRED_TOTAL_SIZE, CLDeviceInfoReader.size_t(), CLVersion.CL20);

    public static final DeviceInfo<Integer> DEVICE_MAX_PIPE_ARGS = new DeviceInfo<>("DEVICE_MAX_PIPE_ARGS", CL_DEVICE_MAX_PIPE_ARGS, CLInfoReader.cl_uint(), CLVersion.CL20);

    public static final DeviceInfo<Integer> DEVICE_PIPE_MAX_ACTIVE_RESERVATIONS = new DeviceInfo<>("DEVICE_PIPE_MAX_ACTIVE_RESERVATIONS", CL_DEVICE_PIPE_MAX_ACTIVE_RESERVATIONS, CLInfoReader.cl_uint(), CLVersion.CL20);

    public static final DeviceInfo<Integer> DEVICE_PIPE_MAX_PACKET_SIZE = new DeviceInfo<>("DEVICE_PIPE_MAX_PACKET_SIZE", CL_DEVICE_PIPE_MAX_PACKET_SIZE, CLInfoReader.cl_uint(), CLVersion.CL20);

    public static final DeviceInfo<Integer> DEVICE_PREFERRED_PLATFORM_ATOMIC_ALIGNMENT = new DeviceInfo<>("DEVICE_PREFERRED_PLATFORM_ATOMIC_ALIGNMENT", CL_DEVICE_PREFERRED_PLATFORM_ATOMIC_ALIGNMENT, CLInfoReader.cl_uint(), CLVersion.CL20);

    public static final DeviceInfo<Integer> DEVICE_PREFERRED_GLOBAL_ATOMIC_ALIGNMENT = new DeviceInfo<>("DEVICE_PREFERRED_GLOBAL_ATOMIC_ALIGNMENT", CL_DEVICE_PREFERRED_GLOBAL_ATOMIC_ALIGNMENT, CLInfoReader.cl_uint(), CLVersion.CL20);

    public static final DeviceInfo<Integer> DEVICE_PREFERRED_LOCAL_ATOMIC_ALIGNMENT = new DeviceInfo<>("DEVICE_PREFERRED_LOCAL_ATOMIC_ALIGNMENT", CL_DEVICE_PREFERRED_LOCAL_ATOMIC_ALIGNMENT, CLInfoReader.cl_uint(), CLVersion.CL20);

    public static final DeviceInfo<String> DEVICE_IL_VERSION = new DeviceInfo<>("DEVICE_IL_VERSION", CL_DEVICE_IL_VERSION, CLInfoReader.cl_string(), CLVersion.CL21);

    public static final DeviceInfo<Integer> DEVICE_MAX_NUM_SUB_GROUPS = new DeviceInfo<>("DEVICE_MAX_NUM_SUB_GROUPS", CL_DEVICE_MAX_NUM_SUB_GROUPS, CLInfoReader.cl_uint(), CLVersion.CL21);

    public static final DeviceInfo<Boolean> DEVICE_SUB_GROUP_INDEPENDENT_FORWARD_PROGRESS = new DeviceInfo<>("DEVICE_SUB_GROUP_INDEPENDENT_FORWARD_PROGRESS", CL_DEVICE_SUB_GROUP_INDEPENDENT_FORWARD_PROGRESS, CLInfoReader.cl_bool(), CLVersion.CL21);

    public static final DeviceInfo<CLBitfield<DeviceFPConfig>> DEVICE_SINGLE_FP_CONFIG = new DeviceInfo<>("DEVICE_SINGLE_FP_CONFIG", CL_DEVICE_SINGLE_FP_CONFIG, CLInfoReader.cl_bitfield(DeviceFPConfig.class));

    public static final DeviceInfo<CLBitfield<DeviceFPConfig>> DEVICE_DOUBLE_FP_CONFIG = new DeviceInfo<>("DEVICE_DOUBLE_FP_CONFIG", CL_DEVICE_DOUBLE_FP_CONFIG, CLInfoReader.cl_bitfield(DeviceFPConfig.class), CLVersion.CL12);

    public static final DeviceInfo<GlobalMemCacheType> DEVICE_GLOBAL_MEM_CACHE_TYPE = new DeviceInfo<>("DEVICE_GLOBAL_MEM_CACHE_TYPE", CL_DEVICE_GLOBAL_MEM_CACHE_TYPE, CLInfoReader.cl_enum(GlobalMemCacheType.class));

    public static final DeviceInfo<LocalMemType> DEVICE_LOCAL_MEM_TYPE = new DeviceInfo<>("DEVICE_LOCAL_MEM_TYPE", CL_DEVICE_LOCAL_MEM_TYPE, CLInfoReader.cl_enum(LocalMemType.class));

    public static final DeviceInfo<CLBitfield<DeviceAffinityDomain>> DEVICE_PARTITION_AFFINITY_DOMAIN = new DeviceInfo<>("DEVICE_PARTITION_AFFINITY_DOMAIN", CL_DEVICE_PARTITION_AFFINITY_DOMAIN, CLInfoReader.cl_bitfield(DeviceAffinityDomain.class), CLVersion.CL12);

    public static final DeviceInfo<CLBitfield<DeviceSVMCapability>> DEVICE_SVM_CAPABILITIES = new DeviceInfo<>("DEVICE_SVM_CAPABILITIES", CL_DEVICE_SVM_CAPABILITIES, CLInfoReader.cl_bitfield(DeviceSVMCapability.class), CLVersion.CL20);

    // unhandled

    // usigned long long
    public static final DeviceInfo<ByteBuffer> DEVICE_MAX_CONSTANT_BUFFER_SIZE = new DeviceInfo<>("DEVICE_MAX_CONSTANT_BUFFER_SIZE", CL_DEVICE_MAX_CONSTANT_BUFFER_SIZE, (device, buffer) -> buffer);

    public static final DeviceInfo<ByteBuffer> DEVICE_PARTITION_TYPE = new DeviceInfo<>("DEVICE_PARTITION_TYPE", CL_DEVICE_PARTITION_TYPE, (device, buffer) -> buffer, CLVersion.CL12);

    public static final DeviceInfo<ByteBuffer> DEVICE_PARTITION_PROPERTIES = new DeviceInfo<>("DEVICE_PARTITION_PROPERTIES", CL_DEVICE_PARTITION_PROPERTIES, (device, buffer) -> buffer, CLVersion.CL12);

    public static List<DeviceInfo<?>> DEVICE_INFOS = Collections.unmodifiableList(Arrays.asList(
            DEVICE_TYPE, DEVICE_VENDOR_ID,
            DEVICE_MAX_COMPUTE_UNITS,
            DEVICE_MAX_WORK_ITEM_DIMENSIONS,
            DEVICE_MAX_WORK_GROUP_SIZE,
            DEVICE_MAX_WORK_ITEM_SIZES,
            DEVICE_PREFERRED_VECTOR_WIDTH_CHAR,
            DEVICE_PREFERRED_VECTOR_WIDTH_SHORT,
            DEVICE_PREFERRED_VECTOR_WIDTH_INT,
            DEVICE_PREFERRED_VECTOR_WIDTH_LONG,
            DEVICE_PREFERRED_VECTOR_WIDTH_FLOAT,
            DEVICE_PREFERRED_VECTOR_WIDTH_DOUBLE,
            DEVICE_MAX_CLOCK_FREQUENCY,
            DEVICE_ADDRESS_BITS,
            DEVICE_MAX_READ_IMAGE_ARGS,
            DEVICE_MAX_WRITE_IMAGE_ARGS,
            DEVICE_MAX_MEM_ALLOC_SIZE,
            DEVICE_IMAGE2D_MAX_WIDTH,
            DEVICE_IMAGE2D_MAX_HEIGHT,
            DEVICE_IMAGE3D_MAX_WIDTH,
            DEVICE_IMAGE3D_MAX_HEIGHT,
            DEVICE_IMAGE3D_MAX_DEPTH,
            DEVICE_IMAGE_SUPPORT,
            DEVICE_MAX_PARAMETER_SIZE,
            DEVICE_MAX_SAMPLERS,
            DEVICE_MEM_BASE_ADDR_ALIGN,
            DEVICE_GLOBAL_MEM_CACHELINE_SIZE,
            DEVICE_GLOBAL_MEM_CACHE_SIZE,
            DEVICE_GLOBAL_MEM_SIZE,
            DEVICE_MAX_CONSTANT_ARGS,
            DEVICE_LOCAL_MEM_SIZE,
            DEVICE_ERROR_CORRECTION_SUPPORT,
            DEVICE_PROFILING_TIMER_RESOLUTION,
            DEVICE_ENDIAN_LITTLE,
            DEVICE_AVAILABLE,
            DEVICE_COMPILER_AVAILABLE,
            DEVICE_EXECUTION_CAPABILITIES,
            DEVICE_QUEUE_PROPERTIES,
            DEVICE_NAME,
            DEVICE_VENDOR,
            DRIVER_VERSION,
            DEVICE_OPENCL_C_VERSION,
            DEVICE_PROFILE,
            DEVICE_VERSION,
            DEVICE_EXTENSIONS,
            DEVICE_LINKER_AVAILABLE,
            DEVICE_BUILT_IN_KERNELS,
            DEVICE_HOST_UNIFIED_MEMORY,
            DEVICE_PLATFORM,
            DEVICE_PREFERRED_VECTOR_WIDTH_HALF,
            DEVICE_NATIVE_VECTOR_WIDTH_CHAR,
            DEVICE_NATIVE_VECTOR_WIDTH_SHORT,
            DEVICE_NATIVE_VECTOR_WIDTH_INT,
            DEVICE_NATIVE_VECTOR_WIDTH_LONG,
            DEVICE_NATIVE_VECTOR_WIDTH_FLOAT,
            DEVICE_NATIVE_VECTOR_WIDTH_DOUBLE,
            DEVICE_NATIVE_VECTOR_WIDTH_HALF,
            DEVICE_IMAGE_MAX_BUFFER_SIZE,
            DEVICE_IMAGE_MAX_ARRAY_SIZE,
            DEVICE_PARENT_DEVICE,
            DEVICE_PARTITION_MAX_SUB_DEVICES,
            DEVICE_REFERENCE_COUNT,
            DEVICE_PREFERRED_INTEROP_USER_SYNC,
            DEVICE_PRINTF_BUFFER_SIZE,
            DEVICE_QUEUE_ON_HOST_PROPERTIES,
            DEVICE_MAX_READ_WRITE_IMAGE_ARGS,
            DEVICE_MAX_GLOBAL_VARIABLE_SIZE,
            DEVICE_QUEUE_ON_DEVICE_PROPERTIES,
            DEVICE_QUEUE_ON_DEVICE_PREFERRED_SIZE,
            DEVICE_QUEUE_ON_DEVICE_MAX_SIZE,
            DEVICE_MAX_ON_DEVICE_QUEUES,
            DEVICE_MAX_ON_DEVICE_EVENTS,
            DEVICE_GLOBAL_VARIABLE_PREFERRED_TOTAL_SIZE,
            DEVICE_MAX_PIPE_ARGS,
            DEVICE_PIPE_MAX_ACTIVE_RESERVATIONS,
            DEVICE_PIPE_MAX_PACKET_SIZE,
            DEVICE_PREFERRED_PLATFORM_ATOMIC_ALIGNMENT,
            DEVICE_PREFERRED_GLOBAL_ATOMIC_ALIGNMENT,
            DEVICE_PREFERRED_LOCAL_ATOMIC_ALIGNMENT,
            DEVICE_IL_VERSION,
            DEVICE_MAX_NUM_SUB_GROUPS,
            DEVICE_SUB_GROUP_INDEPENDENT_FORWARD_PROGRESS,
            DEVICE_SINGLE_FP_CONFIG,
            DEVICE_DOUBLE_FP_CONFIG,
            DEVICE_GLOBAL_MEM_CACHE_TYPE,
            DEVICE_LOCAL_MEM_TYPE,
            DEVICE_PARTITION_AFFINITY_DOMAIN,
            DEVICE_SVM_CAPABILITIES, DEVICE_MAX_CONSTANT_BUFFER_SIZE,
            DEVICE_PARTITION_TYPE,
            DEVICE_PARTITION_PROPERTIES
    ));

    private final String name;
    private final int id;
    private final CLVersion version;
    private final CLDeviceInfoReader<T> reader;

    private DeviceInfo(String name, int id, CLDeviceInfoReader<T> reader) {
        this(name, id, reader, CLVersion.CL10);
    }

    private DeviceInfo(String name, int id, CLDeviceInfoReader<T> reader, CLVersion version) {
        this.name = name;
        this.id = id;
        this.version = version;
        this.reader = reader;
    }

    public String name() {
        return name;
    }

    @Override
    public T read(CLDevice device, ByteBuffer buffer) {
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

    @Override
    public String toString() {
        return name;
    }
}
