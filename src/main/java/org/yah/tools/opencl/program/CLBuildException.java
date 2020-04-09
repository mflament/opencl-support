package org.yah.tools.opencl.program;

import org.yah.tools.opencl.CLException;

public class CLBuildException extends CLException {

    private static final long serialVersionUID = 1L;

    private final String log;

    public CLBuildException(int error, String log) {
        super(error, log);
        this.log = log;
    }

    public String getLog() { return log; }

}
