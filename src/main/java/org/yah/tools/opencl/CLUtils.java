package org.yah.tools.opencl;

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

}
