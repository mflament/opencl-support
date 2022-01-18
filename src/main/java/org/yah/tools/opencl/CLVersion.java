package org.yah.tools.opencl;

import org.lwjgl.opencl.CLCapabilities;

public enum CLVersion {
    CL10,
    CL11,
    CL12,
    CL20,
    CL21,
    CL22;

    public boolean available(CLCapabilities capabilities) {
        switch (this) {
        case CL10:
            return capabilities.OpenCL10;
        case CL11:
            return capabilities.OpenCL11;
        case CL12:
            return capabilities.OpenCL12;
        case CL20:
            return capabilities.OpenCL20;
        case CL21:
            return capabilities.OpenCL21;
        case CL22:
            return capabilities.OpenCL22;
        default:
            return false;
        }
    }
}
