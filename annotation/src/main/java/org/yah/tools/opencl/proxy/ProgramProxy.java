package org.yah.tools.opencl.proxy;

import org.yah.tools.opencl.cmdqueue.CLCommandQueue;
import org.yah.tools.opencl.program.CLProgram;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

class ProgramProxy<T> {
    private final Class<T> programInterface;
    private final Map<Method, KernelProxy> kernelProxies;

    private ProgramProxy(Class<T> programInterface,Map<Method, KernelProxy> kernelProxies) {
        this.programInterface = Objects.requireNonNull(programInterface, "programInterface is null");
        this.kernelProxies = Collections.unmodifiableMap(new HashMap<>(Objects.requireNonNull(kernelProxies, "kernelProxies is null")));
    }

    Object invoke(Method method, CLCommandQueue commandQueue, Object[] args) {
        KernelProxy kernelProxy = kernelProxies.get(method);
        if (kernelProxy == null)
            throw new IllegalArgumentException("no kernel proxy form method " + method + " in program " + this);
        return kernelProxy.invoke(commandQueue, args);
    }

    @Override
    public String toString() {
        return "ProgramProxy{" +
                "programInterface=" + programInterface.getName() +
                '}';
    }

    public static <T> Builder<T> builder(Class<T> programInterface) {
        return new Builder<>(programInterface);
    }

    public static final class Builder<T> {
        private final Class<T> programInterface;
        private CLProgram program;
        private final Map<Method, KernelProxy> kernelProxies = new HashMap<>();

        private Builder(Class<T> programInterface) {
            this.programInterface = programInterface;
        }

        public Builder<T> withKernelProxy(Method method, KernelProxy proxy) {
            kernelProxies.put(method, proxy);
            return this;
        }

        public ProgramProxy<T> build() {
            return new ProgramProxy<>(programInterface, kernelProxies);
        }
    }
}
