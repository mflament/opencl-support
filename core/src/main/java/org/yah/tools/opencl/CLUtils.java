package org.yah.tools.opencl;

import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class CLUtils {

    private CLUtils() {
    }

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

    public static String readCLString(ByteBuffer buffer) {
        StringBuilder sb = new StringBuilder();
        while (buffer.hasRemaining()) {
            byte b = buffer.get();
            if (b == 0) break;
            sb.append((char) b);
        }
        return sb.toString();
    }

    public static String readSizedString(CLParamOperation operation) {
        return readCLString(readSizedParam(operation));
    }

    public static String readSizedString(CLOperation<PointerBuffer> readSize, CLOperation<ByteBuffer> readParam) {
        return readCLString(readSizedParam(readSize, readParam));
    }

    public static ByteBuffer readSizedParam(CLParamOperation operation) {
        return readSizedParam(sb -> operation.apply(sb, null), bb -> operation.apply(null, bb));
    }

    public static ByteBuffer readSizedParam(CLOperation<PointerBuffer> readSize, CLOperation<ByteBuffer> readParam) {
        PointerBuffer sizeBuffer = PointerBuffer.allocateDirect(1);
        CLException.check(readSize.accept(sizeBuffer));
        ByteBuffer byteBuffer = BufferUtils.createByteBuffer((int) sizeBuffer.get(0));
        CLException.check(readParam.accept(byteBuffer));
        return byteBuffer;
    }

    @FunctionalInterface
    public interface CLOperation<T> {
        int accept(T value);
    }

    @FunctionalInterface
    public interface CLParamOperation {
        int apply(PointerBuffer sizeBuffer, ByteBuffer byteBuffer);
    }

}
