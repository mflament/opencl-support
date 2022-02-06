package org.yah.tools.opencl;

import org.lwjgl.BufferUtils;

import java.nio.IntBuffer;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.lwjgl.opencl.CL10.CL_SUCCESS;

public class CLException extends RuntimeException {

    public static String messageFor(int code) {
        switch (code) {
            case 0:
                return "CL_SUCCESS";
            case -1:
                return "CL_DEVICE_NOT_FOUND";
            case -2:
                return "CL_DEVICE_NOT_AVAILABLE";
            case -3:
                return "CL_COMPILER_NOT";
            case -4:
                return "CL_MEM_OBJECT";
            case -5:
                return "CL_OUT_OF_RESOURCES";
            case -6:
                return "CL_OUT_OF_HOST_MEMORY";
            case -7:
                return "CL_PROFILING_INFO_NOT";
            case -8:
                return "CL_MEM_COPY_OVERLAP";
            case -9:
                return "CL_IMAGE_FORMAT";
            case -10:
                return "CL_IMAGE_FORMAT_NOT";
            case -11:
                return "CL_BUILD_PROGRAM";
            case -12:
                return "CL_MAP_FAILURE";
            case -13:
                return "CL_MISALIGNED_SUB";
            case -14:
                return "CL_EXEC_STATUS_ERROR_";
            case -15:
                return "CL_COMPILE_PROGRAM";
            case -16:
                return "CL_LINKER_NOT_AVAILABLE";
            case -17:
                return "CL_LINK_PROGRAM_FAILURE";
            case -18:
                return "CL_DEVICE_PARTITION";
            case -19:
                return "CL_KERNEL_ARG_INFO";
            case -30:
                return "CL_INVALID_VALUE";
            case -31:
                return "CL_INVALID_DEVICE_TYPE";
            case -32:
                return "CL_INVALID_PLATFORM";
            case -33:
                return "CL_INVALID_DEVICE";
            case -34:
                return "CL_INVALID_CONTEXT";
            case -35:
                return "CL_INVALID_QUEUE_PROPERTIES";
            case -36:
                return "CL_INVALID_COMMAND_QUEUE";
            case -37:
                return "CL_INVALID_HOST_PTR";
            case -38:
                return "CL_INVALID_MEM_OBJECT";
            case -39:
                return "CL_INVALID_IMAGE_FORMAT_DESCRIPTOR";
            case -40:
                return "CL_INVALID_IMAGE_SIZE";
            case -41:
                return "CL_INVALID_SAMPLER";
            case -42:
                return "CL_INVALID_BINARY";
            case -43:
                return "CL_INVALID_BUILD_OPTIONS";
            case -44:
                return "CL_INVALID_PROGRAM";
            case -45:
                return "CL_INVALID_PROGRAM_EXECUTABLE";
            case -46:
                return "CL_INVALID_KERNEL_NAME";
            case -47:
                return "CL_INVALID_KERNEL_DEFINITION";
            case -48:
                return "CL_INVALID_KERNEL";
            case -49:
                return "CL_INVALID_ARG_INDEX";
            case -50:
                return "CL_INVALID_ARG_VALUE";
            case -51:
                return "CL_INVALID_ARG_SIZE";
            case -52:
                return "CL_INVALID_KERNEL_ARGS";
            case -53:
                return "CL_INVALID_WORK_DIMENSION";
            case -54:
                return "CL_INVALID_WORK_GROUP_SIZE";
            case -55:
                return "CL_INVALID_WORK_ITEM_SIZE";
            case -56:
                return "CL_INVALID_GLOBAL_OFFSET";
            case -57:
                return "CL_INVALID_EVENT_WAIT_LIST";
            case -58:
                return "CL_INVALID_EVENT";
            case -59:
                return "CL_INVALID_OPERATION";
            case -60:
                return "CL_INVALID_GL_OBJECT";
            case -61:
                return "CL_INVALID_BUFFER_SIZE";
            case -62:
                return "CL_INVALID_MIP_LEVEL";
            case -63:
                return "CL_INVALID_GLOBAL_WORK_SIZE";
            case -64:
                return "CL_INVALID_PROPERTY";
            case -65:
                return "CL_INVALID_IMAGE_DESCRIPTOR";
            case -66:
                return "CL_INVALID_COMPILER_OPTIONS";
            case -67:
                return "CL_INVALID_LINKER_OPTIONS";
            case -68:
                return "CL_INVALID_DEVICE_PARTITION_COUNT";
            case -69:
                return "CL_INVALID_PIPE_SIZE";
            case -70:
                return "CL_INVALID_DEVICE_QUEUE";
            case -1000:
                return "CL_INVALID_GL_SHAREGROUP_REFERENCE_KHR";
            case -1001:
                return "CL_PLATFORM_NOT_FOUND_KHR";
            case -1002:
                return "CL_INVALID_D3D10_DEVICE_KHR";
            case -1003:
                return "CL_INVALID_D3D10_RESOURCE_KHR";
            case -1004:
                return "CL_D3D10_RESOURCE_ALREADY_ACQUIRED_KHR";
            case -1005:
                return "CL_D3D10_RESOURCE_NOT_ACQUIRED_KHR";
            case -1006:
                return "CL_INVALID_D3D11_DEVICE_KHR";
            case -1007:
                return "CL_INVALID_D3D11_RESOURCE_KHR";
            case -1008:
                return "CL_D3D11_RESOURCE_ALREADY_ACQUIRED_KHR";
            case -1009:
                return "CL_D3D11_RESOURCE_NOT_ACQUIRED_KHR";
            case -1010:
                return "CL_INVALID_D3D9_DEVICE_NV";
            case -1011:
                return "CL_INVALID_D3D9_RESOURCE_NV";
            case -1012:
                return "CL_D3D9_RESOURCE_ALREADY_ACQUIRED_NV";
            case -1013:
                return "CL_D3D9_RESOURCE_NOT_ACQUIRED_NV";
            case -1092:
                return "CL_EGL_RESOURCE_NOT_ACQUIRED_KHR";
            case -1093:
                return "CL_INVALID_EGL_OBJECT_KHR";
            case -1094:
                return "CL_INVALID_ACCELERATOR_INTEL";
            case -1095:
                return "CL_INVALID_ACCELERATOR_TYPE_INTEL";
            case -1096:
                return "CL_INVALID_ACCELERATOR_DESCRIPTOR_INTEL";
            case -1097:
                return "CL_ACCELERATOR_TYPE_NOT_SUPPORTED_INTEL";
            case -1098:
                return "CL_INVALID_VA_API_MEDIA_ADAPTER_INTEL";
            case -1099:
                return "CL_INVALID_VA_API_MEDIA_SURFACE_INTEL";
            case -1100:
                return "CL_VA_API_MEDIA_SURFACE_ALREADY_ACQUIRED_INTEL";
            case -1101:
                return "CL_VA_API_MEDIA_SURFACE_NOT_ACQUIRED_INTEL";
            case -9999: // https://stackoverflow.com/questions/29560921/opencl-error-9999-on-nvidia-k20m/47338497
                return "NVIDIA clEnqueueNDRangeKernel Illegal read or write to a buffer";
            default:
                return "Unknown code " + code;
        }
    }

    private static final long serialVersionUID = 1L;

    private final int error;

    public CLException(int error) {
        this(error, null);
    }

    public CLException(int error, String message) {
        super(String.format("OpenCL error %d: %s%s", error, messageFor(error), message != null ? ("\n" + message) : ""));
        this.error = error;
    }

    public int getError() {
        return error;
    }

    public static void check(int error) {
        if (error != CL_SUCCESS)
            throw new CLException(error);
    }

    public static void run(Consumer<IntBuffer> action) {
        IntBuffer buffer = errorBuffers.get();
        buffer.position(0);
        action.accept(buffer);
        check(buffer.get(0));
    }

    public static <T> T apply(Function<IntBuffer, T> action) {
        IntBuffer buffer = errorBuffers.get();
        buffer.position(0);
        T res = action.apply(buffer);
        check(buffer.get(0));
        return res;
    }

    private static final ThreadLocal<IntBuffer> errorBuffers = ThreadLocal.withInitial(() -> BufferUtils.createIntBuffer(1));
}
