package org.yah.tools.opencl.codegen.generator.model;

public enum KernelMethodType {
    WRITE_BUFFER,
    WRITE_BUFFER_ASYNC,

    READ_BUFFER,
    READ_BUFFER_ASYNC,

    SET_VALUE,

    GET_BUFFER,
    SET_BUFFER,

    ALLOCATE_BUFFER,
    ALLOCATE_BUFFER_ELEMENT,

    INVOKE,
    INVOKE_ASYNC,

    // AutoCloseable close override
    CLOSE;

    public boolean isAsync() {
        switch (this) {
            case WRITE_BUFFER_ASYNC:
            case READ_BUFFER_ASYNC:
            case INVOKE_ASYNC:
                return true;
            default:
                return false;
        }
    }
}
