package org.yah.tools.opencl.codegen.naming;

import javax.annotation.Nullable;

public final class TypeNameDecorator {
    @Nullable
    private final String prefix;
    @Nullable
    private final String suffix;

    public TypeNameDecorator(@Nullable String prefix, @Nullable String suffix) {
        this.prefix = prefix;
        this.suffix = suffix;
    }

    public String decorate(String name) {
        String res = "";
        if (prefix != null) res += prefix;
        res += name;
        if (suffix != null) res += suffix;
        return res;
    }
}
