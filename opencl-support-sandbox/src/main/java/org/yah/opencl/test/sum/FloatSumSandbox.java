package org.yah.opencl.test.sum;

import org.lwjgl.BufferUtils;
import org.yah.opencl.test.SandboxSupport;
import org.yah.opencl.test.programs.SumProgram;
import org.yah.opencl.test.programs.cl.CLSumProgramFloat;
import org.yah.tools.opencl.context.CLContext;

import java.nio.FloatBuffer;

import static org.yah.opencl.test.SandboxSupport.parallelFor;

class FloatSumSandbox extends AbstractSumSandbox<FloatBuffer> {
    public FloatSumSandbox(CLContext context) {
        super(context);
    }

    @Override
    protected SumProgram<FloatBuffer> createProgram(CLContext context) {
        return new CLSumProgramFloat(context);
    }

    @Override
    protected FloatBuffer createBuffer(int count) {
        return BufferUtils.createFloatBuffer(count);
    }

    @Override
    protected void randomize(FloatBuffer buffer, double low, double height) {
        SandboxSupport.randomize(buffer, (int) low, (int) height);
    }

    @Override
    protected void compare(String message, FloatBuffer actual, FloatBuffer expected) {
        SandboxSupport.compare(message, actual, expected);
    }

    @Override
    protected void localSum(FloatBuffer a, FloatBuffer b, FloatBuffer results) {
        parallelFor(a, i -> results.put(i, a.get(i) + b.get(i)));
    }
}
