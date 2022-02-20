package org.yah.tools.opencl;

import org.yah.tools.opencl.platform.CLDevice;
import org.yah.tools.opencl.platform.CLPlatform;

import java.util.List;

public class CLPlaformTest {

    public static void main(String[] args) {
        List<CLPlatform> platforms = CLPlatform.platforms();
        platforms.forEach(p -> System.out.println(p.toDetailedString()));

        CLPlatform platform = CLPlatform.getDefaultPlatform();
        CLDevice device = platform.getDefaultDevice();
        System.out.println("\ndefault device: " + platform + " / " + device);
    }

}
