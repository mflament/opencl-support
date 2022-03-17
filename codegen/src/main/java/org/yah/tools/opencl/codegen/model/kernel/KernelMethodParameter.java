package org.yah.tools.opencl.codegen.model.kernel;

import org.yah.tools.opencl.codegen.model.MethodParameterModel;
import org.yah.tools.opencl.codegen.model.kernel.param.*;

public interface KernelMethodParameter extends MethodParameterModel {

    @Override
    KernelMethod getMethod();

    default boolean isBuffer() {
        return false;
    }

    default Buffer asBuffer() {
        throw new UnsupportedOperationException();
    }

    default boolean isBufferSize() {
        return false;
    }

    default BufferSize asBufferSize() {
        throw new UnsupportedOperationException();
    }

    default boolean isBufferOffset() {
        return false;
    }

    default BufferOffset asBufferOffset() {
        throw new UnsupportedOperationException();
    }

    default boolean isBufferProperties() {
        return false;
    }

    default BufferProperties asBufferProperties() {
        throw new UnsupportedOperationException();
    }

    default boolean isValue() {
        return false;
    }

    default Value asValue() {
        throw new UnsupportedOperationException();
    }

    default boolean isEventBuffer() {
        return false;
    }

    default EventBuffer asEventBuffer() {
        throw new UnsupportedOperationException();
    }

    default boolean isInvokeRangeParameter() {
        return false;
    }

    default InvokeRangeParameter asInvokeRangeParameter() {
        throw new UnsupportedOperationException();
    }

    default boolean isInvokeArgument() {
        return false;
    }

    default InvokeArgumentParameter asInvokeArgument() {
        throw new UnsupportedOperationException();
    }

}
