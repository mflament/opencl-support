package org.yah.tools.opencl.codegen.parser.type;

public interface CLType {

    String getName();

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
