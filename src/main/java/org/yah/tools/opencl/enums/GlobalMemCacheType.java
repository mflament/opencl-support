package org.yah.tools.opencl.enums;

import org.yah.tools.opencl.CLVersion;

import static org.lwjgl.opencl.CL10.*;

/**
 * @author Yah
 */
public enum GlobalMemCacheType implements CLEnum {

    NONE(CL_NONE),
    READ_ONLY_CACHE(CL_READ_ONLY_CACHE),
    READ_WRITE_CACHE(CL_READ_WRITE_CACHE);

    private final int id;

    GlobalMemCacheType(int id) {
        this.id = id;
    }

    @Override
    public int id() {
        return id;
    }

    @Override
    public CLVersion version() {
        return CLVersion.CL10;
    }

}
