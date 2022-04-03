package org.yah.tools.opencl.codegen.generator.kernel;

import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.Type;
import org.lwjgl.PointerBuffer;
import org.yah.tools.opencl.codegen.builder.JavaTypeBuilder;
import org.yah.tools.opencl.codegen.parser.type.CLType;
import org.yah.tools.opencl.codegen.parser.type.ScalarDataType;

import java.nio.*;

public class AstTypeGenerator {

    public static Type createType(CLType clType, JavaTypeBuilder typeBuilder) {
        if (clType.isScalar())
            return createPrimitiveType(clType.asScalar());

        if (clType.isMemObjectType())
            return PrimitiveType.longType();

        if (clType.isUnresolved())
            return createBufferType(ScalarDataType.UCHAR, typeBuilder);

        if (clType.isPointer() || clType.isVector()) {
            CLType componentType = clType.getComponentType();
            if (componentType.isScalar()) {
                ScalarDataType scalarType = componentType.asScalar();
                return createBufferType(scalarType, typeBuilder);
            }
        }

        throw new IllegalArgumentException("Invalid clType " + clType);
    }

    public static ClassOrInterfaceType createBufferType(ScalarDataType scalarDataType, JavaTypeBuilder typeBuilder) {
        Class<?> bufferClass;
        switch (scalarDataType) {
            case SHORT:
            case USHORT:
            case HALF:
                bufferClass = ShortBuffer.class;
                break;
            case BOOL:
            case INT:
            case UINT:
                bufferClass = IntBuffer.class;
                break;
            case LONG:
            case ULONG:
                bufferClass = LongBuffer.class;
                break;
            case SIZE_T:
            case PTRDIFF_T:
            case INTPTR_T:
            case UINTPTR_T:
                bufferClass = PointerBuffer.class;
                break;
            case FLOAT:
                bufferClass = FloatBuffer.class;
                break;
            case DOUBLE:
                bufferClass = DoubleBuffer.class;
                break;
            default:
                bufferClass = ByteBuffer.class;
                break;
        }
        return typeBuilder.addImport(bufferClass);
    }

    public static PrimitiveType createPrimitiveType(ScalarDataType scalarDataType) {
        switch (scalarDataType) {
            case BOOL:
                return PrimitiveType.booleanType();
            case SHORT:
            case USHORT:
            case HALF:
                return PrimitiveType.shortType();
            case INT:
            case UINT:
                return PrimitiveType.intType();
            case LONG:
            case ULONG:
            case SIZE_T:
            case PTRDIFF_T:
            case INTPTR_T:
            case UINTPTR_T:
                return PrimitiveType.longType();
            case FLOAT:
                return PrimitiveType.floatType();
            case DOUBLE:
                return PrimitiveType.doubleType();
            default:
                return PrimitiveType.byteType();
        }
    }

}
