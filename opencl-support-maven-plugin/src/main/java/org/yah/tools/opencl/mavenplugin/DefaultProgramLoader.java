package org.yah.tools.opencl.mavenplugin;

import org.yah.tools.opencl.codegen.loader.ProgramLoader;
import org.yah.tools.opencl.context.CLContext;
import org.yah.tools.opencl.program.CLProgram;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.Objects;

public class DefaultProgramLoader implements ProgramLoader  {

    private final CLContext context;

    public DefaultProgramLoader(CLContext context) {
        this.context = Objects.requireNonNull(context, "context is null");
    }

    @Override
    public CLProgram load(Path file, @Nullable String compilerOptions) {
        return null;
    }
}
