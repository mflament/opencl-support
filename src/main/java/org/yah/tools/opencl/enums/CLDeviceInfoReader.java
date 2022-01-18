package org.yah.tools.opencl.enums;

import org.yah.tools.opencl.platform.CLDevice;

import java.nio.ByteBuffer;

@FunctionalInterface
public interface CLDeviceInfoReader {

    Object read(CLDevice device, ByteBuffer buffer);

    static CLDeviceInfoReader size_t() {
        return (device, buffer) -> {
            if (device.getAddressBits() == DeviceAddressBits.$32)
                return Integer.toUnsignedLong(buffer.getInt());
            return buffer.getLong();
        };
    }

}
