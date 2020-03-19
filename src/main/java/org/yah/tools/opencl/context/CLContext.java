package org.yah.tools.opencl.context;

import org.lwjgl.PointerBuffer;
import org.yah.tools.opencl.CLObject;

public interface CLContext extends CLObject {

    long getPlatform();

    PointerBuffer getDevices();

    long getDevice();

}