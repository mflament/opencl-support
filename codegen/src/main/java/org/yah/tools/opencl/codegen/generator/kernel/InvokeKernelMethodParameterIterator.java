package org.yah.tools.opencl.codegen.generator.kernel;

import com.github.javaparser.ast.body.Parameter;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

class InvokeKernelMethodParameterIterator implements Iterator<Parameter[]> {

    /**
     * for each kernel invoke method argument, the array of possible parameters for this argument
     */
    private final List<Parameter[]> argumentParameters;
    /**
     * current invoke method parameters
     */
    private final Parameter[] invokeDefinitions;

    private final int[] indices;

    public InvokeKernelMethodParameterIterator(List<Parameter[]> argumentParameters) {
        this.argumentParameters = argumentParameters;
        indices = new int[argumentParameters.size()];
        invokeDefinitions = new Parameter[argumentParameters.size()];
    }

    @Override
    public boolean hasNext() {
        return indices[0] < argumentParameters.get(0).length;
    }

    @Override
    public Parameter[] next() {
        if (!hasNext())
            throw new NoSuchElementException();
        for (int i = 0; i < indices.length; i++) {
            invokeDefinitions[i] = argumentParameters.get(i)[indices[i]];
        }

        for (int i = indices.length - 1; i >= 0; i--) {
            indices[i]++;
            if (i > 0 && indices[i] == argumentParameters.get(i).length) {
                indices[i] = 0;
            } else {
                break;
            }
        }
        return invokeDefinitions;
    }


}
