package org.yah.tools.opencl.enums.platforminfo;

import org.yah.tools.opencl.CLVersion;
import org.yah.tools.opencl.enums.CLEnum;
import org.yah.tools.opencl.CLInfoReader;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.lwjgl.opencl.CL10.*;
import static org.lwjgl.opencl.CL21.CL_PLATFORM_HOST_TIMER_RESOLUTION;

public class PlatformInfo<T> implements CLEnum, CLInfoReader<T> {

    public static final PlatformInfo<String> PLATFORM_NAME = new PlatformInfo<>("PLATFORM_NAME", CL_PLATFORM_NAME, CLInfoReader.cl_string());
    public static final PlatformInfo<String> PLATFORM_VENDOR = new PlatformInfo<>("PLATFORM_VENDOR", CL_PLATFORM_VENDOR, CLInfoReader.cl_string());
    public static final PlatformInfo<String> PLATFORM_VERSION = new PlatformInfo<>("PLATFORM_VERSION", CL_PLATFORM_VERSION, CLInfoReader.cl_string());
    public static final PlatformInfo<String> PLATFORM_PROFILE = new PlatformInfo<>("PLATFORM_PROFILE", CL_PLATFORM_PROFILE, CLInfoReader.cl_string());
    public static final PlatformInfo<String> PLATFORM_EXTENSIONS = new PlatformInfo<>("PLATFORM_EXTENSIONS", CL_PLATFORM_EXTENSIONS, CLInfoReader.cl_string());
    // ulong
    public static final PlatformInfo<Long> PLATFORM_HOST_TIMER_RESOLUTION = new PlatformInfo<>("PLATFORM_HOST_TIMER_RESOLUTION", CL_PLATFORM_HOST_TIMER_RESOLUTION, CLInfoReader.cl_long(), CLVersion.CL21);


    public static List<PlatformInfo<?>> PLATFORM_INFOS = Collections.unmodifiableList(Arrays.asList(
            PLATFORM_NAME,
            PLATFORM_VENDOR,
            PLATFORM_VERSION,
            PLATFORM_PROFILE,
            PLATFORM_EXTENSIONS,
            PLATFORM_HOST_TIMER_RESOLUTION
    ));

    private final String name;
    private final int id;
    private final CLVersion version;
    private final CLInfoReader<T> reader;

    private PlatformInfo(String name, int id, CLInfoReader<T> reader) {
        this(name, id, reader, CLVersion.CL10);
    }

    private PlatformInfo(String name, int id, CLInfoReader<T> reader, CLVersion version) {
        this.name = name;
        this.id = id;
        this.version = version;
        this.reader = reader;
    }

    public String name() {
        return name;
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
    public T read(ByteBuffer buffer) {
        return reader.read(buffer);
    }

    @Override
    public String toString() {
        return name;
    }
}
