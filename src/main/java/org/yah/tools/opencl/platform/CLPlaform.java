package org.yah.tools.opencl.platform;

import static org.lwjgl.opencl.CL10.clGetPlatformIDs;
import static org.lwjgl.opencl.CL10.clGetPlatformInfo;
import static org.yah.tools.opencl.CLException.check;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opencl.CL;
import org.lwjgl.opencl.CLCapabilities;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.yah.tools.opencl.CLInfoReader;

public class CLPlaform {

    private final long id;

    private final String name;

    private CLCapabilities capabilities;

    private CLPlaform(long id, String name) {
        super();
        this.id = id;
        this.name = name;
    }

    public long getId() { return id; }

    public String getName() { return name; }

    public CLCapabilities getCapabilities() {
        if (capabilities == null)
            capabilities = CL.createPlatformCapabilities(id);
        return capabilities;
    }

    public List<CLDevice> getDevices(DeviceType deviceType) {
        return CLDevice.platformDevices(id, deviceType);
    }

    public CLDevice getDefaultDevice() { return CLDevice.defaultDevice(id); }

    @Override
    public String toString() {
        return name;
    }

    public String getPlatformInfo(PlatformInfo info) {
        return readPlatformInfo(id, info);
    }

    public static List<CLPlaform> platforms() {
        IntBuffer countBuffer = BufferUtils.createIntBuffer(1);
        check(clGetPlatformIDs(null, countBuffer));
        int count = countBuffer.get(0);
        if (countBuffer.get(0) == 0)
            return Collections.emptyList();

        List<CLPlaform> plaforms = new ArrayList<>(count);
        PointerBuffer platformIds = BufferUtils.createPointerBuffer(count);
        check(clGetPlatformIDs(platformIds, countBuffer));
        for (int i = 0; i < countBuffer.get(0); i++) {
            plaforms.add(createPlatform(platformIds.get(i)));
        }
        return plaforms;
    }

    public static CLPlaform defaultPlatform() {
        IntBuffer countBuffer = BufferUtils.createIntBuffer(1);
        PointerBuffer platformIds = BufferUtils.createPointerBuffer(1);
        check(clGetPlatformIDs(platformIds, countBuffer));
        return countBuffer.get(0) > 0 ? createPlatform(platformIds.get(0)) : null;
    }

    public static CLPlaform createPlatform(long platformId) {
        return new CLPlaform(platformId, readPlatformInfo(platformId, PlatformInfo.PLATFORM_NAME));
    }

    public static String readPlatformInfo(long platformId, PlatformInfo info) {
        return readPlatformInfo(platformId, info,
                b -> MemoryUtil.memUTF8(MemoryUtil.memAddress(b)));
    }

    public static <T> T readPlatformInfo(long platformId, PlatformInfo info,
            CLInfoReader<T> reader) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            PointerBuffer sizeBuffer = stack.mallocPointer(1);
            check(clGetPlatformInfo(platformId, info.id(), (ByteBuffer) null, sizeBuffer));
            ByteBuffer valueBuffer = stack.malloc((int) sizeBuffer.get(0));
            check(clGetPlatformInfo(platformId, info.id(), valueBuffer, sizeBuffer));
            return reader.apply(valueBuffer);
        }
    }

}
