package org.yah.tools.opencl;

import org.yah.tools.opencl.enums.CLEnum;
import org.yah.tools.opencl.enums.CLEnumSet;
import org.yah.tools.opencl.platform.CLDevice;

import java.nio.ByteBuffer;

@FunctionalInterface
public interface CLInfoReader<T> extends CLDeviceInfoReader<T> {

    T read(ByteBuffer buffer);

    @Override
    default T read(CLDevice device, ByteBuffer buffer) {
        return read(buffer);
    }

    static CLInfoReader<Integer> cl_uint() {
        return ByteBuffer::getInt;
    }

    static CLInfoReader<Long> cl_long() {
        return ByteBuffer::getLong;
    }

    static CLInfoReader<Boolean> cl_bool() {
        return buffer -> buffer.getInt() != 0;
    }

    static CLInfoReader<String> cl_string() {
        return CLUtils::readCLString;
    }

    static <T extends Enum<T> & CLEnum> CLInfoReader<T> cl_enum(Class<T> enumType) {
        T[] values = enumType.getEnumConstants();
        return buffer -> {
            int id = buffer.getInt();
            return CLEnum.get(id, values);
        };
    }

    static <T extends Enum<T> & CLEnum> CLInfoReader<CLEnumSet<T>> cl_bitfield(Class<T> enumType) {
        T[] values = enumType.getEnumConstants();
        return buffer -> {
            int id = buffer.getInt();
            return new CLEnumSet<>(id, values);
        };
    }

}
