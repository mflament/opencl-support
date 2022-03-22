package org.yah.tools.opencl.codegen.generator;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.TypeParameter;
import org.yah.tools.opencl.codegen.parser.type.CLType;
import org.yah.tools.opencl.codegen.parser.type.ScalarDataType;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

public abstract class AbstractImplementationGenerator extends AbstractCodeGenerator<CompilationUnit> {

    protected final Map<String, CLType> typeArguments;

    public AbstractImplementationGenerator(CompilationUnit source, String packageName, String simpleName, Map<String, CLType> typeArguments) {
        super(source, packageName, simpleName);
        this.typeArguments = Objects.requireNonNull(typeArguments, "typeArguments is null");

        ClassOrInterfaceDeclaration interfaceDeclaration = source.getType(0).asClassOrInterfaceDeclaration();

        ClassOrInterfaceType interfaceType = addImport(source);
        NodeList<TypeParameter> typeParameters = interfaceDeclaration.getTypeParameters();
        if (!typeParameters.isEmpty()) {
            interfaceType.setTypeArguments(typeParameters.stream()
                    .map(tpm -> resolveTypeParameter(tpm.getNameAsString()))
                    .collect(toNodeList()));
        }
        declaration.addImplementedType(interfaceType);
    }

    protected final MethodDeclaration implementMethod(MethodDeclaration interfaceMethod) {
        Type returnType = interfaceMethod.getType();
        if (returnType.isClassOrInterfaceType())
            returnType = importInterfaceTypeReference(returnType.asClassOrInterfaceType());

        MethodDeclaration methodDeclaration = declaration.addMethod(interfaceMethod.getNameAsString(), Modifier.Keyword.PUBLIC)
                .setType(returnType)
                .addMarkerAnnotation(Override.class);
        interfaceMethod.getParameters().stream()
                .map(this::createMethodImplementationParameter)
                .forEach(methodDeclaration::addParameter);
        return methodDeclaration;
    }

    private Parameter createMethodImplementationParameter(Parameter p) {
        Type parameterType = p.getType();
        Type newType;
        if (parameterType.isClassOrInterfaceType()) {
            newType = importInterfaceTypeReference(parameterType.asClassOrInterfaceType());
        } else if (parameterType.isTypeParameter()) {
            String typeName = parameterType.asTypeParameter().getNameAsString();
            newType = resolveTypeParameter(typeName);
        } else {
            newType = parameterType;
        }
        return new Parameter(newType, p.getName()).setVarArgs(p.isVarArgs());
    }

    protected final Type resolveTypeParameter(String typeName) {
        CLType type = typeArguments.get(typeName);
        if (type == null)
            throw new NoSuchElementException(typeName);

        boolean buffer = type.isPointer() || type.isVector() || type.isUnresolved();
        CLType componentType = type.getComponentType();
        if (!componentType.isScalar())
            throw new IllegalArgumentException("Invalid type parameter argument " + typeName + " " + type);

        ScalarDataType scalarDataType = componentType.asScalar();
        return buffer ? resolveBufferType(scalarDataType) : resolveScalarClass(scalarDataType);
    }

    private ClassOrInterfaceType importInterfaceTypeReference(ClassOrInterfaceType type) {
        String simpleName = type.asClassOrInterfaceType().getNameAsString();
        String qualifiedName;
        if (source.getType(0).getNameAsString().equals(simpleName)) {
            String packageName = source.getPackageDeclaration().orElseThrow(IllegalArgumentException::new).getNameAsString();
            qualifiedName = packageName + "." + simpleName;
        } else {
            qualifiedName = source.getImports().stream()
                    .map(ImportDeclaration::getNameAsString)
                    .filter(name -> name.endsWith(simpleName))
                    .findFirst().orElseThrow(() -> new IllegalArgumentException("Unresolved type " + type + " in imports " + source.getImports()));
        }
        compilationUnit.addImport(qualifiedName);
        return new ClassOrInterfaceType(null, simpleName);
    }

    protected static ClassOrInterfaceType resolveScalarClass(ScalarDataType parameterType) {
        Class<?> clazz;
        switch (parameterType) {
            case BOOL:
                clazz = Boolean.class;
                break;
            case SHORT:
            case USHORT:
            case HALF:
                clazz = Short.class;
                break;
            case INT:
            case UINT:
                clazz = Integer.class;
                break;
            case FLOAT:
                clazz = Float.class;
                break;
            case DOUBLE:
                clazz = Double.class;
                break;
            case LONG:
            case ULONG:
            case SIZE_T:
            case PTRDIFF_T:
            case INTPTR_T:
            case UINTPTR_T:
                clazz = Long.class;
                break;
            default:
                clazz = Byte.class;
                break;
        }
        return new ClassOrInterfaceType(null, clazz.getSimpleName());
    }

    protected static int getScalarSize(ScalarDataType type) {
        switch (type) {
            case BOOL:
            case INT:
            case UINT:
            case FLOAT:
                return 4;
            case SHORT:
            case USHORT:
            case HALF:
                return 2;
            case DOUBLE:
            case LONG:
            case ULONG:
            case SIZE_T:
            case PTRDIFF_T:
            case INTPTR_T:
            case UINTPTR_T:
                return 8;
            default:
                return 1;
        }
    }

}
