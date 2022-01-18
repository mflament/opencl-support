package org.yah.tools.opencl.enums;

import org.yah.tools.opencl.platform.CLDevice;

import java.nio.ByteBuffer;

@FunctionalInterface
public interface CLInfoReader extends CLDeviceInfoReader {

    Object read(ByteBuffer buffer);

    @Override
    default Object read(CLDevice device, ByteBuffer buffer) {
        return read(buffer);
    }

    static CLInfoReader cl_uint() {
        return ByteBuffer::getInt;
    }

    static CLInfoReader cl_long() {
        return ByteBuffer::getLong;
    }

    static CLInfoReader cl_bool() {
        return buffer -> buffer.getInt() != 0;
    }

    static CLInfoReader cl_string() {
        StringBuilder sb = new StringBuilder();
        return buffer -> {
            while (buffer.hasRemaining()) {
                byte b = buffer.get();
                if (b == 0)
                    break;
                sb.append((char) b);
            }
            return sb.toString();
        };
    }

    static <T extends Enum<T> & CLEnum> CLInfoReader cl_enum(Class<T> enumType) {
        T[] values = enumType.getEnumConstants();
        return buffer -> {
            int id = buffer.getInt();
            return CLEnum.get(id, values);
        };
    }

    static <T extends Enum<T> & CLEnum> CLInfoReader cl_bitfield(Class<T> enumType) {
        T[] values = enumType.getEnumConstants();
        return buffer -> {
            int id = buffer.getInt();
            return new CLEnumSet<>(id, values);
        };
    }

}
