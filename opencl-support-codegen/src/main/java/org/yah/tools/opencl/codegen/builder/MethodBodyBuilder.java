package org.yah.tools.opencl.codegen.builder;

import com.github.javaparser.ast.body.CallableDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.Type;

import java.util.List;
import java.util.Objects;

public final class MethodBodyBuilder {

    private final CallableDeclaration<?> methodDeclaration;

    private final BlockStmt blockStmt = new BlockStmt();

    public MethodBodyBuilder(CallableDeclaration<?> methodDeclaration) {
        this.methodDeclaration = Objects.requireNonNull(methodDeclaration, "methodDeclaration is null");
    }

    public MethodBodyBuilder addStatement(String format, Object... args) {
        return add(String.format(format, args));
    }

    public MethodBodyBuilder add(String stmt) {
        if (!stmt.endsWith(";"))
            stmt += ";";
        blockStmt.addStatement(stmt);
        return this;
    }

    public MethodBodyBuilder add(String... stmts) {
        for (String stmt : stmts) {
            blockStmt.addStatement(stmt);
        }
        return this;
    }

    public List<Parameter> getParameters() {
        return methodDeclaration.getParameters();
    }

    public Parameter getParameter(int index) {
        return methodDeclaration.getParameter(index);
    }

    public boolean isVoidReturnType() {
        if (methodDeclaration.isMethodDeclaration())
            return methodDeclaration.asMethodDeclaration().getType().isVoidType();
        return true;
    }

    public String getMethodName() {
        return methodDeclaration.getNameAsString();
    }

    public MethodBodyBuilder setReturnType(Type type) {
        if (methodDeclaration.isMethodDeclaration()) {
            methodDeclaration.asMethodDeclaration().setType(type);
            return this;
        }
        throw new UnsupportedOperationException("not a method " + methodDeclaration);
    }

    public BlockStmt andReturn(String returnExpression, Object... args) {
        add("return " + String.format(returnExpression, args));
        return blockStmt;
    }

    public BlockStmt build() {
        return blockStmt;
    }

    public String getParameterName(int i) {
        return getParameter(i).getNameAsString();
    }

    public static String quote(String s) {
        return '"' + s + '"';
    }
}
