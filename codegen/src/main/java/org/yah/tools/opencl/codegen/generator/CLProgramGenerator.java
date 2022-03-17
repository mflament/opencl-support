package org.yah.tools.opencl.codegen.generator;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.stmt.BlockStmt;
import org.yah.tools.opencl.codegen.model.kernel.KernelModel;
import org.yah.tools.opencl.codegen.model.program.ProgramModel;
import org.yah.tools.opencl.codegen.parser.model.ParsedProgram;
import org.yah.tools.opencl.context.CLContext;
import org.yah.tools.opencl.enums.CommandQueueProperty;
import org.yah.tools.opencl.generated.AbstractGeneratedProgram;
import org.yah.tools.opencl.platform.CLDevice;
import org.yah.tools.opencl.program.CLCompilerOptions;

import javax.annotation.Nullable;

public class CLProgramGenerator extends AbstractCodeGenerator<CompilationUnit> {

    public CLProgramGenerator(CompilationUnit programInterface) {
        super(programInterface, getPackageName(programInterface), getTypeName(programInterface));
        declaration.addExtendedType(addImport(AbstractGeneratedProgram.class));
        addImplementedType(programInterface);
    }

    @Override
    public CompilationUnit generate() {
        createConstructor();
        source.getType(0).getMethods().stream()
                .filter(md -> md.containsData(KERNEL_MODEL))
                .forEach(this::implementsCreateKernel);
        return compilationUnit;
    }

    private void createConstructor() {
        compilationUnit.addImport(Nullable.class);
        ProgramModel programModel = getData(source, PROGRAM_MODEL);
        ParsedProgram parsedProgram = programModel.getParsedProgram();

        CLCompilerOptions options = new CLCompilerOptions(parsedProgram.getCompilerOptions()).withoutKernelArgInfo();
        addPublicConstant(CLCompilerOptions.class, "DEFAULT_COMPILER_OPTIONS", "CLCompilerOptions.parse(" + quote(options.toString()) + ")");
        addPublicConstant(String.class, "PROGRAM_PATH", quote(parsedProgram.getFilePath()));

        declaration.addConstructor(Modifier.Keyword.PUBLIC)
                .addParameter(new Parameter(addImport(CLContext.class), "context"))
                .addParameter(new Parameter(addImport(CLDevice.class), "device").addMarkerAnnotation(Nullable.class))
                .addParameter(new Parameter(addImport(CLCompilerOptions.class), "compilerOptions").addMarkerAnnotation(Nullable.class))
                .addParameter(new Parameter(addImport(CommandQueueProperty.class), "commandQueueProperties").setVarArgs(true))
                .setBody(buildBlockStmt("super(context, PROGRAM_PATH, device, compilerOptions, commandQueueProperties);"));

        declaration.addConstructor(Modifier.Keyword.PUBLIC)
                .addParameter(new Parameter(addImport(CLContext.class), "context"))
                .setBody(buildBlockStmt("this(context, null, DEFAULT_COMPILER_OPTIONS);"));
    }

    private void implementsCreateKernel(MethodDeclaration md) {
        KernelModel kernelModel = md.getData(KERNEL_MODEL);

        String kernelName = kernelModel.getParsedKernel().getName();
        String packageName = CLKernelGenerator.getPackageName(kernelModel.getProgramModel().getBasePackage());
        String className = CLKernelGenerator.getTypeName(kernelModel.getName());
        compilationUnit.addImport(packageName + "." + className);
        implementsMethod(md).setBody(new BlockStmt().addStatement(String.format("return new %s(this, \"%s\");", className, kernelName)));
    }

    private static String getPackageName(CompilationUnit compilationUnit) {
        ProgramModel programModel = getData(compilationUnit, PROGRAM_MODEL);
        return programModel.getBasePackage() + ".cl";
    }

    private static String getTypeName(CompilationUnit programInterface) {
        ProgramModel programModel = getData(programInterface, PROGRAM_MODEL);
        return "CL" + programModel.getName();
    }
}
