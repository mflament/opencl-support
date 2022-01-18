package org.yah.tools.opencl.enums;

import org.yah.tools.opencl.CLVersion;

public enum DeviceAddressBits implements CLEnum {
    $32(32),
    $64(64);

    private final int bits;

    DeviceAddressBits(int bits) {
        this.bits = bits;
    }

    public int bytes() {
        return this == $32 ? 4 : 8;
    }

    @Override
    public int id() {
        return bits;
    }

    @Override
    public CLVersion version() {
        return CLVersion.CL10;
    }

    @Override
    public String toString() {
        return Integer.toString(bits);
    }
}
