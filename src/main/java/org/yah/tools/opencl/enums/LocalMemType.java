package org.yah.tools.opencl.enums;

import org.yah.tools.opencl.CLVersion;

import static org.lwjgl.opencl.CL10.CL_GLOBAL;
import static org.lwjgl.opencl.CL10.CL_LOCAL;

/**
 * @author Yah
 *
 */
public enum LocalMemType implements CLEnum {

    LOCAL(CL_LOCAL),
    GLOBAL(CL_GLOBAL);

    private final int id;

    LocalMemType(int id) {
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
