package org.yah.tools.opencl.enums;

import java.util.*;
import java.util.stream.Collectors;

public class CLBitfield<T extends CLEnum> {
    private static final CLBitfield<CLEnum> EMPTY = new CLBitfield<>(0, Collections.emptySet());

    private final long value;
    private final Set<T> values;

    @SafeVarargs
    public static <T extends CLEnum> CLBitfield<T> of(T value, T... otherValues) {
        int v = value.id();
        Set<T> values = new HashSet<>();
        values.add(value);
        for (T otherValue : otherValues) {
            v |= otherValue.id();
            values.add(otherValue);
        }
        return new CLBitfield<>(v, values);
    }

    public static <T extends CLEnum> CLBitfield<T> from(long value, T[] enums) {
        Set<T> values = Collections.unmodifiableSet(Arrays.stream(enums)
                .filter(e -> (value & e.id()) != 0)
                .collect(Collectors.toSet()));
        return new CLBitfield<>(value, values);
    }

    private CLBitfield(long value, Set<T> values) {
        this.value = value;
        this.values = values;
    }

    @SuppressWarnings("unchecked")
    public static <T extends CLEnum> CLBitfield<T> empty() {
        return (CLBitfield<T>) EMPTY;
    }

    public long getValue() {
        return value;
    }

    public Set<T> values() {
        return values;
    }

    @Override
    public String toString() {
        return values.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CLBitfield<?> that = (CLBitfield<?>) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    public boolean contains(T v) {
        return values.contains(v);
    }
}
