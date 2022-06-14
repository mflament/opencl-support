package org.yah.opencl.test.programs.cl;

import javax.annotation.Nullable;
import org.yah.tools.opencl.program.CLCompilerOptions;
import org.yah.tools.opencl.context.CLContext;
import org.yah.tools.opencl.platform.CLDevice;
import org.yah.tools.opencl.enums.CommandQueueProperty;
import org.yah.tools.opencl.generated.AbstractGeneratedProgram;
import org.yah.opencl.test.programs.SumProgram;
import java.nio.FloatBuffer;
import org.yah.opencl.test.programs.cl.kernels.CLMySumKernelFloat;

public class CLSumProgramFloat extends AbstractGeneratedProgram implements SumProgram<FloatBuffer> {

    public static final CLCompilerOptions DEFAULT_COMPILER_OPTIONS = CLCompilerOptions.parse("-D T=float");

    public static final String PROGRAM_PATH = "classpath:sum.cl";

    public CLSumProgramFloat(CLContext context, @Nullable CLDevice device, @Nullable CLCompilerOptions compilerOptions, CommandQueueProperty... commandQueueProperties) {
        super(context, PROGRAM_PATH, device, compilerOptions, commandQueueProperties);
    }

    public CLSumProgramFloat(CLContext context) {
        this(context, null, DEFAULT_COMPILER_OPTIONS);
    }

    public CLMySumKernelFloat mySum() {
        return new CLMySumKernelFloat(this, "mySum");
    }
}
