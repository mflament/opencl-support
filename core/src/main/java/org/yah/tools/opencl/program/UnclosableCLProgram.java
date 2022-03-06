package org.yah.tools.opencl.program;

public class UnclosableCLProgram extends CLProgram {

    public UnclosableCLProgram(CLProgram delegate) {
        super(delegate);
    }

    @Override
    public void close() {
        // prevented
    }
}
