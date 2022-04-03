package org.yah.tools.opencl.enums;

import org.yah.tools.opencl.CLVersion;

import static org.lwjgl.opencl.CL12.*;

/**
 * @author Yah
 *
 */
public enum DeviceAffinityDomain implements CLEnum {

    DEVICE_AFFINITY_DOMAIN_NUMA(CL_DEVICE_AFFINITY_DOMAIN_NUMA),
    DEVICE_AFFINITY_DOMAIN_L4_CACHE(CL_DEVICE_AFFINITY_DOMAIN_L4_CACHE),
    DEVICE_AFFINITY_DOMAIN_L3_CACHE(CL_DEVICE_AFFINITY_DOMAIN_L3_CACHE),
    DEVICE_AFFINITY_DOMAIN_L2_CACHE(CL_DEVICE_AFFINITY_DOMAIN_L2_CACHE),
    DEVICE_AFFINITY_DOMAIN_L1_CACHE(CL_DEVICE_AFFINITY_DOMAIN_L1_CACHE),
    DEVICE_AFFINITY_DOMAIN_NEXT_PARTITIONABLE(CL_DEVICE_AFFINITY_DOMAIN_NEXT_PARTITIONABLE);

    private final int id;

    DeviceAffinityDomain(int id) {
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
