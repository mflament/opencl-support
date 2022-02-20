package org.yah.tools.opencl.platform;

import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opencl.CL;
import org.lwjgl.opencl.CL10;
import org.lwjgl.opencl.CLCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yah.tools.opencl.CLUtils;
import org.yah.tools.opencl.enums.DeviceType;
import org.yah.tools.opencl.enums.deviceinfo.DeviceInfo;
import org.yah.tools.opencl.enums.platforminfo.PlatformInfo;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.lwjgl.opencl.CL10.*;
import static org.yah.tools.opencl.CLException.check;

public class CLPlatform {

    private static final Logger LOGGER = LoggerFactory.getLogger(CLPlatform.class);

    private List<CLDevice> devices;

    public static List<CLPlatform> platforms() {
        IntBuffer countBuffer = BufferUtils.createIntBuffer(1);
        check(clGetPlatformIDs(null, countBuffer));
        int count = countBuffer.get(0);
        if (countBuffer.get(0) == 0)
            return Collections.emptyList();

        List<CLPlatform> platforms = new ArrayList<>(count);
        PointerBuffer platformIds = BufferUtils.createPointerBuffer(count);
        check(clGetPlatformIDs(platformIds, countBuffer));
        for (int i = 0; i < countBuffer.get(0); i++) {
            platforms.add(new CLPlatform(platformIds.get(i)));
        }
        return platforms;
    }

    public static CLPlatform getDefaultPlatform() {
        PointerBuffer platforms = BufferUtils.createPointerBuffer(1);
        IntBuffer numPlatforms = BufferUtils.createIntBuffer(1);
        check(clGetPlatformIDs(platforms, numPlatforms));
        if (numPlatforms.get(0) > 0)
            return new CLPlatform(platforms.get(0));
        throw new IllegalStateException("No default platform");
    }


    private final long id;
    private final String name;
    private volatile CLCapabilities capabilities;

    public CLPlatform(long id) {
        super();
        this.id = id;
        this.name = (String) readPlatformInfo(PlatformInfo.PLATFORM_NAME);
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public CLCapabilities getCapabilities() {
        if (capabilities == null) {
            synchronized (this) {
                if (capabilities == null)
                    capabilities = CL.createPlatformCapabilities(id);
            }
        }
        return capabilities;
    }

    public List<CLDevice> getDevices(DeviceType deviceType) {
        if (devices == null) {
            int[] numDevices = new int[1];
            int error = clGetDeviceIDs(id, deviceType.id(), null, numDevices);
            if (error == CL10.CL_DEVICE_NOT_FOUND)
                return Collections.emptyList();
            PointerBuffer deviceIds = BufferUtils.createPointerBuffer(numDevices[0]);
            check(clGetDeviceIDs(id, CL_DEVICE_TYPE_ALL, deviceIds, numDevices));
            devices = new ArrayList<>(numDevices[0]);
            for (int i = 0; i < numDevices[0]; i++) {
                try {
                    devices.add(new CLDevice(this, deviceIds.get(i)));
                } catch (IndexOutOfBoundsException e) {
                    LOGGER.error("Invalid device count " + numDevices[0] + ", devices ids count " + deviceIds.remaining() + " for platform " + name);
                }
            }
        }
        return devices;
    }

    public CLDevice getDefaultDevice() {
        List<CLDevice> devices = getDevices(DeviceType.DEVICE_TYPE_DEFAULT);
        if (devices.size() > 0)
            return devices.get(0);
        throw new IllegalStateException("No default device");
    }

    @Override
    public String toString() {
        return name;
    }

    public String toDetailedString() {
        StringBuilder sb = new StringBuilder();
        final String newline = System.lineSeparator();
        LinePrinter printer = (params) -> {
            for (Object param : params)
                sb.append(param);
            sb.append(newline);
        };

        CLCapabilities capabilities = getCapabilities();
        ByteBuffer buffer = BufferUtils.createByteBuffer(8 * 1024);
        printer.println(getName());
        printer.println("\tvendor:  ", readPlatformInfo(PlatformInfo.PLATFORM_VENDOR));
        printer.println("\tversion: ", readPlatformInfo(PlatformInfo.PLATFORM_VERSION));
        printer.println("\tprofile: ", readPlatformInfo(PlatformInfo.PLATFORM_PROFILE));
        printer.println("\texts:    ", readPlatformInfo(PlatformInfo.PLATFORM_EXTENSIONS));
        if (PlatformInfo.PLATFORM_HOST_TIMER_RESOLUTION.available(capabilities))
            printer.println("\thost_timer_res:    " + readPlatformInfo(PlatformInfo.PLATFORM_HOST_TIMER_RESOLUTION));
        printer.println("\tdevices:");
        List<CLDevice> devices = getDevices(DeviceType.DEVICE_TYPE_ALL);
        List<DeviceInfo<?>> deviceInfos = DeviceInfo.DEVICE_INFOS.stream()
                .filter(i -> i.available(capabilities))
                .collect(Collectors.toList());
        devices.forEach(d -> {
            printer.println("\t  - ", d.getName());
            for (DeviceInfo<?> deviceInfo : deviceInfos) {
                Object value = d.getDeviceInfo(deviceInfo);
                printer.println("\t\t", deviceInfo.name(), "=", value);
            }
        });
        return sb.toString();
    }

    public Object readPlatformInfo(PlatformInfo<?> info) {
        ByteBuffer buffer = CLUtils.readSizedParam((sb, bb) -> clGetPlatformInfo(id, info.id(), bb, sb));
        return info.read(buffer);
    }

    @FunctionalInterface
    private interface LinePrinter {
        void println(Object... params);
    }

}
