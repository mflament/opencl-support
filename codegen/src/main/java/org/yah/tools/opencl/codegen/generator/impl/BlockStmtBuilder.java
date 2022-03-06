package org.yah.tools.opencl.codegen.generator.impl;

import com.github.javaparser.ast.stmt.BlockStmt;

import java.util.function.Consumer;

public final class BlockStmtBuilder {

    public static BlockStmt build(Consumer<BlockStmtBuilder> consumer) {
        BlockStmt block = new BlockStmt();
        consumer.accept(new BlockStmtBuilder(block));
        return block;
    }

    private final BlockStmt blockStmt;

    private BlockStmtBuilder(BlockStmt blockStmt) {
        this.blockStmt = blockStmt;
    }

    public BlockStmtBuilder withStatement(String statement) {
        blockStmt.addStatement(statement);
        return this;
    }

    public BlockStmtBuilder withStatement(String format, Object... args) {
        blockStmt.addStatement(String.format(format, args));
        return this;
    }

    public BlockStmtBuilder withStatements(String... statements) {
        for (String statement : statements) {
            blockStmt.addStatement(statement);
        }
        return this;
    }

}
