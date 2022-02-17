package org.yah.tools.opencl;

import org.lwjgl.BufferUtils;
import org.yah.tools.opencl.enums.DeviceAddressBits;
import org.yah.tools.opencl.platform.CLDevice;

import java.nio.ByteBuffer;
import java.nio.LongBuffer;

@FunctionalInterface
public interface CLDeviceInfoReader<T> {

    T read(CLDevice device, ByteBuffer buffer);

    static CLDeviceInfoReader<Long> size_t() {
        return (device, buffer) -> {
            if (device.getAddressBits() == DeviceAddressBits.$32)
                return Integer.toUnsignedLong(buffer.getInt());
            return buffer.getLong();
        };
    }

    static CLDeviceInfoReader<LongBuffer> size_t_array() {
        CLDeviceInfoReader<Long> sizeReader = CLDeviceInfoReader.size_t();
        return (device, buffer) -> {
            int count = buffer.remaining() / device.getAddressBits().bytes();
            LongBuffer ptrs = BufferUtils.createLongBuffer(count);
            for (int i = 0; i < count; i++)
                ptrs.put(i, sizeReader.read(device, buffer));
            return ptrs;
        };
    }

}
