package org.yah.opencl.test.programs.cl;

import javax.annotation.Nullable;
import org.yah.tools.opencl.program.CLCompilerOptions;
import org.yah.tools.opencl.context.CLContext;
import org.yah.tools.opencl.platform.CLDevice;
import org.yah.tools.opencl.enums.CommandQueueProperty;
import org.yah.tools.opencl.generated.AbstractGeneratedProgram;
import org.yah.opencl.test.programs.OclReductionProgram;
import java.nio.FloatBuffer;
import org.yah.opencl.test.programs.cl.kernels.CLReduce0KernelFloat;
import org.yah.opencl.test.programs.cl.kernels.CLReduce1KernelFloat;
import org.yah.opencl.test.programs.cl.kernels.CLReduce2KernelFloat;
import org.yah.opencl.test.programs.cl.kernels.CLReduce3KernelFloat;
import org.yah.opencl.test.programs.cl.kernels.CLReduce4KernelFloat;
import org.yah.opencl.test.programs.cl.kernels.CLReduce5KernelFloat;
import org.yah.opencl.test.programs.cl.kernels.CLReduce6KernelFloat;

public class CLOclReductionProgramFloat extends AbstractGeneratedProgram implements OclReductionProgram<FloatBuffer> {

    public static final CLCompilerOptions DEFAULT_COMPILER_OPTIONS = CLCompilerOptions.parse("-D blockSize=128 -D nIsPow2=1 -D T=float");

    public static final String PROGRAM_PATH = "classpath:oclReduction.cl";

    public CLOclReductionProgramFloat(CLContext context, @Nullable CLDevice device, @Nullable CLCompilerOptions compilerOptions, CommandQueueProperty... commandQueueProperties) {
        super(context, PROGRAM_PATH, device, compilerOptions, commandQueueProperties);
    }

    public CLOclReductionProgramFloat(CLContext context) {
        this(context, null, DEFAULT_COMPILER_OPTIONS);
    }

    public CLReduce0KernelFloat reduce0() {
        return new CLReduce0KernelFloat(this, "reduce0");
    }

    public CLReduce1KernelFloat reduce1() {
        return new CLReduce1KernelFloat(this, "reduce1");
    }

    public CLReduce2KernelFloat reduce2() {
        return new CLReduce2KernelFloat(this, "reduce2");
    }

    public CLReduce3KernelFloat reduce3() {
        return new CLReduce3KernelFloat(this, "reduce3");
    }

    public CLReduce4KernelFloat reduce4() {
        return new CLReduce4KernelFloat(this, "reduce4");
    }

    public CLReduce5KernelFloat reduce5() {
        return new CLReduce5KernelFloat(this, "reduce5");
    }

    public CLReduce6KernelFloat reduce6() {
        return new CLReduce6KernelFloat(this, "reduce6");
    }
}
