package org.yah.tools.opencl.generated;

import org.yah.tools.opencl.CLUtils;

import javax.annotation.Nullable;
import java.util.Objects;

public class ProgramMetadata {

    private final String programFile;

    @Nullable
    private final String compilerOptions;

    public ProgramMetadata(String programFile, @Nullable String compilerOptions) {
        this.programFile = Objects.requireNonNull(programFile, "programFile is null");
        this.compilerOptions = compilerOptions;
    }

    public String getProgramFile() {
        return programFile;
    }

    @Nullable
    public String getCompilerOptions() {
        return compilerOptions;
    }

    public String getBaseName() {
        String path = CLUtils.getPath(programFile);
        int i = path.lastIndexOf('/');
        if (i >= 0)
            path = path.substring(i + 1);
        i = path.lastIndexOf('.');
        if (i > 0)
            path = path.substring(0, i);
        return path;
    }

    @Override
    public String toString() {
        return "ProgramMetadata{" +
                "relativePath='" + programFile + '\'' +
                ", options='" + compilerOptions + '\'' +
                '}';
    }

}
