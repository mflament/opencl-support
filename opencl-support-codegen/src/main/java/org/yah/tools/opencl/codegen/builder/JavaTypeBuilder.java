package org.yah.tools.opencl.codegen.builder;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.*;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.TypeParameter;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class JavaTypeBuilder {

    @FunctionalInterface
    public interface TypeParameterResolver {
        Optional<Type> resolve(String typeParameterName);
    }

    protected final CompilationUnit compilationUnit;
    protected final ClassOrInterfaceDeclaration declaration;

    protected final TypeParameterResolver typeParameterResolver;

    public JavaTypeBuilder(String packageName, String simpleName) {
        this(packageName, simpleName, null);
    }

    public JavaTypeBuilder(String packageName, String simpleName, @Nullable TypeParameterResolver typeParameterResolver) {
        compilationUnit = new CompilationUnit(packageName);
        declaration = new ClassOrInterfaceDeclaration()
                .setName(simpleName)
                .setModifiers(Modifier.Keyword.PUBLIC);
        compilationUnit.addType(declaration);
        this.typeParameterResolver = typeParameterResolver != null ? typeParameterResolver : defaultTypeResolver();
    }

    public ClassOrInterfaceDeclaration getDeclaration() {
        return declaration;
    }

    public CompilationUnit getCompilationUnit() {
        return compilationUnit;
    }

    public boolean isGeneric() {
        return !declaration.getTypeParameters().isEmpty();
    }

    public List<TypeParameter> getTypeParameters() {
        return declaration.getTypeParameters();
    }

    public void addTypeParameter(String name) {
        addTypeParameter(new TypeParameter(name));
    }

    public void addTypeParameter(TypeParameter parameter) {
        NodeList<TypeParameter> typeParameters = declaration.getTypeParameters();
        typeParameters.add(parameter);
    }

    public void addExtendedType(ClassOrInterfaceType type) {
        declaration.addExtendedType(type);
    }

    public CompilationUnit build() {
        return compilationUnit;
    }

    public String getSimpleName() {
        return declaration.getNameAsString();
    }

    public void withJavaDoc(String doc) {
        declaration.setJavadocComment(doc);
    }

    public MethodDeclaration addMethod(String name, Modifier.Keyword... keywords) {
        return declaration.addMethod(name, keywords);
    }

    public Type getThisType() {
        return createType(declaration);
    }

    public ClassOrInterfaceType addImport(String fqn) {
        int index = fqn.lastIndexOf('.');
        if (index < 0)
            throw new IllegalArgumentException("No package for " + fqn);

        String packageName = fqn.substring(0, index);
        String typeName = fqn.substring(index + 1);
        ParseResult<ClassOrInterfaceType> parseResult = new JavaParser().parseClassOrInterfaceType(typeName);
        if (!parseResult.isSuccessful() || !parseResult.getResult().isPresent())
            throw new IllegalArgumentException("Invalid type " + parseResult.getProblems());
        ClassOrInterfaceType type = parseResult.getResult().get();
        compilationUnit.addImport(packageName + "." + type.getNameAsString());
        return type;
    }

    public ClassOrInterfaceType addImport(Class<?> type) {
        compilationUnit.addImport(type);
        return new ClassOrInterfaceType(null, type.getSimpleName());
    }

    public String getPackageName() {
        return compilationUnit.getPackageDeclaration()
                .map(PackageDeclaration::getNameAsString)
                .orElseThrow(() -> new IllegalStateException("package name not defined"));
    }

    public ClassOrInterfaceType addImport(ClassOrInterfaceDeclaration declaration) {
        CompilationUnit declarationCompilationUnit = declaration.findCompilationUnit().orElseThrow(NoSuchElementException::new);
        String packageName = declarationCompilationUnit.getPackageDeclaration()
                .map(PackageDeclaration::getNameAsString).orElseThrow(IllegalArgumentException::new);
        String simpleName = declaration.getNameAsString();
        compilationUnit.addImport(packageName + "." + simpleName);
        declaration.getTypeParameters().forEach(coit -> resolveFrom(declaration, coit));
        return new ClassOrInterfaceType(null, simpleName);
    }

    public Type resolveFrom(ClassOrInterfaceDeclaration declaration, Type type) {
        if (type.isClassOrInterfaceType()) {
            ClassOrInterfaceType classOrInterfaceType = type.asClassOrInterfaceType();
            String simpleName = classOrInterfaceType.getNameAsString();

            ClassOrInterfaceType resolvedType = new ClassOrInterfaceType(null, simpleName);
            if (declaration.getNameAsString().equals(simpleName)) {
                String fqn = declaration.getFullyQualifiedName().orElseThrow(NoSuchElementException::new);
                compilationUnit.addImport(fqn);
            } else {
                NodeList<ImportDeclaration> interfaceImports = declaration.findCompilationUnit().orElseThrow(NoSuchElementException::new).getImports();
                interfaceImports.stream()
                        .map(ImportDeclaration::getNameAsString)
                        .filter(fqn -> fqn.endsWith(simpleName))
                        .findFirst().ifPresent(compilationUnit::addImport);
            }
            classOrInterfaceType.getTypeArguments().map(this::resolveTypeArguments).ifPresent(resolvedType::setTypeArguments);
            return resolvedType;
        }
        return type;
    }

    public Type resolveTypeArgument(Type type) {
        if (type.isTypeParameter())
            return resolveTypeArgument(type.asTypeParameter().getNameAsString()).orElse(type);
        return type;
    }

    public Optional<Type> resolveTypeArgument(String name) {
        return typeParameterResolver.resolve(name);
    }

    public void addPublicConstant(Class<?> type, String name, String stmt, Object... stmtArgs) {
        ClassOrInterfaceType astType = addImport(type);
        FieldDeclaration fieldDeclaration = declaration.addField(astType, name, Modifier.Keyword.PUBLIC, Modifier.Keyword.STATIC, Modifier.Keyword.FINAL);
        if (stmtArgs.length > 0)
            stmt = String.format(stmt, stmtArgs);
        fieldDeclaration.getVariable(0).setInitializer(stmt);
    }

    private NodeList<Type> resolveTypeArguments(NodeList<Type> args) {
        return args.stream().map(this::resolveTypeArgument).collect(toNodeList());
    }

    public static ClassOrInterfaceType createType(ClassOrInterfaceDeclaration declaration) {
        ClassOrInterfaceType type = new ClassOrInterfaceType(null, declaration.getNameAsString());
        NodeList<TypeParameter> typeParameters = declaration.getTypeParameters();
        if (typeParameters.isNonEmpty()) {
            type.setTypeArguments(typeParameters.stream()
                    .map(t -> new TypeParameter(t.getNameAsString()))
                    .collect(toNodeList()));
        }
        return type;
    }

    public static <N extends Node> Collector<N, NodeList<N>, NodeList<N>> toNodeList() {
        return new Collector<N, NodeList<N>, NodeList<N>>() {
            @Override
            public Supplier<NodeList<N>> supplier() {
                return NodeList::new;
            }

            @Override
            public BiConsumer<NodeList<N>, N> accumulator() {
                return NodeList::add;
            }

            @Override
            public BinaryOperator<NodeList<N>> combiner() {
                return (l1, l2) -> {
                    l1.addAll(l2);
                    return l1;
                };
            }

            @Override
            public Function<NodeList<N>, NodeList<N>> finisher() {
                return l -> l;
            }

            @Override
            public Set<Characteristics> characteristics() {
                return EnumSet.noneOf(Characteristics.class);
            }
        };
    }

    private static TypeParameterResolver defaultTypeResolver() {
        return typeParameterName -> Optional.empty();
    }

}
