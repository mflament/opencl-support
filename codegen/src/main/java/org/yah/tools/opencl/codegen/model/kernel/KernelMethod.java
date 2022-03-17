package org.yah.tools.opencl.codegen.model.kernel;

import org.yah.tools.opencl.codegen.model.MethodModel;
import org.yah.tools.opencl.codegen.model.kernel.methods.*;
import org.yah.tools.opencl.codegen.parser.model.ParsedKernel;

import java.util.List;

public interface KernelMethod extends MethodModel {

    @Override
    KernelModel getDeclaringType();

    @Override
    List<? extends KernelMethodParameter> getParameters();

    default KernelMethodParameter getParameter(int index) {
        return getParameters().get(index);
    }

    @SuppressWarnings("unchecked")
    @Override
    default List<? extends KernelMethodParameter> getRequiredParameters() {
        return (List<? extends KernelMethodParameter>) MethodModel.super.getRequiredParameters();
    }

    default ParsedKernel getParsedKernel() {
        return getDeclaringType().getParsedKernel();
    }

    default boolean isKernelArgumentMethod() {
        return false;
    }

    default KernelArgumentMethod asKernelArgumentMethod() {
        throw new UnsupportedOperationException();
    }

    default boolean isAsyncKernelMethod() {
        return false;
    }

    default AsyncKernelMethod asAsyncKernelMethod() {
        throw new UnsupportedOperationException();
    }

    default boolean isSetKernelArgumentMethod() {
        return false;
    }

    default SetKernelArgumentMethod asSetKernelArgumentMethod() {
        throw new UnsupportedOperationException();
    }

    default boolean isSetValue() {
        return false;
    }

    default SetValue asSetValue() {
        throw new UnsupportedOperationException();
    }

    default boolean isWriteBuffer() {
        return false;
    }

    default WriteBuffer asWriteBuffer() {
        throw new UnsupportedOperationException();
    }

    default boolean isReadBuffer() {
        return false;
    }

    default ReadBuffer asReadBuffer() {
        throw new UnsupportedOperationException();
    }

    default boolean isCreateBuffer() {
        return false;
    }

    default CreateBuffer asCreateBuffer() {
        throw new UnsupportedOperationException();
    }

    default boolean isInvoke() {
        return false;
    }

    default Invoke asInvoke() {
        throw new UnsupportedOperationException();
    }

    default boolean isCloseKernel() {
        return false;
    }

    default CloseKernel asCloseKernel() {
        throw new UnsupportedOperationException();
    }

    default boolean isSetLocalSize() {
        return false;
    }

    default SetLocalSize asSetLocalSize() {
        throw new UnsupportedOperationException();
    }
}
