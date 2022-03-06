package org.yah.tools.opencl.codegen.generator.kernel;

import org.yah.tools.opencl.codegen.generator.model.old.GeneratorKernel;
import org.yah.tools.opencl.codegen.generator.model.old.KernelInterface;
import org.yah.tools.opencl.codegen.generator.NamingStrategy;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * https://www.khronos.org/registry/OpenCL/sdk/1.2/docs/man/xhtml/clSetKernelArg.html
 */
public class KernelnterfacesGenerator {

    private final NamingStrategy namingStrategy;

    public KernelnterfacesGenerator(NamingStrategy namingStrategy) {
        this.namingStrategy = Objects.requireNonNull(namingStrategy, "namingStrategy is null");
    }

    public List<KernelInterface> generateKernelInterfaces(String packageName, List<GeneratorKernel> kernels) {
        String kernelPackageName = packageName + ".kernels";
        return kernels.stream()
                .map(kernel -> generateKernelInterface(kernelPackageName, kernel))
                .collect(Collectors.toList());
    }

    private KernelInterface generateKernelInterface(String packageName, GeneratorKernel kernel) {
        KernelInterfaceGenerator interfaceGenerator = new KernelInterfaceGenerator(namingStrategy, packageName, kernel);
        return new KernelInterface(kernel, interfaceGenerator.generate(kernel));
    }

}
