package org.yah.tools.opencl;

import static org.lwjgl.opencl.CL10.CL_BUILD_PROGRAM_FAILURE;
import static org.lwjgl.opencl.CL10.CL_COMPILER_NOT_AVAILABLE;
import static org.lwjgl.opencl.CL10.CL_DEVICE_NOT_AVAILABLE;
import static org.lwjgl.opencl.CL10.CL_DEVICE_NOT_FOUND;
import static org.lwjgl.opencl.CL10.CL_IMAGE_FORMAT_MISMATCH;
import static org.lwjgl.opencl.CL10.CL_IMAGE_FORMAT_NOT_SUPPORTED;
import static org.lwjgl.opencl.CL10.CL_INVALID_ARG_INDEX;
import static org.lwjgl.opencl.CL10.CL_INVALID_ARG_SIZE;
import static org.lwjgl.opencl.CL10.CL_INVALID_ARG_VALUE;
import static org.lwjgl.opencl.CL10.CL_INVALID_BINARY;
import static org.lwjgl.opencl.CL10.CL_INVALID_BUFFER_SIZE;
import static org.lwjgl.opencl.CL10.CL_INVALID_BUILD_OPTIONS;
import static org.lwjgl.opencl.CL10.CL_INVALID_COMMAND_QUEUE;
import static org.lwjgl.opencl.CL10.CL_INVALID_CONTEXT;
import static org.lwjgl.opencl.CL10.CL_INVALID_DEVICE;
import static org.lwjgl.opencl.CL10.CL_INVALID_DEVICE_TYPE;
import static org.lwjgl.opencl.CL10.CL_INVALID_EVENT;
import static org.lwjgl.opencl.CL10.CL_INVALID_EVENT_WAIT_LIST;
import static org.lwjgl.opencl.CL10.CL_INVALID_GLOBAL_OFFSET;
import static org.lwjgl.opencl.CL10.CL_INVALID_GLOBAL_WORK_SIZE;
import static org.lwjgl.opencl.CL10.CL_INVALID_HOST_PTR;
import static org.lwjgl.opencl.CL10.CL_INVALID_IMAGE_FORMAT_DESCRIPTOR;
import static org.lwjgl.opencl.CL10.CL_INVALID_IMAGE_SIZE;
import static org.lwjgl.opencl.CL10.CL_INVALID_KERNEL;
import static org.lwjgl.opencl.CL10.CL_INVALID_KERNEL_ARGS;
import static org.lwjgl.opencl.CL10.CL_INVALID_KERNEL_DEFINITION;
import static org.lwjgl.opencl.CL10.CL_INVALID_KERNEL_NAME;
import static org.lwjgl.opencl.CL10.CL_INVALID_MEM_OBJECT;
import static org.lwjgl.opencl.CL10.CL_INVALID_OPERATION;
import static org.lwjgl.opencl.CL10.CL_INVALID_PLATFORM;
import static org.lwjgl.opencl.CL10.CL_INVALID_PROGRAM;
import static org.lwjgl.opencl.CL10.CL_INVALID_PROGRAM_EXECUTABLE;
import static org.lwjgl.opencl.CL10.CL_INVALID_QUEUE_PROPERTIES;
import static org.lwjgl.opencl.CL10.CL_INVALID_SAMPLER;
import static org.lwjgl.opencl.CL10.CL_INVALID_VALUE;
import static org.lwjgl.opencl.CL10.CL_INVALID_WORK_DIMENSION;
import static org.lwjgl.opencl.CL10.CL_INVALID_WORK_GROUP_SIZE;
import static org.lwjgl.opencl.CL10.CL_INVALID_WORK_ITEM_SIZE;
import static org.lwjgl.opencl.CL10.CL_MAP_FAILURE;
import static org.lwjgl.opencl.CL10.CL_MEM_COPY_OVERLAP;
import static org.lwjgl.opencl.CL10.CL_MEM_OBJECT_ALLOCATION_FAILURE;
import static org.lwjgl.opencl.CL10.CL_OUT_OF_HOST_MEMORY;
import static org.lwjgl.opencl.CL10.CL_OUT_OF_RESOURCES;
import static org.lwjgl.opencl.CL10.CL_PROFILING_INFO_NOT_AVAILABLE;
import static org.lwjgl.opencl.CL10.CL_SUCCESS;

import java.nio.IntBuffer;
import java.util.function.Consumer;
import java.util.function.Function;

import org.lwjgl.BufferUtils;

public class CLException extends RuntimeException {

