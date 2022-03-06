package org.yah.tools.opencl;

import org.yah.tools.opencl.context.CLContext;
import org.yah.tools.opencl.program.CLProgram;

import java.util.function.Consumer;
import java.util.function.Function;

public final class CLTestSupport {

    private CLTestSupport() {
    }

    public static void runWithProgram(String resource, Consumer<CLProgram> test) {
        runWithProgram(context -> context.programBuilder()
                .withResource(resource)
                .withOptions("-cl-kernel-arg-info")
                .build(), test);
    }

    public static void runWithProgram(Function<CLContext, CLProgram> factory, Consumer<CLProgram> test) {
        try (CLContext context = CLContext.builder().build();
             CLProgram program = factory.apply(context)) {
            test.accept(program);
        }
    }

}
