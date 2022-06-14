package org.yah.opencl.test.programs.cl;

import javax.annotation.Nullable;
import org.yah.tools.opencl.program.CLCompilerOptions;
import org.yah.tools.opencl.context.CLContext;
import org.yah.tools.opencl.platform.CLDevice;
import org.yah.tools.opencl.enums.CommandQueueProperty;
import org.yah.tools.opencl.generated.AbstractGeneratedProgram;
import org.yah.opencl.test.programs.SumProgram;
import java.nio.IntBuffer;
import org.yah.opencl.test.programs.cl.kernels.CLMySumKernelInt;

public class CLSumProgramInt extends AbstractGeneratedProgram implements SumProgram<IntBuffer> {

    public static final CLCompilerOptions DEFAULT_COMPILER_OPTIONS = CLCompilerOptions.parse("-D T=int");

    public static final String PROGRAM_PATH = "classpath:sum.cl";

    public CLSumProgramInt(CLContext context, @Nullable CLDevice device, @Nullable CLCompilerOptions compilerOptions, CommandQueueProperty... commandQueueProperties) {
        super(context, PROGRAM_PATH, device, compilerOptions, commandQueueProperties);
    }

    public CLSumProgramInt(CLContext context) {
        this(context, null, DEFAULT_COMPILER_OPTIONS);
    }

    public CLMySumKernelInt mySum() {
        return new CLMySumKernelInt(this, "mySum");
    }
}
