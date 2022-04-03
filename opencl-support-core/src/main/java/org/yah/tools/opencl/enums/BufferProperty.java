package org.yah.tools.opencl.enums;

import static org.lwjgl.opencl.CL10.CL_MEM_ALLOC_HOST_PTR;
import static org.lwjgl.opencl.CL10.CL_MEM_COPY_HOST_PTR;
import static org.lwjgl.opencl.CL10.CL_MEM_READ_ONLY;
import static org.lwjgl.opencl.CL10.CL_MEM_READ_WRITE;
import static org.lwjgl.opencl.CL10.CL_MEM_USE_HOST_PTR;
import static org.lwjgl.opencl.CL10.CL_MEM_WRITE_ONLY;
import static org.lwjgl.opencl.CL12.CL_MEM_HOST_NO_ACCESS;
import static org.lwjgl.opencl.CL12.CL_MEM_HOST_READ_ONLY;
import static org.lwjgl.opencl.CL12.CL_MEM_HOST_WRITE_ONLY;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;

import org.yah.tools.opencl.CLVersion;

public enum BufferProperty implements CLEnum {
    MEM_READ_WRITE(CL_MEM_READ_WRITE, CLVersion.CL10),
    MEM_WRITE_ONLY(CL_MEM_WRITE_ONLY, CLVersion.CL10),
    MEM_READ_ONLY(CL_MEM_READ_ONLY, CLVersion.CL10),

    MEM_USE_HOST_PTR(CL_MEM_USE_HOST_PTR, CLVersion.CL10),
    MEM_ALLOC_HOST_PTR(CL_MEM_ALLOC_HOST_PTR, CLVersion.CL10),
    MEM_COPY_HOST_PTR(CL_MEM_COPY_HOST_PTR, CLVersion.CL10),

    MEM_HOST_WRITE_ONLY(CL_MEM_HOST_WRITE_ONLY, CLVersion.CL12),
    MEM_HOST_READ_ONLY(CL_MEM_HOST_READ_ONLY, CLVersion.CL12),
    MEM_HOST_NO_ACCESS(CL_MEM_HOST_NO_ACCESS, CLVersion.CL12);

    private final int id;

    private final CLVersion version;

    BufferProperty(int id, CLVersion version) {
        this.id = id;
        this.version = version;
    }

    @Override
    public int id() {
        return id;
    }

    @Override
    public CLVersion version() {
        return version;
    }

    public static long combine(BufferProperty... props) {
        long res = 0;
        for (BufferProperty p : props) {
            res |= p.id;
        }
        return res;
    }

    public static long combine(Collection<BufferProperty> props) {
        long res = 0;
        for (BufferProperty p : props) {
            res |= p.id;
        }
        return res;
    }

    public static Set<BufferProperty> setOf(BufferProperty prop, BufferProperty... props) {
        return EnumSet.of(prop, props);
    }
}
