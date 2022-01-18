package org.yah.tools.opencl.enums;

import org.yah.tools.opencl.CLVersion;

import static org.lwjgl.opencl.CL20.*;

/**
 * @author Yah
 *
 */
public enum DeviceSVMCapability implements CLEnum {

    DEVICE_SVM_COARSE_GRAIN_BUFFER(CL_DEVICE_SVM_COARSE_GRAIN_BUFFER),
    DEVICE_SVM_FINE_GRAIN_BUFFER(CL_DEVICE_SVM_FINE_GRAIN_BUFFER),
    DEVICE_SVM_FINE_GRAIN_SYSTEM(CL_DEVICE_SVM_FINE_GRAIN_SYSTEM),
    DEVICE_SVM_ATOMICS(CL_DEVICE_SVM_ATOMICS);

    private final int id;

    DeviceSVMCapability(int id) {
        this.id = id;
    }

    @Override
    public int id() {
        return id;
    }

    @Override
    public CLVersion version() {
        return CLVersion.CL20;
    }

}
