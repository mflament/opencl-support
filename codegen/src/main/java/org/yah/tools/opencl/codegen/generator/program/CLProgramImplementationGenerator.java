package org.yah.tools.opencl.codegen.generator.program;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.yah.tools.opencl.cmdqueue.CLCommandQueue;
import org.yah.tools.opencl.codegen.generator.*;
import org.yah.tools.opencl.codegen.generator.impl.CodeGeneratorSupport;
import org.yah.tools.opencl.codegen.generator.impl.BlockStmtBuilder;
import org.yah.tools.opencl.codegen.generator.kernel.CLKernelImplementationGenerator;
import org.yah.tools.opencl.codegen.generator.model.old.KernelImplementation;
import org.yah.tools.opencl.codegen.generator.model.old.KernelInterface;
import org.yah.tools.opencl.codegen.generator.model.old.ProgramImplementation;
import org.yah.tools.opencl.codegen.generator.model.old.ProgramInterface;
import org.yah.tools.opencl.codegen.parser.model.ParsedProgram;
import org.yah.tools.opencl.generated.CLGeneratedProgramBuilder;
import org.yah.tools.opencl.generated.ProgramMetadata;
import org.yah.tools.opencl.generated.TypeName;
import org.yah.tools.opencl.program.CLProgram;

import javax.annotation.Nullable;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static org.yah.tools.opencl.CLUtils.toStandardPath;

public class CLProgramImplementationGenerator implements ImplementationGenerator {

    private static final NodeList<Modifier> MODIFIERS = NodeList.nodeList(Modifier.publicModifier(), Modifier.finalModifier());
    public static final String MAVEN_RESOURCE_PREFIX = "src/main/resources";

    private final CLKernelImplementationGenerator kernelGenerator;

    public CLProgramImplementationGenerator() {
        this.kernelGenerator = new CLKernelImplementationGenerator();
    }

    @Override
    public ProgramImplementation generateImplementation(ProgramInterface programInterface) {
        TypeName interfaceName = CodeGeneratorSupport.createTypeName(programInterface.getCompilationUnit());
        TypeName implementationName = CLGeneratedProgramBuilder.getCLImplementationName(interfaceName);
        List<KernelImplementation> kernelImplementations = programInterface.getKernelInterfaces().stream()
                .map(ki -> kernelGenerator.generateImplementation(implementationName.getPackageName().orElse(null), ki))
                .collect(Collectors.toList());

        ImplementationBuilder implementationBuilder = new ImplementationBuilder(implementationName, programInterface.getCompilationUnit());
        CompilationUnit cu = implementationBuilder
                .addConstant(true, ProgramMetadata.class, "PROGRAM_METADATA", createProgramMetadata(programInterface.getParsedProgram(), implementationBuilder))
                .addField(CLProgram.class, "program", true)
                .addField(CLCommandQueue.class, "commandQueue", true)
                .addConstructor()
                .implementMethods((methodDeclaration, blockBuilder) ->
                        implement(programInterface, methodDeclaration, blockBuilder, implementationBuilder))
                .build();

        return new ProgramImplementation(programInterface, cu, kernelImplementations);
    }

    private Expression createProgramMetadata(ParsedProgram parsedProgram,
                                             ImplementationBuilder implementationBuilder) {
        ProgramMetadata metadata = parsedProgram.getMetadata();
        implementationBuilder.addImport(ProgramMetadata.class);

        Expression relativePath = new StringLiteralExpr(toStandardPath(metadata.getProgramFile()));

        Expression compilerOptions = metadata.getCompilerOptions() != null
                ? new StringLiteralExpr(metadata.getCompilerOptions())
                : new NullLiteralExpr();

        return new ObjectCreationExpr(null,
                new ClassOrInterfaceType(null, ProgramMetadata.class.getSimpleName()),
                NodeList.nodeList(relativePath, compilerOptions));
    }

    @Nullable
    private static String getResourcePath(String programFile) {
        if (programFile.startsWith(MAVEN_RESOURCE_PREFIX))
            return programFile.substring(MAVEN_RESOURCE_PREFIX.length());
        return null;
    }

    private void implement(ProgramInterface programInterface,
                           MethodDeclaration interfaceMethod,
                           BlockStmtBuilder blockBuilder,
                           ImplementationBuilder implementationBuilder) {
        if (isCloseMethod(interfaceMethod)) {
            blockBuilder.withStatements("program.close();", "commandQueue.close();");
        } else {
            String kernelImplementationName = "CL" + interfaceMethod.getType().asString();
            implementationBuilder.addImport(".kernels." + kernelImplementationName);

            String kernelInterfaceName = interfaceMethod.getType().asClassOrInterfaceType().getNameAsString();
            String kernelName = findKernelInteface(programInterface, kernelInterfaceName).getKernel().getParsedKernel().getName();
            blockBuilder.withStatement("return new %s(program.newKernel(\"%s\"), commandQueue);", kernelImplementationName, kernelName);
        }
    }


    private KernelInterface findKernelInteface(ProgramInterface programInterface, String interfaceName) {
        return programInterface.getKernelInterfaces().stream()
                .filter(ki -> ki.getCompilationUnit().getType(0).getNameAsString().equals(interfaceName))
                .findFirst().orElseThrow(() -> new NoSuchElementException("kernel for " + interfaceName + " not found"));
    }
}
