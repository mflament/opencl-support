package org.yah.tools.opencl.codegen.parser.impl;

import org.lwjgl.BufferUtils;
import org.yah.tools.opencl.CLParameterReader;
import org.yah.tools.opencl.codegen.parser.model.ParsedKernelArgument;
import org.yah.tools.opencl.codegen.parser.model.type.CLType;
import org.yah.tools.opencl.enums.*;
import org.yah.tools.opencl.kernel.CLKernel;

import java.nio.ByteBuffer;
import java.util.Objects;

import static org.lwjgl.opencl.CL12.*;
import static org.yah.tools.opencl.CLException.check;

public class KernelArgumentParser {
    private final TypeResolver typeResolver;

    private final CLParameterReader parameterReader = new CLParameterReader();
    private final ByteBuffer parameterBuffer = BufferUtils.createByteBuffer(Long.BYTES);

    public KernelArgumentParser(TypeResolver typeResolver) {
        this.typeResolver = Objects.requireNonNull(typeResolver, "typeResolver is null");
    }

    public ParsedKernelArgument parse(CLKernel kernel, int argIndex) {
        String typeName = readStringArgInfo(kernel, argIndex, CL_KERNEL_ARG_TYPE_NAME);
        boolean isPointer = typeName.endsWith("*");
        if (isPointer)
            typeName = typeName.substring(0, typeName.length()-1);
        CLType resolvedType = typeResolver.resolve(typeName);
        return ParsedKernelArgument.builder()
                .withArgIndex(argIndex)
                .withTypeName(typeName)
                .withPointer(isPointer)
                .withType(resolvedType)
                .withArgName(readStringArgInfo(kernel, argIndex, CL_KERNEL_ARG_NAME))
                .withAddressQualifier(readEnumArgInfo(kernel, argIndex, CL_KERNEL_ARG_ADDRESS_QUALIFIER, KernelArgAddressQualifier.class))
                .withAccessQualifier(readEnumArgInfo(kernel, argIndex, CL_KERNEL_ARG_ACCESS_QUALIFIER, KernelArgAccessQualifier.class))
                .withTypeQualifiers(CLBitfield.from(readLongArgInfo(kernel, argIndex, CL_KERNEL_ARG_TYPE_QUALIFIER), KernelArgTypeQualifier.values()))
                .build();
    }

    private String readStringArgInfo(CLKernel kernel, int argIndex, int name) {
        return parameterReader.readSizedString((sb, bb) -> clGetKernelArgInfo(kernel.getId(), argIndex, name, bb, sb));
    }

    private <E extends Enum<E> & CLEnum> E readEnumArgInfo(CLKernel kernel, int argIndex, int name, Class<E> enumyType) {
        int i = readIntArgInfo(kernel, argIndex, name);
        return CLEnum.get(i, enumyType.getEnumConstants());
    }

    private int readIntArgInfo(CLKernel kernel, int argIndex, int name) {
        parameterBuffer.limit(Integer.BYTES);
        check(clGetKernelArgInfo(kernel.getId(), argIndex, name, parameterBuffer, null));
        return parameterBuffer.getInt(0);
    }

    @SuppressWarnings("SameParameterValue")
    private long readLongArgInfo(CLKernel kernel, int argIndex, int name) {
        parameterBuffer.limit(Long.BYTES);
        check(clGetKernelArgInfo(kernel.getId(), argIndex, name, parameterBuffer, null));
        return parameterBuffer.getLong(0);
    }

}
