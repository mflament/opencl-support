package org.yah.tools.opencl.ndrange;

public class NDRange2 extends AbstractNDRange<NDRange2> {

    NDRange2() {
        super(2);
    }

    @Override
    protected NDRange2 self() {
        return this;
    }

    public NDRange2 globalWorkSize(long x, long y) {
        globalWorkSizesBuffer.put(0, x);
        globalWorkSizesBuffer.put(1, y);
        return this;
    }


    public NDRange2 localWorkSize(long x, long y) {
        localWorkSizesBuffer.limit(2);
        localWorkSizesBuffer.put(0, x);
        localWorkSizesBuffer.put(1, y);
        return this;
    }

    public NDRange2 globalWorkOffset(long x, long y) {
        globalWorkOffsetsBuffer.limit(2);
        globalWorkOffsetsBuffer.put(0, x);
        globalWorkOffsetsBuffer.put(1, y);
        return this;
    }

}
