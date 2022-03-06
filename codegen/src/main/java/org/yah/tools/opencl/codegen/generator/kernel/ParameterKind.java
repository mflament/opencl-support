package org.yah.tools.opencl.codegen.generator.kernel;

public enum ParameterKind {
    VALUE, // for CLType scalar
    ADDRESS, // for CLType other,
    BUFFER_BYTES, // buffer size in bytes
    BUFFER_ELEMENTS, // buffer size in components
    BUFFER, // buffer for pointer type
    BUFFER_OFFSET,
    VECTOR_COMPONENT, // a component of a vector (x,y,z or w)
    MEMOBJECT,
    EVENT; // CLMemObject for pointer
}
