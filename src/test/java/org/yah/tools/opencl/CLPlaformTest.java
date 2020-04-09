package org.yah.tools.opencl;

import java.lang.reflect.Array;
import java.util.List;

import org.lwjgl.opencl.CLCapabilities;
import org.yah.tools.opencl.platform.CLDevice;
import org.yah.tools.opencl.platform.CLPlaform;
import org.yah.tools.opencl.platform.DeviceInfo;
import org.yah.tools.opencl.platform.DeviceType;
import org.yah.tools.opencl.platform.PlatformInfo;

public class CLPlaformTest {

    public static void main(String[] args) {
        List<CLPlaform> platforms = CLPlaform.platforms();
        platforms.forEach(p -> {
            System.out.println(p.getName());
            System.out.println("\tvendor:  " + p.getPlatformInfo(PlatformInfo.PLATFORM_VENDOR));
            System.out.println("\tversion: " + p.getPlatformInfo(PlatformInfo.PLATFORM_VERSION));
            System.out.println("\tprofile: " + p.getPlatformInfo(PlatformInfo.PLATFORM_PROFILE));
            System.out.println("\texts:    " + p.getPlatformInfo(PlatformInfo.PLATFORM_EXTENSIONS));
            System.out.println("\tdevices:");

            CLCapabilities capabilities = p.getCapabilities();
            List<CLDevice> devices = CLDevice.platformDevices(p.getId(),
                    DeviceType.DEVICE_TYPE_GPU);
            devices.forEach(d -> {
                System.out.println("\t  - " + d.getName());
                DeviceInfo[] values = DeviceInfo.values();
                for (DeviceInfo value : values) {
                    if (!value.available(capabilities))
                        continue;
                    CLInfoReader<?> reader = value.getReader();
                    Object info = d.getDeviceInfo(value, reader);
                    System.out.println("\t\t" + value.name() + ": " + toString(info));
                }
            });
        });
        CLPlaform platform = CLPlaform.defaultPlatform();
        CLDevice device = platform != null ? platform.getDefaultDevice() : null;
        System.out.println("\ndefault device: " + platform + " / " + device);
    }

    private static String toString(Object value) {
        if (value == null)
            return null;
        if (value.getClass().isArray()) {
            int l = Array.getLength(value);
            StringBuilder sb = new StringBuilder();
            sb.append('[');
            for (int i = 0; i < l; i++) {
                sb.append(Array.get(value, i));
                if (i < l - 1)
                    sb.append(", ");
            }
            sb.append(']');
            return sb.toString();
        }
        return value.toString();
    }
}
