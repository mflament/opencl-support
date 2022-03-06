package org.yah.tools.opencl.context;

public class CLUncloseableContext extends CLContext {
    public CLUncloseableContext(CLContext from) {
        super(from);
    }

    @Override
    public void close() {
        // prevented
    }
}
