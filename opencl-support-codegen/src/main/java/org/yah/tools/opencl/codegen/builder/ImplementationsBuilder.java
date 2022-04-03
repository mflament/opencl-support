package org.yah.tools.opencl.codegen.builder;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.TypeParameter;

import java.util.List;
import java.util.Objects;

public class ImplementationsBuilder<G> extends JavaTypeBuilder {

    private final G generator;

    public ImplementationsBuilder(String packageName, String simpleName, G generator, TypeParameterResolver typeParameterResolver) {
        super(packageName, simpleName, typeParameterResolver);
        this.generator = Objects.requireNonNull(generator, "generator is null");
    }

    public void implementInterface(ClassOrInterfaceDeclaration interfaceDeclaration) {
        ClassOrInterfaceType implementedType = addImport(interfaceDeclaration);
        // add type parameters
        NodeList<TypeParameter> impemenentationTypeParameters = new NodeList<>();
        NodeList<Type> implementedTypeArguments = new NodeList<>();
        if (interfaceDeclaration.getTypeParameters().isNonEmpty()) {
            for (TypeParameter typeParameter : interfaceDeclaration.getTypeParameters()) {
                String typeParameterName = typeParameter.getNameAsString();
                Type resolvedType = resolveTypeArgument(typeParameterName).orElse(null);
                if (resolvedType == null) {
                    // for unresolved type variables, create a TypeParameter on the implementation declaration
                    impemenentationTypeParameters.add(new TypeParameter(typeParameterName));
                    implementedTypeArguments.add(new TypeParameter(typeParameterName));
                } else {
                    // for resolved type variables, set the type argument of the implementation
                    implementedTypeArguments.add(resolvedType);
                }
            }
            if (impemenentationTypeParameters.isNonEmpty())
                declaration.setTypeParameters(impemenentationTypeParameters);
            if (implementedTypeArguments.isNonEmpty())
                implementedType.setTypeArguments(implementedTypeArguments);
        }
        declaration.addImplementedType(implementedType);

        for (MethodDeclaration md : interfaceDeclaration.getMethods()) {
            List<MethodBodyGeneratorFactory<G>> factories = getMethodGeneratorFactories(md);
            factories.stream()
                    .map(f -> f.create(generator))
                    .forEach(mg -> implementMethod(interfaceDeclaration, md, mg));
        }
    }

    private List<MethodBodyGeneratorFactory<G>> getMethodGeneratorFactories(MethodDeclaration md) {
        MethodBodyGeneratorFactories generators = InterfaceMethodBuilder.getImplementationGenerators(md);
        return generators.find(generator);
    }

    private void implementMethod(ClassOrInterfaceDeclaration interfaceDeclaration,
                                 MethodDeclaration interfaceMethodDeclaration,
                                 MethodBodyGenerator methodGenerator) {
        MethodDeclaration methodDeclaration = declaration.addMethod(interfaceMethodDeclaration.getNameAsString(), Modifier.Keyword.PUBLIC);
        methodDeclaration
                .setParameters(createMethodParameters(interfaceDeclaration, interfaceMethodDeclaration))
                .setBody(methodGenerator.generate(new MethodBodyBuilder(methodDeclaration)));
        if (methodDeclaration.getType().isVoidType() && !interfaceMethodDeclaration.getType().isVoidType())
            methodDeclaration.setType(resolveFrom(interfaceDeclaration, interfaceMethodDeclaration.getType()));
    }

    private NodeList<Parameter> createMethodParameters(ClassOrInterfaceDeclaration interfaceDeclaration, MethodDeclaration interfaceMethodDeclaration) {
        return interfaceMethodDeclaration.getParameters().stream()
                .map(p -> createMethodParameter(interfaceDeclaration, p))
                .collect(toNodeList());
    }

    private Parameter createMethodParameter(ClassOrInterfaceDeclaration interfaceDeclaration, Parameter interfaceParameter) {
        Type parameterType = resolveFrom(interfaceDeclaration, interfaceParameter.getType());
        if (parameterType.isTypeParameter())
            parameterType = resolveTypeArgument(parameterType.asTypeParameter().getNameAsString()).orElse(parameterType);

        Parameter newParam = new Parameter(parameterType, interfaceParameter.getName()).setVarArgs(interfaceParameter.isVarArgs());

        interfaceParameter.getAnnotations().forEach(a -> {
            resolveFrom(interfaceDeclaration, new ClassOrInterfaceType(null, a.getNameAsString()));
            if (a.isMarkerAnnotationExpr())
                newParam.addMarkerAnnotation(a.getNameAsString());
            else
                newParam.addAnnotation(a.getNameAsString());
        });

        return newParam;
    }

}
