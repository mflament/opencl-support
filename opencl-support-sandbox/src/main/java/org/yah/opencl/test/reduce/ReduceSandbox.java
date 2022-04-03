package org.yah.opencl.test.reduce;

import org.lwjgl.BufferUtils;
import org.yah.opencl.test.programs.cl.CLOclReductionProgramFloat;
import org.yah.tools.opencl.ndrange.NDRange;
import org.yah.tools.opencl.ndrange.NDRange1;

import java.nio.FloatBuffer;

import static org.yah.opencl.test.SandboxSupport.runInContext;

public class ReduceSandbox {

    public static void main(String[] args) throws Exception {
        int size = 10_000;
        runInContext(context ->
                new Reducer(new CLOclReductionProgramFloat(context).reduce0()).run(size)
        );
    }

    private static final class Reducer {
        private final ReduceKernel<FloatBuffer> kernel;

        public Reducer(ReduceKernel<FloatBuffer> kernel) {
            this.kernel = kernel;
        }

        public void run(int size) {
            FloatBuffer inputs = BufferUtils.createFloatBuffer(size);
            FloatBuffer results = BufferUtils.createFloatBuffer(size);

            kernel.createGIdata(inputs);

            NDRange1 range = NDRange.range1();
            range.globalWorkSize(0);
            kernel.invoke(range);
        }
    }
}
