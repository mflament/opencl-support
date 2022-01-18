package org.yah.tools.opencl.enums;

import org.yah.tools.opencl.enums.CLEnum;
import org.yah.tools.opencl.CLVersion;
import static org.lwjgl.opencl.CL10.CL_EXEC_KERNEL;
import static org.lwjgl.opencl.CL10.CL_EXEC_NATIVE_KERNEL;

public enum DeviceExecutionCapability implements CLEnum  {
    EXEC_KERNEL(CL_EXEC_KERNEL),
    EXEC_NATIVE_KERNEL(CL_EXEC_NATIVE_KERNEL);

    private final int id;

    DeviceExecutionCapability(int id) {
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
