package org.yah.tools.opencl;

import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;

import java.nio.ByteBuffer;

public class CLParameterReader {
    private final PointerBuffer sizeBuffer = BufferUtils.createPointerBuffer(1);
    private ByteBuffer dataBufer;

    public ByteBuffer readSizedParam(CLUtils.CLOperation<PointerBuffer> readSize, CLUtils.CLOperation<ByteBuffer> readParam) {
        CLException.check(readSize.accept(sizeBuffer));
        int requiredCapacity = (int) sizeBuffer.get(0);
        if (dataBufer == null || dataBufer.capacity() < requiredCapacity)
            dataBufer = BufferUtils.createByteBuffer(Math.max(requiredCapacity, 512));
        CLException.check(readParam.accept(dataBufer));
        return dataBufer;
    }

    public ByteBuffer readSizedParam(CLUtils.CLParamOperation operation) {
        return readSizedParam(sb -> operation.apply(sb, null), bb -> operation.apply(null, bb));
    }

    public String readSizedString(CLUtils.CLParamOperation operation) {
        return CLUtils.readCLString(readSizedParam(operation));
    }

    public String readSizedString(CLUtils.CLOperation<PointerBuffer> readSize, CLUtils.CLOperation<ByteBuffer> readParam) {
        return CLUtils.readCLString(readSizedParam(readSize, readParam));
    }

}
