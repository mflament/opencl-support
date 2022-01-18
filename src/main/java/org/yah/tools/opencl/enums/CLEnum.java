package org.yah.tools.opencl.enums;

import org.lwjgl.opencl.CLCapabilities;
import org.yah.tools.opencl.CLVersion;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

/**
 * @author Yah
 */
public interface CLEnum {

    int id();

    CLVersion version();

    /**
     * @noinspection BooleanMethodIsAlwaysInverted
     */
    default boolean available(CLCapabilities capabilities) {
        return version().available(capabilities);
    }

    static <T extends CLEnum> T get(int id, T[] enums) {
        return Arrays.stream(enums).filter(e -> e.id() == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown id " + id));
    }

    static <E extends Enum<E>> Set<E> getValues(Class<E> enumType, CLVersion version) {
        if (!CLEnum.class.isAssignableFrom(enumType))
            throw new IllegalArgumentException("Enum type " + enumType.getName()
                    + " is not an instance of " + CLEnum.class.getName());
        EnumSet<E> res = EnumSet.noneOf(enumType);
        E[] values = enumType.getEnumConstants();
        for (E value : values) {
            if (((CLEnum) value).version() == version)
                res.add(value);
        }
        return res;
    }

}
