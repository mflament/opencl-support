package org.yah.tools.opencl.codegen.generator.model.old;

import com.github.javaparser.ast.type.Type;
import org.yah.tools.opencl.codegen.parser.model.ParsedKernelArgument;
import org.yah.tools.opencl.codegen.parser.model.type.CLType;
import org.yah.tools.opencl.codegen.parser.model.type.ScalarDataType;
import org.yah.tools.opencl.enums.KernelArgAddressQualifier;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Objects;

import static org.yah.tools.opencl.codegen.generator.impl.CodeGeneratorSupport.*;
import static org.yah.tools.opencl.enums.KernelArgAccessQualifier.READ_ONLY;
import static org.yah.tools.opencl.enums.KernelArgAddressQualifier.GLOBAL;
import static org.yah.tools.opencl.enums.KernelArgTypeQualifier.CONST;

public final class GeneratedKernelArgument {

    private final ParsedKernelArgument parsedArgument;
    private final String javaName;
    private final String javaDoc;
    private final ScalarDataType scalarDataType;
    private final Type parserType;
    @Nullable
    private final Class<?> bufferClass;

    public GeneratedKernelArgument(ParsedKernelArgument parsedArgument, String javaName) {
        this.parsedArgument = Objects.requireNonNull(parsedArgument, "argument is null");
        this.javaName = Objects.requireNonNull(javaName, "javaName is null");
        javaDoc = String.format("%2d: %s", parsedArgument.getArgIndex(), parsedArgument);
        scalarDataType = resolveScalarType(parsedArgument.getType());
        parserType = resolveParserType(scalarDataType);
        bufferClass = resolveBufferClass(scalarDataType);
    }

    @Nullable
    private Class<?> resolveBufferClass(ScalarDataType scalarDataType) {
        if (isPointer() || isVector()) {
            Class<?> bufferClass = resolveBufferType(scalarDataType);
            if (bufferClass != ByteBuffer.class)
                return bufferClass;
        }
        return null;
    }

    public ParsedKernelArgument getParsedArgument() {
        return parsedArgument;
    }

    public boolean isPointer() {
        return parsedArgument.isPointer();
    }

    public ScalarDataType getScalarDataType() {
        return scalarDataType;
    }

    public Type getParserType() {
        return parserType;
    }

    @Nullable
    public Class<?> getBufferClass() {
        return bufferClass;
    }

    public int getArgIndex() {
        return parsedArgument.getArgIndex();
    }

    public CLType getType() {
        return parsedArgument.getType();
    }

    public String getArgName() {
        return parsedArgument.getArgName();
    }

    public String getJavaName() {
        return javaName;
    }

    public String getJavaDoc() {
        return javaDoc;
    }

    public boolean canRead() {
        return parsedArgument.getType().isPointer() && parsedArgument.getAddressQualifier() == GLOBAL &&
                !parsedArgument.getTypeQualifiers().contains(CONST) && parsedArgument.getAccessQualifier() != READ_ONLY;
    }

    public boolean isAnyAddressQualifier(KernelArgAddressQualifier... qualifiers) {
        KernelArgAddressQualifier addressQualifier = parsedArgument.getAddressQualifier();
        return Arrays.asList(qualifiers).contains(addressQualifier);
    }

    @Override
    public String toString() {
        return "GeneratedKernelArgument{" +
                "parsedArgument=" + parsedArgument +
                ", javaName='" + javaName + '\'' +
                ", javaDoc='" + javaDoc + '\'' +
                '}';
    }

    public boolean isScalar() {
        return getType().isScalar();
    }

    public boolean isVector() {
        return getType().isVector();
    }


    public boolean isOther() {
        return getType().isOther();
    }

    public boolean isUnresolved() {
        return getType().isUnresolved();
    }
}
