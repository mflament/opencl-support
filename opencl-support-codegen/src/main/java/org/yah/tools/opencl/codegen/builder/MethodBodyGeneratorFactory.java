package org.yah.tools.opencl.codegen.builder;

public interface MethodBodyGeneratorFactory<G> {

    boolean accept(Object generator);

    MethodBodyGenerator create(G generator);

}
