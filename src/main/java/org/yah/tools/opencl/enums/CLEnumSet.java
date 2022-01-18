package org.yah.tools.opencl.enums;

import java.util.*;
import java.util.stream.Collectors;

public class CLEnumSet<T extends CLEnum> {
    private final int value;
    private final Set<T> values;

    public CLEnumSet(int value, T[] enums) {
        this.value = value;
        this.values = Arrays.stream(enums)
                .filter(e -> (value & e.id()) != 0)
                .collect(Collectors.toUnmodifiableSet());
    }

    public int getValue() {
        return value;
    }

    public Set<T> values() {
       return values;
    }

    @Override
    public String toString() {
        return values.toString();
    }
}
