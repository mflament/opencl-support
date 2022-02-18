package org.yah.tools.opencl.enums;

import org.yah.tools.opencl.CLVersion;

import static org.lwjgl.opencl.CL12.*;

/**
 * @author Yah
 */
public enum ProgramBinaryType implements CLEnum {

    NONE(CL_PROGRAM_BINARY_TYPE_NONE),
    COMPILED_OBJECT(CL_PROGRAM_BINARY_TYPE_COMPILED_OBJECT),
    LIBRARY(CL_PROGRAM_BINARY_TYPE_LIBRARY),
    EXECUTABLE(CL_PROGRAM_BINARY_TYPE_EXECUTABLE);

    private final int id;

    ProgramBinaryType(int id) {
        this.id = id;
    }

    @Override
    public int id() {
        return id;
    }

    @Override
    public CLVersion version() {
        return CLVersion.CL12;
    }

}
