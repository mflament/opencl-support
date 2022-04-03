package org.yah.tools.opencl.enums;

import org.yah.tools.opencl.CLVersion;

import java.util.EnumSet;
import java.util.Set;

import static org.lwjgl.opencl.CL10.*;
import static org.lwjgl.opencl.CL12.CL_FP_CORRECTLY_ROUNDED_DIVIDE_SQRT;
import static org.lwjgl.opencl.CL12.CL_FP_SOFT_FLOAT;

public enum DeviceFPConfig implements CLEnum {
    FP_DENORM(CL_FP_DENORM),
    FP_INF_NAN(CL_FP_INF_NAN),
    FP_ROUND_TO_NEAREST(CL_FP_ROUND_TO_NEAREST),
    FP_ROUND_TO_ZERO(CL_FP_ROUND_TO_ZERO),
    FP_ROUND_TO_INF(CL_FP_ROUND_TO_INF),
    FP_FMA(CL_FP_FMA),
    FP_CORRECTLY_ROUNDED_DIVIDE_SQRT(CL_FP_CORRECTLY_ROUNDED_DIVIDE_SQRT),
    FP_SOFT_FLOAT(CL_FP_SOFT_FLOAT);

    private final int id;

    DeviceFPConfig(int id) {
        this.id = id;
    }

    @Override
    public int id() {
        return id;
    }

    @Override
    public CLVersion version() {
        return CLVersion.CL10;
    }

    public static long combine(DeviceFPConfig... props) {
        long res = 0;
        for (DeviceFPConfig p : props) {
            res |= p.id;
        }
        return res;
    }

    public static Set<DeviceFPConfig> setOf(DeviceFPConfig prop, DeviceFPConfig... props) {
        return EnumSet.of(prop, props);
    }
}
