package org.yah.tools.opencl.ndrange;

public class NDRange1 extends AbstractNDRange<NDRange1> {

    NDRange1() {
        super(1);
    }

    @Override
    protected NDRange1 self() {
        return this;
    }

    public NDRange1 globalWorkSize(long x) {
        globalWorkSizesBuffer.put(0, x);
        return this;
    }

    public NDRange1 localWorkSize(long x) {
        localWorkSizesBuffer.limit(1);
        localWorkSizesBuffer.put(0, x);
        return this;
    }

    public NDRange1 globalWorkOffset(long x) {
        globalWorkOffsetsBuffer.limit(1);
        globalWorkOffsetsBuffer.put(0, x);
        return this;
    }

}
