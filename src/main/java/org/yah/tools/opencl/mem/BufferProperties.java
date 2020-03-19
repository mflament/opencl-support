package org.yah.tools.opencl.mem;

import static org.lwjgl.opencl.CL10.*;
import static org.lwjgl.opencl.CL12.*;

import java.util.EnumSet;
import java.util.Set;

import org.yah.tools.opencl.CLEnum;
import org.yah.tools.opencl.CLVersion;

public enum BufferProperties implements CLEnum {
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

    BufferProperties(int id, CLVersion version) {
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

    public static long combine(Set<BufferProperties> props) {
        long res = 0;
        for (BufferProperties p : props) {
            res |= p.id;
        }
        return res;
    }

    public static Set<BufferProperties> setOf(BufferProperties prop, BufferProperties... props) {
        return EnumSet.of(prop, props);
    }
}
