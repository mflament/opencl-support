package org.yah.tools.opencl.ndrange;

public class NDRange3 extends AbstractNDRange<NDRange3> {

     NDRange3() {
        super(3);
    }

    @Override
    protected NDRange3 self() {
        return this;
    }

    public NDRange3 globalWorkSize(long x, long y, long z) {
        globalWorkSizesBuffer.limit(3);
        globalWorkSizesBuffer.put(0, x);
        globalWorkSizesBuffer.put(1, y);
        globalWorkSizesBuffer.put(2, z);
        return this;
    }

    public NDRange3 localWorkSize(long x, long y, long z) {
        globalWorkSizesBuffer.limit(3);
        localWorkSizesBuffer.put(0, x);
        localWorkSizesBuffer.put(1, y);
        localWorkSizesBuffer.put(2, z);
        return this;
    }

    public NDRange3 globalWorkOffset(long x, long y, long z) {
        globalWorkSizesBuffer.limit(3);
        globalWorkOffsetsBuffer.put(0, x);
        globalWorkOffsetsBuffer.put(1, y);
        globalWorkOffsetsBuffer.put(2, z);
        return this;
    }

}
