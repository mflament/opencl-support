package org.yah.opencl.test.programs.cl;

import javax.annotation.Nullable;
import org.yah.tools.opencl.program.CLCompilerOptions;
import org.yah.tools.opencl.context.CLContext;
import org.yah.tools.opencl.platform.CLDevice;
import org.yah.tools.opencl.enums.CommandQueueProperty;
import org.yah.tools.opencl.generated.AbstractGeneratedProgram;
import org.yah.opencl.test.programs.SqrtProgram;
import org.yah.opencl.test.programs.cl.kernels.CLMySqrtKernel;

public class CLSqrtProgram extends AbstractGeneratedProgram implements SqrtProgram {

    public static final CLCompilerOptions DEFAULT_COMPILER_OPTIONS = CLCompilerOptions.parse("");

    public static final String PROGRAM_PATH = "classpath:sqrt.cl";

    public CLSqrtProgram(CLContext context, @Nullable CLDevice device, @Nullable CLCompilerOptions compilerOptions, CommandQueueProperty... commandQueueProperties) {
        super(context, PROGRAM_PATH, device, compilerOptions, commandQueueProperties);
    }

    public CLSqrtProgram(CLContext context) {
        this(context, null, DEFAULT_COMPILER_OPTIONS);
    }

    public CLMySqrtKernel mySqrt() {
        return new CLMySqrtKernel(this, "mySqrt");
    }
}
