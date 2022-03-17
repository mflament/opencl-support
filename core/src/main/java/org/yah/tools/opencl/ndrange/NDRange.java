package org.yah.tools.opencl.ndrange;

import org.lwjgl.PointerBuffer;
import org.yah.tools.opencl.platform.CLDevice;

import javax.annotation.Nullable;
import java.util.Collection;

public interface NDRange {

    int getDimensions();

    PointerBuffer getGlobalWorkSizes();

    @Nullable
    PointerBuffer getLocalWorkSizes();

    @Nullable
    PointerBuffer getGlobalWorkOffsets();

    void validate(CLDevice device);

    default void validate(Collection<CLDevice> devices) {
        devices.forEach(this::validate);
    }

    static NDRange1 range1() {
        return new NDRange1();
    }

    static NDRange2 range2() {
        return new NDRange2();
    }

    static NDRange3 range3() {
        return new NDRange3();
    }
}
