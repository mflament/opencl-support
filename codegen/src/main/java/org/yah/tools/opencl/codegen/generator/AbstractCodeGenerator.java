package org.yah.tools.opencl.codegen.generator;

import com.github.javaparser.ast.*;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import org.yah.tools.opencl.codegen.model.kernel.KernelMethod;
import org.yah.tools.opencl.codegen.model.kernel.KernelMethodParameter;
import org.yah.tools.opencl.codegen.model.kernel.KernelModel;
import org.yah.tools.opencl.codegen.model.program.ProgramMethod;
import org.yah.tools.opencl.codegen.model.program.ProgramModel;

import java.util.Objects;

public abstract class AbstractCodeGenerator<T> {

    protected final static ClassOrInterfaceType STRING_TYPE = new ClassOrInterfaceType(null, String.class.getSimpleName());

    protected final T source;
    protected final String packageName;
    protected final String simpleName;

    protected final CompilationUnit compilationUnit;
    protected final ClassOrInterfaceDeclaration declaration;

    protected final Type thisType;

    public AbstractCodeGenerator(T source, String packageName, String simpleName) {
        this.source = Objects.requireNonNull(source, "source is null");
        this.packageName = Objects.requireNonNull(packageName, "packageName is null");
        this.simpleName = Objects.requireNonNull(simpleName, "simpleName is null");

        compilationUnit = new CompilationUnit(packageName);
        declaration = new ClassOrInterfaceDeclaration()
                .setName(simpleName)
                .setModifiers(Modifier.Keyword.PUBLIC);
        compilationUnit.addType(declaration);
        thisType = new ClassOrInterfaceType(null, simpleName);
    }

    public abstract CompilationUnit generate();

    protected final void addImplementedType(CompilationUnit interfaceCompilationUnit) {
        ClassOrInterfaceType interfaceType = addImport(interfaceCompilationUnit);
        declaration.addImplementedType(interfaceType);
    }

    protected final ClassOrInterfaceType addImport(Class<?> type) {
        compilationUnit.addImport(type);
        return new ClassOrInterfaceType(null, type.getSimpleName());
    }

    protected final ClassOrInterfaceType addImport(CompilationUnit cu) {
        String packageName = cu.getPackageDeclaration()
                .map(PackageDeclaration::getNameAsString).orElseThrow(IllegalArgumentException::new);
        String simpleName = cu.getType(0).getNameAsString();
        compilationUnit.addImport(packageName + "." + simpleName);
        return new ClassOrInterfaceType(null, simpleName);
    }

    protected final MethodDeclaration implementsMethod(MethodDeclaration interfaceMethod) {
        CompilationUnit interfaceCu = interfaceMethod.findCompilationUnit().orElseThrow(IllegalStateException::new);
        Type returnType = resolveType(interfaceCu, interfaceMethod.getType());

        MethodDeclaration methodDeclaration = declaration.addMethod(interfaceMethod.getNameAsString(), Modifier.Keyword.PUBLIC)
                .setType(returnType)
                .addMarkerAnnotation(Override.class);
        interfaceMethod.getParameters().stream()
                .map(p -> new Parameter(resolveType(interfaceCu, p.getType()), p.getName()).setVarArgs(p.isVarArgs()))
                .forEach(methodDeclaration::addParameter);
        return methodDeclaration;
    }

    private Type resolveType(CompilationUnit interfaceCu, Type type) {
        if (type.isPrimitiveType())
            return type;
        String simpleName = type.asClassOrInterfaceType().getNameAsString();
        String qualifiedName;
        if (interfaceCu.getType(0).getNameAsString().equals(simpleName)) {
            String packageName = interfaceCu.getPackageDeclaration().orElseThrow(IllegalArgumentException::new).getNameAsString();
            qualifiedName = packageName + "." + simpleName;
        } else {
            qualifiedName = interfaceCu.getImports().stream()
                    .map(ImportDeclaration::getNameAsString)
                    .filter(name -> name.endsWith(simpleName))
                    .findFirst().orElseThrow(() -> new IllegalArgumentException("Unresolved type " + type + " in imports " + interfaceCu.getImports()));
        }
        compilationUnit.addImport(qualifiedName);
        return new ClassOrInterfaceType(null, simpleName);
    }


    protected final void addPublicConstant(Class<?> type, String name, String stmt) {
        ClassOrInterfaceType astType = addImport(type);
        FieldDeclaration fieldDeclaration = declaration.addField(astType, name, Modifier.Keyword.PUBLIC, Modifier.Keyword.STATIC, Modifier.Keyword.FINAL);
        fieldDeclaration.getVariable(0).setInitializer(stmt);
    }

    public static final DataKey<ProgramModel> PROGRAM_MODEL = new DataKey<ProgramModel>() {
    };

    public static final DataKey<KernelModel> KERNEL_MODEL = new DataKey<KernelModel>() {
    };

    public static final DataKey<ProgramMethod> PROGRAM_METHOD = new DataKey<ProgramMethod>() {
    };

    public static final DataKey<KernelMethod> KERNEL_METHOD = new DataKey<KernelMethod>() {
    };

    public static final DataKey<KernelMethodParameter> KERNEL_METHOD_PARAMETER = new DataKey<KernelMethodParameter>() {
    };

    protected static <T> T getData(CompilationUnit compilationUnit, DataKey<T> key) {
        return compilationUnit.getType(0).getData(key);
    }

    protected static BlockStmt buildBlockStmt(String... statements) {
        BlockStmt blockStmt = new BlockStmt();
        for (String statement : statements) {
            if (!statement.endsWith(";"))
                statement += ";";
            blockStmt.addStatement(statement);
        }
        return blockStmt;
    }

    protected static String quote(String s) {
        if (s == null) return "\"\"";
        return '"' + s + '"';
    }

}
