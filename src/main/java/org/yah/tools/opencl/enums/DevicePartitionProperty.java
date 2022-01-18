package org.yah.tools.opencl.enums;

import org.yah.tools.opencl.CLVersion;

import static org.lwjgl.opencl.CL12.*;

/**
 * @author Yah
 *
 */
public enum DevicePartitionProperty implements CLEnum {

    DEVICE_PARTITION_EQUALLY(CL_DEVICE_PARTITION_EQUALLY),
    DEVICE_PARTITION_BY_COUNTS(CL_DEVICE_PARTITION_BY_COUNTS),
    DEVICE_PARTITION_BY_AFFINITY_DOMAIN(CL_DEVICE_PARTITION_BY_AFFINITY_DOMAIN);

    private final int id;

    DevicePartitionProperty(int id) {
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
