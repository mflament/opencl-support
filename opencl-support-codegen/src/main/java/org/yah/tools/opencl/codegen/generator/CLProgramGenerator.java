package org.yah.tools.opencl.codegen.generator;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import org.yah.tools.opencl.codegen.builder.ImplementationsBuilder;
import org.yah.tools.opencl.codegen.builder.JavaTypeBuilder;
import org.yah.tools.opencl.codegen.builder.MethodBodyGenerator;
import org.yah.tools.opencl.codegen.generator.kernel.CLKernelGenerator;
import org.yah.tools.opencl.codegen.generator.type.JavaTypeVariable;
import org.yah.tools.opencl.codegen.generator.type.JavaTypeVariables;
import org.yah.tools.opencl.codegen.naming.NamingStrategy.KernelImplementationNamingStrategy;
import org.yah.tools.opencl.codegen.naming.NamingStrategy.ProgramImplementationNamingStrategy;
import org.yah.tools.opencl.codegen.parser.ParsedKernel;
import org.yah.tools.opencl.codegen.parser.type.CLType;
import org.yah.tools.opencl.context.CLContext;
import org.yah.tools.opencl.enums.CommandQueueProperty;
import org.yah.tools.opencl.generated.AbstractGeneratedProgram;
import org.yah.tools.opencl.platform.CLDevice;
import org.yah.tools.opencl.program.CLCompilerOptions;

import javax.annotation.Nullable;
import java.util.*;

import static org.yah.tools.opencl.codegen.builder.MethodBodyBuilder.quote;

public class CLProgramGenerator {

    private final ProgramGeneratorRequest request;
    private final ProgramImplementationNamingStrategy namingStrategy;
    private final JavaTypeVariables typeVariables;
    private final Map<String, CLType> typeArguments;

    private final ImplementationsBuilder<CLProgramGenerator> implementationsBuilder;
    private final List<CompilationUnit> kernelImplementations = new ArrayList<>();

    public CLProgramGenerator(ProgramGeneratorRequest request,
                              ProgramImplementationNamingStrategy namingStrategy,
                              JavaTypeVariables typeVariables, Map<String, CLType> typeArguments) {
        this.request = Objects.requireNonNull(request, "request is null");
        this.namingStrategy = Objects.requireNonNull(namingStrategy, "namingStrategy is null");
        this.typeArguments = Objects.requireNonNull(typeArguments, "typeArguments is null");
        this.typeVariables = Objects.requireNonNull(typeVariables, "typeVariables is null");
        this.implementationsBuilder = new ImplementationsBuilder<>(namingStrategy.packageName(request.getBasePackage()),
                namingStrategy.className(), this, this::resolveTypeParameter);
    }

    public ClassOrInterfaceDeclaration getDeclaration() {
        return implementationsBuilder.getDeclaration();
    }

    public CompilationUnit getCompilationUnit() {
        return implementationsBuilder.getCompilationUnit();
    }

    public List<CompilationUnit> getKernelImplementations() {
        return kernelImplementations;
    }

    public List<CompilationUnit> generate(ClassOrInterfaceDeclaration interfaceDeclaration) {
        JavaTypeBuilder builder = implementationsBuilder;
        ClassOrInterfaceDeclaration declaration = builder.getDeclaration();

        builder.addImport(Nullable.class);

        CLCompilerOptions compilerOptions = new CLCompilerOptions(request.getCompilerOptions());
        typeArguments.forEach((name, type) -> compilerOptions.putMacro(name, type.toString()));

        builder.addPublicConstant(CLCompilerOptions.class, "DEFAULT_COMPILER_OPTIONS", "CLCompilerOptions.parse(%s)",
                quote(compilerOptions.toString()));

        builder.addPublicConstant(String.class, "PROGRAM_PATH", quote(request.getProgramPath()));

        declaration.addConstructor(Modifier.Keyword.PUBLIC)
                .addParameter(builder.addImport(CLContext.class), "context")
                .addParameter(nullableParameter(CLDevice.class, "device"))
                .addParameter(nullableParameter(CLCompilerOptions.class, "compilerOptions"))
                .addParameter(new Parameter(builder.addImport(CommandQueueProperty.class), "commandQueueProperties").setVarArgs(true))
                .setBody(new BlockStmt().addStatement("super(context, PROGRAM_PATH, device, compilerOptions, commandQueueProperties);"));

        declaration.addConstructor(Modifier.Keyword.PUBLIC)
                .addParameter(builder.addImport(CLContext.class), "context")
                .setBody(new BlockStmt().addStatement("this(context, null, DEFAULT_COMPILER_OPTIONS);"));

        declaration.addExtendedType(implementationsBuilder.addImport(AbstractGeneratedProgram.class));
        implementationsBuilder.implementInterface(interfaceDeclaration);

        List<CompilationUnit> compilationUnits = new ArrayList<>();
        compilationUnits.add(implementationsBuilder.getCompilationUnit());
        compilationUnits.addAll(kernelImplementations);
        return compilationUnits;
    }

    public String getBasePackage() {
        return request.getBasePackage();
    }

    public MethodBodyGenerator implementCreateKernel(ParsedKernel kernel, ClassOrInterfaceDeclaration kernelInterface) {
        KernelImplementationNamingStrategy kernelNamingStrategy = namingStrategy.kernelImplementation(kernel);
        CLKernelGenerator kernelGenerator = new CLKernelGenerator(kernel, getBasePackage(), kernelNamingStrategy, typeVariables, typeArguments);

        CompilationUnit kernelImplCu = kernelGenerator.generate(kernelInterface, implementationsBuilder.getDeclaration());
        kernelImplementations.add(kernelImplCu);

        ClassOrInterfaceDeclaration kernelImplementation = kernelImplCu.getType(0).asClassOrInterfaceDeclaration();
        implementationsBuilder.addImport(kernelImplementation);

        return mbb -> mbb
                .setReturnType(JavaTypeBuilder.createType(kernelImplementation))
                .andReturn("new %s(this, %s);", kernelImplementation.getNameAsString(), quote(kernel.getName()));
    }

    private Optional<Type> resolveTypeParameter(String javaTypeName) {
        return typeVariables.getVariables().stream()
                .filter(v -> v.getName().equals(javaTypeName))
                .findFirst()
                .map(this::createParameterArgument);
    }

    private ClassOrInterfaceType createParameterArgument(JavaTypeVariable javaTypeVariable) {
        return javaTypeVariable.createParameterArgument(typeArguments, implementationsBuilder);
    }

    private Parameter nullableParameter(Class<?> type, String name) {
        return new Parameter(implementationsBuilder.addImport(type), name).addMarkerAnnotation(Nullable.class);
    }

}
