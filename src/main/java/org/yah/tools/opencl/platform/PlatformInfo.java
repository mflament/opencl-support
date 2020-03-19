package org.yah.tools.opencl.platform;

import static org.lwjgl.opencl.CL22.*;

import org.yah.tools.opencl.CLEnum;
import org.yah.tools.opencl.CLVersion;

public enum PlatformInfo implements CLEnum {

    PLATFORM_NAME(CL_PLATFORM_NAME, CLVersion.CL10),
    PLATFORM_VENDOR(CL_PLATFORM_VENDOR, CLVersion.CL10),
    PLATFORM_VERSION(CL_PLATFORM_VERSION, CLVersion.CL10),
    PLATFORM_PROFILE(CL_PLATFORM_PROFILE, CLVersion.CL10),
    PLATFORM_EXTENSIONS(CL_PLATFORM_EXTENSIONS, CLVersion.CL10),
    PLATFORM_HOST_TIMER_RESOLUTION(CL_PLATFORM_HOST_TIMER_RESOLUTION, CLVersion.CL21);

    private final int id;
    private final CLVersion version;

    PlatformInfo(int id, CLVersion version) {
        this.id = id;
        this.version = version;
    }

    @Override
    public int id() {
        return id;
    }

    @Override
    public CLVersion version() {
        return version;
    }

}
