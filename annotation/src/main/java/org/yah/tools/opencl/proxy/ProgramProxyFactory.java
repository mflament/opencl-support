package org.yah.tools.opencl.proxy;

import org.yah.tools.opencl.annotations.Kernel;
import org.yah.tools.opencl.annotations.KernelArgumentSetter;
import org.yah.tools.opencl.annotations.NDRangeConfigurer;
import org.yah.tools.opencl.annotations.Program;
import org.yah.tools.opencl.cmdqueue.CLCommandQueue;
import org.yah.tools.opencl.context.CLContext;
import org.yah.tools.opencl.kernel.CLKernel;
import org.yah.tools.opencl.program.CLProgram;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ProgramProxyFactory {


    @Nullable
    private final ClassLoader classLoader;

    private final Map<Class<?>, ProgramProxy<?>> cache = new HashMap<>();

    public ProgramProxyFactory() {
        this(null);
    }

    public ProgramProxyFactory(@Nullable ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @SuppressWarnings("unchecked")
    public synchronized <T extends AutoCloseable> T create(@Nonnull Class<T> programInterface, CLCommandQueue commandQueue) {
        Objects.requireNonNull(programInterface, "programInterface is null");
        ProgramProxy<?> programProxy = cache.computeIfAbsent(programInterface, i -> createProgramProxy(programInterface, commandQueue.getContext()));
        ClassLoader classLoaderToUse = classLoader == null ? programInterface.getClassLoader() : classLoader;
        InvocationHandler handler = (_proxy, method, args) -> programProxy.invoke(method, commandQueue, args);
        return (T) Proxy.newProxyInstance(classLoaderToUse, new Class[]{programInterface}, handler);
    }

    private <T> ProgramProxy<T> createProgramProxy(Class<T> programInterface, CLContext context) {
        Program programAnnotation = programInterface.getAnnotation(Program.class);
        if (programAnnotation == null)
            throw new IllegalArgumentException("No program annotation found on " + programInterface.getName());

        // use all context devices for the program, next command queues can use different devices
        CLProgram program = CLProgram.builder(context)
                .withFile(programAnnotation.value())
                .withOptions(programAnnotation.options())
                .build();

        ProgramProxy.Builder<T> builder = ProgramProxy.builder(programInterface);
        Method[] methods = programInterface.getMethods(); // include parent interfaces
        for (Method method : methods) {
            Kernel kernelAnnotation = method.getAnnotation(Kernel.class);
            if (kernelAnnotation == null)
                continue;

            String kernelName = kernelAnnotation.value();
            if (kernelName.equals(Kernel.METHOD_NAME))
                kernelName = method.getName();
            CLKernel kernel = new CLKernel(program, kernelName);
            KernelProxy kernelProxy = KernelProxy.builder(kernel)
                    .withAsync(isAsync(method))
                    .withDimensions(getRangeDimension(method, kernelAnnotation))
                    .withArgumentsSetter(createArgumentSetter(method, kernelAnnotation))
                    .withRangeConfigurer(createRangeConfigurer(method, kernelAnnotation))
                    .build();
            builder.withKernelProxy(method, kernelProxy);
        }
        return builder.build();
    }

    private boolean isAsync(Method method) {
        return method.getReturnType() == Long.TYPE;
    }

    private KernelArgumentSetter createArgumentSetter(Method method, Kernel kernelAnnotation) {
//        Parameter[] parameters = method.getParameters();
//        for (int i = 0; i < parameters.length; i++) {
//            Parameter parameter = parameters[i];
//            AddressSpace asAnnotation = parameter.getAnnotation(AddressSpace.class);
//            CLAddressSpace addressSpace = asAnnotation == null ? null : asAnnotation.value();
//            boolean unsigned = parameter.getAnnotation(Unsigned.class) != null;
//            checkParameterType(parameter, addressSpace);
//
//            //kernelParameters.add();
//        }
        return null;
    }

    private int getRangeDimension(Method method, Kernel kernelAnnotation) {
        return 0;
    }

    private NDRangeConfigurer createRangeConfigurer(Method method, Kernel kernelAnnotation) {
        return null;
    }

    private static class RangeSource {

    }
}