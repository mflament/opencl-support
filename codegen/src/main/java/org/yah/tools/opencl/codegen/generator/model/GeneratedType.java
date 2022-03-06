package org.yah.tools.opencl.codegen.generator.model;

import com.github.javaparser.ast.*;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.Type;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.NoSuchElementException;
import java.util.Objects;

public abstract class GeneratedType<T, THIS extends GeneratedType<T, THIS>> extends ClassOrInterfaceDeclaration {

    protected final T generatedFrom;
    protected final CompilationUnit compilationUnit;

    protected GeneratedType(T generatedFrom, String packageName, String simpleName, boolean isInterface) {
        super(NodeList.nodeList(), isInterface, simpleName);
        this.generatedFrom = Objects.requireNonNull(generatedFrom, "source is null");
        Objects.requireNonNull(packageName, "packageName is null");
        compilationUnit = new CompilationUnit().addImport(packageName);
        compilationUnit.addType(this);
    }

    protected abstract THIS getThis();

    public T getGeneratedFrom() {
        return generatedFrom;
    }

    public CompilationUnit getCompilationUnit() {
        return compilationUnit;
    }

    public final THIS addImport(ClassOrInterfaceDeclaration declaration) {
        compilationUnit.addImport(declaration.getFullyQualifiedName().orElseGet(declaration::getNameAsString));
        return getThis();
    }

    public final THIS addImport(String fqn) {
        compilationUnit.addImport(fqn);
        return getThis();
    }

    public final THIS addImport(Class<?> type) {
        compilationUnit.addImport(type);
        return getThis();
    }

    public  Type addType(Class<?> javaClass) {
        if (javaClass.isPrimitive()) {
            if (javaClass == Byte.TYPE) return PrimitiveType.byteType();
            if (javaClass == Short.TYPE) return PrimitiveType.shortType();
            if (javaClass == Integer.TYPE) return PrimitiveType.intType();
            if (javaClass == Long.TYPE) return PrimitiveType.longType();
            if (javaClass == Float.TYPE) return PrimitiveType.floatType();
            if (javaClass == Double.TYPE) return PrimitiveType.doubleType();
            if (javaClass == Boolean.TYPE) return PrimitiveType.booleanType();
            throw new IllegalArgumentException("Unsupported primitive type " + javaClass);
        }
        compilationUnit.addImport(javaClass);
        return new ClassOrInterfaceType(null, javaClass.getSimpleName());
    }

    public final String resolveFullyQualifiedName(ClassOrInterfaceType type) {
        return resolveFullyQualifiedName(type.getNameAsString());
    }

    public final String resolveFullyQualifiedName(String simpleName) {
        if (simpleName.equals(getNameAsString())) {
            return getCompilationUnitQualifiedName(simpleName);
        }

        TypeDeclaration<?> localType = compilationUnit.getTypes().stream()
                .filter(t -> t.getNameAsString().equals(simpleName))
                .findAny().orElse(null);
        if (localType != null)
            return getCompilationUnitQualifiedName(localType.getNameAsString());

        return compilationUnit.getImports().stream()
                .map(importDeclaration -> addType(importDeclaration, simpleName))
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(simpleName + " not found in\n" + compilationUnit.getImports()));
    }

    public final void writeJavaFile(Path outputDirectory) throws IOException {
        String packageName = compilationUnit.getPackageDeclaration().map(PackageDeclaration::getNameAsString).orElse(null);
        Path packageDir = packageName == null ? outputDirectory : outputDirectory.resolve(packageName.replaceAll("\\.", "/"));
        Files.createDirectories(packageDir);
        Path outputFile = packageDir.resolve(getNameAsString() + ".java");
        Files.write(outputFile, compilationUnit.toString().getBytes(StandardCharsets.UTF_8));
    }

    @Nullable
    private String addType(ImportDeclaration importDeclaration, String simpleName) {
        String importName = importDeclaration.getNameAsString();
        if (importDeclaration.isAsterisk()) {
            importName = importName.substring(0, importName.length() - ".*".length());
            String className = importName + simpleName;
            if (findClass(className))
                return className;
            return null;
        }
        if (importName.endsWith(simpleName))
            return importName;
        return null;
    }

    private boolean findClass(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private String getCompilationUnitQualifiedName(String simpleName) {
        return compilationUnit.getPackageDeclaration()
                .map(p -> p.getNameAsString() + "." + simpleName)
                .orElse(simpleName);
    }

    public static boolean isAutocloseableCloseMethod(MethodDeclaration declaration) {
        return (declaration.getModifiers().isEmpty() || declaration.isPublic())
                && declaration.getNameAsString().equals("close")
                && declaration.getParameters().isEmpty();
    }
}
