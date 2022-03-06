package org.yah.tools.opencl.codegen.generator.kernel;

import java.util.Arrays;

public enum MethodKind {
    SETTER(0),
    GETTER(1),
    AWAIT(2),
    CLOSE(3),
    INVOKE(4),

    SIZE(10),
    ELEMENTS(11),
    MEMOBJECT(12),

    ASYNC(20);

    private final int id;

    MethodKind(int bit) {
        this.id = 0x1 << bit;
    }

    public int getId() {
        return id;
    }

    public static int ids(MethodKind... kinds) {
        return Arrays.stream(kinds).mapToInt(MethodKind::getId).reduce(0, (left, right) -> left | right);
    }

    public static boolean contains(int actual, MethodKind... kinds) {
        return (actual & ids(kinds)) != 0;
    }

    public static boolean is(int actual, MethodKind... kinds) {
        int ids = ids(kinds);
        return (actual & ids) == ids;
    }
}
