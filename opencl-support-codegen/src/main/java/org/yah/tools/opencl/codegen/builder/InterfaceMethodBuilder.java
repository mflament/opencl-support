package org.yah.tools.opencl.codegen.builder;

import com.github.javaparser.ast.DataKey;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.type.Type;

import java.lang.annotation.Annotation;
import java.util.function.Function;

public class InterfaceMethodBuilder {

    public static MethodBodyGeneratorFactories getImplementationGenerators(MethodDeclaration methodDeclaration) {
        if (methodDeclaration.containsData(IMPLEMENTATION_GENERATORS))
            return methodDeclaration.getData(IMPLEMENTATION_GENERATORS);
        return MethodBodyGeneratorFactories.empty();
    }

    private final MethodDeclaration methodDeclaration;
    private final MethodBodyGeneratorFactories methodBodyGeneratorFactories = new MethodBodyGeneratorFactories();

    public InterfaceMethodBuilder(MethodDeclaration methodDeclaration) {
        this.methodDeclaration = methodDeclaration.removeBody();
        methodDeclaration.removeBody().setData(IMPLEMENTATION_GENERATORS, methodBodyGeneratorFactories);
    }

    public <G> InterfaceMethodBuilder implementedBy(Class<G> generatorType, Function<G, MethodBodyGenerator> generatorFactory) {
        methodBodyGeneratorFactories.add(defaultBodyGeneratorFactory(generatorType, generatorFactory));
        return this;
    }

    public <G> InterfaceMethodBuilder implementedBy(MethodBodyGeneratorFactory<G> generatorFactory) {
        methodBodyGeneratorFactories.add(generatorFactory);
        return this;
    }

    public InterfaceMethodBuilder addAnnotation(Class<? extends Annotation> clazz) {
        methodDeclaration.addAnnotation(clazz);
        return this;
    }

    public void addMarkerAnnotation(Class<? extends Annotation> clazz) {
        methodDeclaration.addMarkerAnnotation(clazz);
    }

    public InterfaceMethodBuilder addParameter(Type astType, String name) {
        methodDeclaration.addParameter(astType, name);
        return this;
    }

    public InterfaceMethodBuilder addParameter(Class<?> javaType, String name) {
        methodDeclaration.addParameter(javaType, name);
        return this;
    }

    public InterfaceMethodBuilder addParameter(Parameter parameter) {
        methodDeclaration.addParameter(parameter);
        return this;
    }

    public void setDefault(MethodBodyGenerator bodyGenerator) {
        MethodBodyBuilder methodBodyBuilder = new MethodBodyBuilder(methodDeclaration);
        methodDeclaration.setDefault(true).setBody(bodyGenerator.generate(methodBodyBuilder));
    }

    public InterfaceMethodBuilder withJavaDoc(String doc) {
        methodDeclaration.setJavadocComment(doc);
        return this;
    }

    private static final DataKey<MethodBodyGeneratorFactories> IMPLEMENTATION_GENERATORS = new DataKey<MethodBodyGeneratorFactories>() {
    };

    private static <G> MethodBodyGeneratorFactory<G> defaultBodyGeneratorFactory(Class<G> generatorType, Function<G, MethodBodyGenerator> factory) {
        return new MethodBodyGeneratorFactory<G>() {
            @Override
            public boolean accept(Object generator) {
                return generatorType.isInstance(generator);
            }

            @Override
            public MethodBodyGenerator create(G generator) {
                return factory.apply(generator);
            }
        };
    }

    public InterfaceMethodBuilder setType(Type type) {
        methodDeclaration.setType(type);
        return this;
    }
}
