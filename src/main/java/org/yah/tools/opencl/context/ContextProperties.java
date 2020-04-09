package org.yah.tools.opencl.context;

import static org.lwjgl.opencl.CL10.CL_CONTEXT_PLATFORM;
import static org.lwjgl.opencl.CL12.CL_CONTEXT_INTEROP_USER_SYNC;
import static org.lwjgl.opencl.KHRGLSharing.CL_CGL_SHAREGROUP_KHR;
import static org.lwjgl.opencl.KHRGLSharing.CL_EGL_DISPLAY_KHR;
import static org.lwjgl.opencl.KHRGLSharing.CL_GLX_DISPLAY_KHR;
import static org.lwjgl.opencl.KHRGLSharing.CL_GL_CONTEXT_KHR;
import static org.lwjgl.opencl.KHRGLSharing.CL_WGL_HDC_KHR;

import org.lwjgl.opencl.CLCapabilities;
import org.yah.tools.opencl.CLEnum;
import org.yah.tools.opencl.CLVersion;

/**
 * @author Yah
 *
 */
public enum ContextProperties implements CLEnum {

    CONTEXT_PLATFORM(CL_CONTEXT_PLATFORM, CLVersion.CL10),
    CONTEXT_INTEROP_USER_SYNC(CL_CONTEXT_INTEROP_USER_SYNC, CLVersion.CL12),

    GL_CONTEXT_KHR(CL_GL_CONTEXT_KHR, CLVersion.CL10),
    EGL_DISPLAY_KHR(CL_EGL_DISPLAY_KHR, CLVersion.CL10),
    GLX_DISPLAY_KHR(CL_GLX_DISPLAY_KHR, CLVersion.CL10),
    WGL_HDC_KHR(CL_WGL_HDC_KHR, CLVersion.CL10),
    CGL_SHAREGROUP_KHR(CL_CGL_SHAREGROUP_KHR, CLVersion.CL10);

    private final int id;
    private final CLVersion version;

    ContextProperties(int id, CLVersion version) {
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

    @Override
    public boolean available(CLCapabilities capabilities) {
        if (CLEnum.super.available(capabilities)) {
            switch (this) {
            case CONTEXT_PLATFORM:
            case CONTEXT_INTEROP_USER_SYNC:
                return true;
            default:
                return capabilities.cl_khr_gl_sharing;
            }
        }
        return false;
    }
}
