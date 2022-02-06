package org.yah.tools.opencl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class CLUtils {

    private CLUtils() {}

    /**
     * http://graphics.stanford.edu/~seander/bithacks.html#RoundUpPowerOf2
     */
    public static int nextPowerOfTwo(int n) {
        n--;
        n |= n >> 1;
        n |= n >> 2;
        n |= n >> 4;
        n |= n >> 8;
        n |= n >> 16;
        n++;
        return n;
    }

    public static <T> List<T> copyOf(Collection<T> from) {
        return Collections.unmodifiableList(new ArrayList<>(from));
    }
}
