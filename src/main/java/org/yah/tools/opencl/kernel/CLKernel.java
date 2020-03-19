package org.yah.tools.opencl.kernel;

import java.nio.ByteBuffer;

import org.yah.tools.opencl.CLObject;
import org.yah.tools.opencl.mem.CLBuffer;

public interface CLKernel extends CLObject {

    void setArg(int index, ByteBuffer buffer);

    void setArg(int index, CLBuffer buffer);

    void setArg(int index, int value);

    void setArg(int index, float value);

    void setArg(int index, double value);

    void setArg(int index, short value);

    void setArg(int index, long value);

}