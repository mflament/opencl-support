package org.yah.tools.opencl.codegen;

import org.yah.tools.opencl.context.CLContext;
import org.yah.tools.opencl.program.CLProgram;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;
import java.util.function.Function;

public final class CLTestSupport {

    private CLTestSupport() {
    }

    public static void runWithContext(Consumer<CLContext> test) {
        try (CLContext context = CLContext.builder().build()) {
            test.accept(context);
        }
    }
}
