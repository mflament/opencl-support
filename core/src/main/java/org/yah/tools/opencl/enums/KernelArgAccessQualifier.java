package org.yah.tools.opencl.enums;

import org.yah.tools.opencl.CLVersion;

import static org.lwjgl.opencl.CL12.*;

/**
 * @author Yah
 */
public enum KernelArgAccessQualifier implements CLEnum {

    READ_ONLY(CL_KERNEL_ARG_ACCESS_READ_ONLY),
    WRITE_ONLY(CL_KERNEL_ARG_ACCESS_WRITE_ONLY),
    READ_WRITE(CL_KERNEL_ARG_ACCESS_READ_WRITE),
    NONE(CL_KERNEL_ARG_ACCESS_NONE);

    private final int id;

    KernelArgAccessQualifier(int id) {
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
