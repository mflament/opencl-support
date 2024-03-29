package org.yah.tools.opencl.enums;

import org.yah.tools.opencl.CLVersion;

import static org.lwjgl.opencl.CL10.*;
import static org.lwjgl.opencl.CL12.CL_DEVICE_TYPE_CUSTOM;

public enum DeviceType implements CLEnum {
    DEVICE_TYPE_DEFAULT(CL_DEVICE_TYPE_DEFAULT, CLVersion.CL10),
    DEVICE_TYPE_CPU(CL_DEVICE_TYPE_CPU, CLVersion.CL10),
    DEVICE_TYPE_GPU(CL_DEVICE_TYPE_GPU, CLVersion.CL10),
    DEVICE_TYPE_ACCELERATOR(CL_DEVICE_TYPE_ACCELERATOR, CLVersion.CL10),
    DEVICE_TYPE_ALL(CL_DEVICE_TYPE_ALL, CLVersion.CL10),
    DEVICE_TYPE_CUSTOM(CL_DEVICE_TYPE_CUSTOM, CLVersion.CL12);

    private final int id;
    private final CLVersion version;

    DeviceType(int id, CLVersion version) {
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
