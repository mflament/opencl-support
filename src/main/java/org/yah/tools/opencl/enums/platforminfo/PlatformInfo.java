package org.yah.tools.opencl.enums.platforminfo;

import org.yah.tools.opencl.CLVersion;
import org.yah.tools.opencl.enums.CLEnum;
import org.yah.tools.opencl.enums.CLInfoReader;

import java.nio.ByteBuffer;

import static org.lwjgl.opencl.CL10.*;
import static org.lwjgl.opencl.CL10.CL_PLATFORM_EXTENSIONS;
import static org.lwjgl.opencl.CL21.CL_PLATFORM_HOST_TIMER_RESOLUTION;

public enum PlatformInfo implements CLEnum, CLInfoReader {

    PLATFORM_NAME(CL_PLATFORM_NAME),
    PLATFORM_VENDOR(CL_PLATFORM_VENDOR),
    PLATFORM_VERSION(CL_PLATFORM_VERSION),
    PLATFORM_PROFILE(CL_PLATFORM_PROFILE),
    PLATFORM_EXTENSIONS(CL_PLATFORM_EXTENSIONS),
    // ulong
    PLATFORM_HOST_TIMER_RESOLUTION(CL_PLATFORM_HOST_TIMER_RESOLUTION, CLInfoReader.cl_long(), CLVersion.CL21);

    private final int id;
    private final CLVersion version;
    private final CLInfoReader reader;

    PlatformInfo(int id) {
        this(id, CLInfoReader.cl_string(), CLVersion.CL10);
    }

    PlatformInfo(int id, CLInfoReader reader, CLVersion version) {
        this.id = id;
        this.version = version;
        this.reader = reader;
    }

    @Override
    public int id() {
        return id;
    }

    @Override
    public CLVersion version() {
        return version;
    }

    @Override
    public Object read(ByteBuffer buffer) {
        return reader.read(buffer);
    }
}
