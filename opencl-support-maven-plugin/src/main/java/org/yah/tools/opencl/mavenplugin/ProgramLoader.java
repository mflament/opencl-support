package org.yah.tools.opencl.mavenplugin;

import org.yah.tools.opencl.program.CLProgram;

import javax.annotation.Nullable;

public interface ProgramLoader {

    CLProgram load(String file, @Nullable String compilerOptions);

}
