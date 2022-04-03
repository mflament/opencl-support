package org.yah.opencl.test.sum;

import org.lwjgl.BufferUtils;
import org.yah.opencl.test.SandboxSupport;
import org.yah.opencl.test.programs.SumProgram;
import org.yah.opencl.test.programs.cl.CLSumProgramInt;
import org.yah.tools.opencl.context.CLContext;

import java.nio.IntBuffer;

import static org.yah.opencl.test.SandboxSupport.parallelFor;

class IntSumSandbox extends AbstractSumSandbox<IntBuffer> {
    public IntSumSandbox(CLContext context) {
        super(context);
    }

    @Override
    protected SumProgram<IntBuffer> createProgram(CLContext context) {
        return new CLSumProgramInt(context);
    }

    @Override
    protected IntBuffer createBuffer(int count) {
        return BufferUtils.createIntBuffer(count);
    }

    @Override
    protected void randomize(IntBuffer buffer, double low, double height) {
        SandboxSupport.randomize(buffer, (int) low, (int) height);
    }

    @Override
    protected void compare(String message, IntBuffer actual, IntBuffer expected) {
        SandboxSupport.compare(message, actual, expected);
    }

    @Override
    protected void localSum(IntBuffer a, IntBuffer b, IntBuffer results) {
        parallelFor(a, i -> results.put(i, a.get(i) + b.get(i)));
    }
}