    private static String messageFor(int code) {
        switch (code) {
        case CL_DEVICE_NOT_FOUND:
            return "CL_DEVICE_NOT_FOUND";
        case CL_DEVICE_NOT_AVAILABLE:
            return "CL_DEVICE_NOT_AVAILABLE";
        case CL_COMPILER_NOT_AVAILABLE:
            return "CL_COMPILER_NOT_AVAILABLE";
        case CL_MEM_OBJECT_ALLOCATION_FAILURE:
            return "CL_MEM_OBJECT_ALLOCATION_FAILURE";
        case CL_OUT_OF_RESOURCES:
            return "CL_OUT_OF_RESOURCES";
        case CL_OUT_OF_HOST_MEMORY:
            return "CL_OUT_OF_HOST_MEMORY";
        case CL_PROFILING_INFO_NOT_AVAILABLE:
            return "CL_PROFILING_INFO_NOT_AVAILABLE";
        case CL_MEM_COPY_OVERLAP:
            return "CL_MEM_COPY_OVERLAP";
        case CL_IMAGE_FORMAT_MISMATCH:
            return "CL_IMAGE_FORMAT_MISMATCH";
        case CL_IMAGE_FORMAT_NOT_SUPPORTED:
            return "CL_IMAGE_FORMAT_NOT_SUPPORTED";
        case CL_BUILD_PROGRAM_FAILURE:
            return "CL_BUILD_PROGRAM_FAILURE";
        case CL_MAP_FAILURE:
            return "CL_MAP_FAILURE";
        case CL_INVALID_VALUE:
            return "CL_INVALID_VALUE";
        case CL_INVALID_DEVICE_TYPE:
            return "CL_INVALID_DEVICE_TYPE";
        case CL_INVALID_PLATFORM:
            return "CL_INVALID_PLATFORM";
        case CL_INVALID_DEVICE:
            return "CL_INVALID_DEVICE";
        case CL_INVALID_CONTEXT:
            return "CL_INVALID_CONTEXT";
        case CL_INVALID_QUEUE_PROPERTIES:
            return "CL_INVALID_QUEUE_PROPERTIES";
        case CL_INVALID_COMMAND_QUEUE:
            return "CL_INVALID_COMMAND_QUEUE";
        case CL_INVALID_HOST_PTR:
            return "CL_INVALID_HOST_PTR";
        case CL_INVALID_MEM_OBJECT:
            return "CL_INVALID_MEM_OBJECT";
        case CL_INVALID_IMAGE_FORMAT_DESCRIPTOR:
            return "CL_INVALID_IMAGE_FORMAT_DESCRIPTOR";
        case CL_INVALID_IMAGE_SIZE:
            return "CL_INVALID_IMAGE_SIZE";
        case CL_INVALID_SAMPLER:
            return "CL_INVALID_SAMPLER";
        case CL_INVALID_BINARY:
            return "CL_INVALID_BINARY";
        case CL_INVALID_BUILD_OPTIONS:
            return "CL_INVALID_BUILD_OPTIONS";
        case CL_INVALID_PROGRAM:
            return "CL_INVALID_PROGRAM";
        case CL_INVALID_PROGRAM_EXECUTABLE:
            return "CL_INVALID_PROGRAM_EXECUTABLE";
        case CL_INVALID_KERNEL_NAME:
            return "CL_INVALID_KERNEL_NAME";
        case CL_INVALID_KERNEL_DEFINITION:
            return "CL_INVALID_KERNEL_DEFINITION";
        case CL_INVALID_KERNEL:
            return "CL_INVALID_KERNEL";
        case CL_INVALID_ARG_INDEX:
            return "CL_INVALID_ARG_INDEX";
        case CL_INVALID_ARG_VALUE:
            return "CL_INVALID_ARG_VALUE";
        case CL_INVALID_ARG_SIZE:
            return "CL_INVALID_ARG_SIZE";
        case CL_INVALID_KERNEL_ARGS:
            return "CL_INVALID_KERNEL_ARGS";
        case CL_INVALID_WORK_DIMENSION:
            return "CL_INVALID_WORK_DIMENSION";
        case CL_INVALID_WORK_GROUP_SIZE:
            return "CL_INVALID_WORK_GROUP_SIZE";
        case CL_INVALID_WORK_ITEM_SIZE:
            return "CL_INVALID_WORK_ITEM_SIZE";
        case CL_INVALID_GLOBAL_OFFSET:
            return "CL_INVALID_GLOBAL_OFFSET";
        case CL_INVALID_EVENT_WAIT_LIST:
            return "CL_INVALID_EVENT_WAIT_LIST";
        case CL_INVALID_EVENT:
            return "CL_INVALID_EVENT";
        case CL_INVALID_OPERATION:
            return "CL_INVALID_OPERATION";
        case CL_INVALID_BUFFER_SIZE:
            return "CL_INVALID_BUFFER_SIZE";
        case CL_INVALID_GLOBAL_WORK_SIZE:
            return "CL_INVALID_GLOBAL_WORK_SIZE";
        default:
            return "Unknown code";
        }
    }

    private static final long serialVersionUID = 1L;

    private final int error;

    public CLException(int error) {
        super(String.format("OpenCL error %d: %s", error, messageFor(error)));
        this.error = error;
    }

    public int getError() { return error; }

//    public static final void check(IntBuffer buffer) {
//        check(buffer.get(0));
//    }

    public static final void check(int error) {
        if (error != CL_SUCCESS)
            throw new CLException(error);
    }

    public static final void run(Consumer<IntBuffer> action) {
        IntBuffer buffer = errorBuffers.get();
        buffer.position(0);
        action.accept(buffer);
        check(buffer.get(0));
    }

    public static final <T> T apply(Function<IntBuffer,T> action) {
        IntBuffer buffer = errorBuffers.get();
        buffer.position(0);
        T res = action.apply(buffer);
        check(buffer.get(0));
        return res;
    }

    private static final ThreadLocal<IntBuffer> errorBuffers = new ThreadLocal<IntBuffer>() {
        @Override
        protected IntBuffer initialValue() {
            return BufferUtils.createIntBuffer(1);
        }
    };
}
