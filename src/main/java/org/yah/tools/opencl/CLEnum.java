/**
 * 
 */
package org.yah.tools.opencl;

import java.util.EnumSet;
import java.util.Set;

import org.lwjgl.opencl.CLCapabilities;

/**
 * @author Yah
 *
 */
public interface CLEnum {

    int id();

    CLVersion version();
    
    default boolean available(CLCapabilities capabilities) {
        return version().available(capabilities);
    }

    static <E extends Enum<E>> Set<E> getValues(Class<E> enumType, CLVersion version) {
        if (!CLEnum.class.isAssignableFrom(enumType))
            throw new IllegalArgumentException("Enum type " + enumType.getName()
                    + " is not an instance of " + CLEnum.class.getName());
        EnumSet<E> res = EnumSet.noneOf(enumType);
        E[] values = enumType.getEnumConstants();
        for (int i = 0; i < values.length; i++) {
            if (((CLEnum) values[i]).version() == version)
                res.add(values[i]);
        }
        return res;
    }

}
