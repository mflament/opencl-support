package org.yah.tools.opencl.codegen.parser.type;

public interface CLType {

    String getName();

    CLType getComponentType();

    default boolean isPointer() {
        return false;
    }

    default PointerType asPointer() {
        throw new UnsupportedOperationException("not a pointer " + this);
    }

    default boolean isScalar() {
        return false;
    }

    default ScalarDataType asScalar() {
        throw new UnsupportedOperationException("not a scalar " + this);
    }

    default boolean isVector() {
        return false;
    }

    default VectorType asVector() {
        throw new UnsupportedOperationException("not a vector " + this);
    }

    default boolean isCLTypeParameter() {
        return false;
    }

    default CLTypeParameter asCLTypeParameter() {
        throw new UnsupportedOperationException("not a macro parameter " + this);
    }

    default boolean isMemObjectType() {
        return false;
    }

    default MemObjectType asMemObjectType() {
        throw new UnsupportedOperationException("not an other data type " + this);
    }

    default boolean isUnresolved() {
        return false;
    }

}
