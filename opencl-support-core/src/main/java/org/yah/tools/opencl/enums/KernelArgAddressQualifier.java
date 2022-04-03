package org.yah.tools.opencl.enums;

import org.yah.tools.opencl.CLVersion;

import static org.lwjgl.opencl.CL12.*;

/**
 * @author Yah
 */
public enum KernelArgAddressQualifier implements CLEnum {

    GLOBAL(CL_KERNEL_ARG_ADDRESS_GLOBAL),
    LOCAL(CL_KERNEL_ARG_ADDRESS_LOCAL),
    CONSTANT(CL_KERNEL_ARG_ADDRESS_CONSTANT),
    PRIVATE(CL_KERNEL_ARG_ADDRESS_PRIVATE);

    private final int id;

    KernelArgAddressQualifier(int id) {
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
