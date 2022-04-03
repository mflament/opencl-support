package org.yah.tools.opencl.mem;

import org.lwjgl.PointerBuffer;
import org.yah.tools.opencl.CLObject;

public interface CLMemObject extends CLObject {
    PointerBuffer getPointer();
}
