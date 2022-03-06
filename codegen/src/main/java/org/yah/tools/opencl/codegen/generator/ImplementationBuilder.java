package org.yah.tools.opencl.codegen.generator;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithName;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import org.yah.tools.opencl.codegen.generator.impl.BlockStmtBuilder;
import org.yah.tools.opencl.generated.TypeName;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static org.yah.tools.opencl.codegen.generator.impl.CodeGeneratorSupport.body;

@SuppressWarnings("UnusedReturnValue")
public class ImplementationBuilder {

    private static final NodeList<Modifier> INTERFACE_MODIFIERS = NodeList.nodeList(Modifier.publicModifier(), Modifier.finalModifier());

    private final TypeName typeName;
    private final CompilationUnit compilationUnit;
    private final ClassOrInterfaceDeclaration declaration;
    private final CompilationUnit interfaceCompilationUnit;
    private final List<Parameter> constructorParameters = new ArrayList<>();

    public ImplementationBuilder(@Nullable String packageName, String simpleName, CompilationUnit interfaceCompilationUnit) {
        this(new TypeName(packageName, simpleName), interfaceCompilationUnit);
    }

    public ImplementationBuilder(TypeName typeName, CompilationUnit interfaceCompilationUnit) {
        this.typeName = Objects.requireNonNull(typeName, "typeName is null");
        this.interfaceCompilationUnit = interfaceCompilationUnit;
        compilationUnit = typeName.getPackageName().map(CompilationUnit::new).orElseGet(CompilationUnit::new);
        TypeDeclaration<?> interfaceDeclaration = getInterfaceDeclaration();
        interfaceDeclaration.getFullyQualifiedName()
                .ifPresent(compilationUnit::addImport);

        declaration = new ClassOrInterfaceDeclaration(INTERFACE_MODIFIERS, false, typeName.getSimpleName())
                .addImplementedType(new ClassOrInterfaceType(null, interfaceDeclaration.getNameAsString()));
        compilationUnit.addType(declaration);
    }

    public ImplementationBuilder addField(Class<?> type, String name, boolean isFinal) {
        compilationUnit.addImport(type);
        ClassOrInterfaceType dependencyType = new ClassOrInterfaceType(null, type.getSimpleName());
        FieldDeclaration fieldDeclaration = declaration.addField(dependencyType, name, Modifier.Keyword.PRIVATE);
        if (isFinal) {
            fieldDeclaration.addModifier(Modifier.Keyword.FINAL);
            constructorParameters.add(new Parameter(dependencyType, name));
        }
        return this;
    }

    public ImplementationBuilder addConstructor() {
        compilationUnit.addImport(Objects.class);
        ConstructorDeclaration ctr = declaration.addConstructor(Modifier.Keyword.PUBLIC)
                .addParameter(constructorParameters.get(0))
                .addParameter(constructorParameters.get(1))
                .setBody(body(builder ->
                        constructorParameters.stream().map(Parameter::getNameAsString)
                                .forEach(n -> builder.withStatement("this.%1$s = Objects.requireNonNull(%1$s, \"%1$s is null\");", n))
                ));
        return this;
    }

    public ImplementationBuilder addMethod(String name, Consumer<MethodDeclaration> builder, Modifier.Keyword... keywords) {
        MethodDeclaration methodDeclaration = declaration.addMethod(name, keywords);
        builder.accept(methodDeclaration);
        return this;
    }

    public ImplementationBuilder implementMethods(BiConsumer<MethodDeclaration, BlockStmtBuilder> implementer) {
        getInterfaceDeclaration().getMethods().stream()
                .filter(d -> !d.isDefault())
                .forEach(m -> implementMethod(m, implementer));
        return this;
    }

    private void implementMethod(MethodDeclaration methodDeclaration, BiConsumer<MethodDeclaration, BlockStmtBuilder> implementer) {
        CompilationUnit interfaceCu = methodDeclaration.findCompilationUnit().orElseThrow(IllegalStateException::new);

        Type methodType = methodDeclaration.getType();
        if (methodType.isClassOrInterfaceType())
            compilationUnit.addImport(resolveFullyQualifiedName(interfaceCu, methodType.asClassOrInterfaceType()));

        methodDeclaration.getParameters().stream()
                .map(Parameter::getType)
                .filter(Type::isClassOrInterfaceType)
                .map(Type::asClassOrInterfaceType)
                .forEach(coit -> compilationUnit.addImport(resolveFullyQualifiedName(interfaceCu, coit)));

        MethodDeclaration method = declaration.addMethod(methodDeclaration.getNameAsString(), Modifier.Keyword.PUBLIC)
                .addMarkerAnnotation(Override.class)
                .setType(methodType);
        methodDeclaration.getParameters().stream()
                .map(Parameter::clone)
                .forEach(method::addParameter);
        method.setBody(body(builder -> implementer.accept(methodDeclaration, builder)));
    }

    private String resolveFullyQualifiedName(CompilationUnit interfaceCu, ClassOrInterfaceType type) {
        String typeName = type.getNameAsString();

        TypeDeclaration<?> interfaceDeclaration = interfaceCu.getType(0);
        if (typeName.equals(interfaceDeclaration.getNameAsString()))
            return interfaceDeclaration.getFullyQualifiedName().orElseThrow(IllegalStateException::new);

        return interfaceCu.getImports().stream()
                .map(NodeWithName::getNameAsString)
                .filter(iname -> iname.endsWith(typeName))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(typeName + " not found in\n" + interfaceCu.getImports()));
    }

    public CompilationUnit build() {
        return compilationUnit;
    }

    private ClassOrInterfaceDeclaration getInterfaceDeclaration() {
        return interfaceCompilationUnit.getType(0).asClassOrInterfaceDeclaration();
    }

    public void addImport(String s) {
        String importName;
        if (s.startsWith("."))
            importName = typeName.getPackageName().map(p -> p + s).orElseGet(() -> s.substring(1));
        else
            importName = s;
        compilationUnit.addImport(importName);
    }

    public void addImport(Class<?> c) {
        compilationUnit.addImport(c);
    }

    public String getInterfaceName() {
        return interfaceCompilationUnit.getType(0).getNameAsString();
    }

    public ImplementationBuilder addStringConstant(boolean pub, String name, String value) {
        return addConstant(pub, String.class, name, new StringLiteralExpr(value));
    }

    public ImplementationBuilder addConstant(boolean pub, Class<?> type, String name, Expression expression) {
        compilationUnit.addImport(type);
        declaration.addFieldWithInitializer(type, name, expression, pub ? Modifier.Keyword.PUBLIC : Modifier.Keyword.PRIVATE,
                Modifier.Keyword.STATIC, Modifier.Keyword.FINAL);
        return this;
    }

}
