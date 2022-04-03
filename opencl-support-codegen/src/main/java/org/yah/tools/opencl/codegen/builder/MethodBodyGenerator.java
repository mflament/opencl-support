package org.yah.tools.opencl.codegen.builder;

import com.github.javaparser.ast.stmt.BlockStmt;

@FunctionalInterface
public interface MethodBodyGenerator {
    BlockStmt generate(MethodBodyBuilder mbb);
}
