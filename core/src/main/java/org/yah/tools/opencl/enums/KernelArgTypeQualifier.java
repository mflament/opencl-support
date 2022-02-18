package org.yah.tools.opencl.enums;

import org.yah.tools.opencl.CLVersion;

import static org.lwjgl.opencl.CL12.*;

/**
 * @author Yah
 */
public enum KernelArgTypeQualifier implements CLEnum {

    CONST(CL_KERNEL_ARG_TYPE_CONST),
    RESTRICT(CL_KERNEL_ARG_TYPE_RESTRICT),
    VOLATILE(CL_KERNEL_ARG_TYPE_VOLATILE),
    NONE(CL_KERNEL_ARG_TYPE_NONE);

    private final int id;

    KernelArgTypeQualifier(int id) {
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
