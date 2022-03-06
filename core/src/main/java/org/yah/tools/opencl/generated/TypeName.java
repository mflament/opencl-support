package org.yah.tools.opencl.generated;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public final class TypeName {
    @Nullable
    private final String packageName;
    private final String simpleName;

    public TypeName(String fqn) {
        Objects.requireNonNull(fqn, "fqn is null");
        String[] names = fqn.split("\\.");
        if (names.length == 0)
            throw new IllegalArgumentException("empty name " + fqn);
        if (names.length == 1) {
            packageName = null;
            simpleName = names[0];
        } else {
            packageName = Arrays.stream(names).limit(names.length - 1).collect(Collectors.joining("."));
            simpleName = names[names.length - 1];
        }
    }

    public TypeName(@Nullable String packageName, String simpleName) {
        if (packageName != null && packageName.length() == 0)
            throw new IllegalArgumentException("package name is empty");
        this.packageName = packageName;
        this.simpleName = Objects.requireNonNull(simpleName, "simpleName is null");
    }

    public TypeName(Class<?> type) {
        Package pkg = type.getPackage();
        this.packageName = pkg != null ? pkg.getName() : null;
        this.simpleName = type.getSimpleName();
    }

    public Optional<String> getPackageName() {
        return Optional.of(packageName);
    }

    public String getSimpleName() {
        return simpleName;
    }

    @Override
    public String toString() {
        return packageName + "." + simpleName;
    }
}
