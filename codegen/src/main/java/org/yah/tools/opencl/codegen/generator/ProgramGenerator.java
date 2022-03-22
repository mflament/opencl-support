package org.yah.tools.opencl.codegen.generator;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;
import org.yah.tools.opencl.codegen.DefaultNamingStrategy;
import org.yah.tools.opencl.codegen.NamingStrategy;
import org.yah.tools.opencl.codegen.TypeParametersConfig;
import org.yah.tools.opencl.codegen.model.ProgramModelBuilder;
import org.yah.tools.opencl.codegen.model.kernel.KernelModel;
import org.yah.tools.opencl.codegen.model.program.ProgramModel;
import org.yah.tools.opencl.codegen.parser.ParsedProgram;
import org.yah.tools.opencl.codegen.parser.ProgramParser;
import org.yah.tools.opencl.codegen.parser.TypeResolver;
import org.yah.tools.opencl.codegen.parser.type.CLType;
import org.yah.tools.opencl.codegen.parser.type.CLTypeParameter;
import org.yah.tools.opencl.context.CLContext;
import org.yah.tools.opencl.program.CLCompilerOptions;
import org.yah.tools.opencl.program.CLProgram;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class ProgramGenerator {

    private final CLContext context;
    private final Path outputDirectory;
    private final NamingStrategy namingStrategy;

    public ProgramGenerator(CLContext context, Path outputDirectory, @Nullable NamingStrategy namingStrategy) {
        this.context = Objects.requireNonNull(context, "context is null");
        this.outputDirectory = Objects.requireNonNull(outputDirectory, "outputDirectory is null");
        this.namingStrategy = namingStrategy != null ? namingStrategy : DefaultNamingStrategy.get();
    }

    public void generate(ProgramGeneratorRequest request) throws IOException {
        Objects.requireNonNull(request, "request is null");
        Files.createDirectories(outputDirectory);
        new ProgramGeneratorContext(request).generate();
    }

    private void write(CompilationUnit compilationUnit) throws IOException {
        String packageName = compilationUnit.getPackageDeclaration()
                .map(PackageDeclaration::getNameAsString)
                .orElseThrow(() -> new IllegalArgumentException("No package in compilation unit " + compilationUnit));
        Path packageDir = outputDirectory.resolve(packageName.replaceAll("\\.", "/"));
        Files.createDirectories(packageDir);
        String typeName = compilationUnit.getType(0).getNameAsString();
        Path outputFile = packageDir.resolve(typeName + ".java");
        Files.write(outputFile, compilationUnit.toString().getBytes(StandardCharsets.UTF_8));
    }

    public static boolean areParameterAgumentsCompatible(CLType type1, CLType type2) {
        return (type1.isPointer() && type2.isPointer())
                || (type1.isScalar() && type2.isScalar())
                || type1.isVector() && type2.isVector();
    }

    private final class ProgramGeneratorContext {
        private final ProgramGeneratorRequest request;
        private final TypeResolver typeResolver;
        private final ProgramParser parser;
        private final ProgramModelBuilder modelBuilder;

        private ProgramModel programModel;
        private CompilationUnit programInterface;
        private final List<CompilationUnit> kernelInterfaces = new ArrayList<>();

        public ProgramGeneratorContext(ProgramGeneratorRequest request) {
            this.request = request;
            typeResolver = new TypeResolver();
            parser = new ProgramParser(typeResolver);
            modelBuilder = new ProgramModelBuilder(namingStrategy, false);
        }

        public void generate() throws IOException {
            CLProgram program = createProgram();

            ParsedProgram parsedProgram = parser.parse(program, request.getProgramPath(), request.getTypeParametersConfig());
            programModel = modelBuilder.build(request.getBasePackage(), parsedProgram);
            generateInterfaces();

            TypeParametersConfig typeParametersConfig = request.getTypeParametersConfig();
            if (typeParametersConfig != null) {
                for (TypeParametersConfig.ParameterTypeArguments argumentDeclaration : typeParametersConfig.getArgumentDeclarations()) {
                    Map<String, CLType> typeArguments = typeResolver.resolve(argumentDeclaration.getTypeDeclarations());
                    generateCLImplementation(typeArguments);
                }
            } else {
                generateCLImplementation(Collections.emptyMap());
            }
        }

        private CLProgram createProgram() {
            if (request.getTypeParametersConfig() != null)
                return createProgram(request.getTypeParametersConfig());

            return context.programBuilder()
                    .withCompilerOptions(new CLCompilerOptions(request.getCompilerOptions()).withKernelArgInfo())
                    .withDevices(context.getDevices())
                    .withSource(request.getProgramSource())
                    .build();
        }

        /**
         * Add typedefs named macroName_TYPE to expose the macro name in type name resolution.
         */
        private CLProgram createProgram(TypeParametersConfig typeParameters) {
            StringBuilder typedefs = new StringBuilder();
            CLCompilerOptions newOptions = new CLCompilerOptions(request.getCompilerOptions()).withKernelArgInfo();
            for (String macroName : typeParameters.getNames()) {
                List<String> typeDeclarations = typeParameters.getArgumentDeclarations(macroName);
                String firstDelaration = null;
                CLType firstType = null;
                for (String typeDeclaration : typeDeclarations) {
                    CLType newType = typeResolver.resolve(typeDeclaration);
                    if (firstType == null) {
                        firstDelaration = typeDeclaration;
                        firstType = newType;
                    } else if (!areParameterAgumentsCompatible(firstType, newType))
                        throw new IllegalArgumentException("Incompatible type parameter " + macroName + " arguments,  " + newType + " is not compatible with " + firstType);
                }
                typeResolver.registerTypeParamter(new CLTypeParameter(macroName, firstType));
                String newName = macroName + "_TYPE";
                newOptions.putMacro(newName, firstDelaration);
                typedefs.append("typedef ").append(newName).append(" ").append(macroName).append(";\n");
            }

            return context.programBuilder()
                    .withCompilerOptions(newOptions)
                    .withDevices(context.getDevices())
                    .withSource(typedefs.append(request.getProgramSource()).toString())
                    .build();
        }

        private void generateInterfaces() throws IOException {
            ProgramInterfaceGenerator programInterfaceGenerator = new ProgramInterfaceGenerator(programModel);
            programInterface = programInterfaceGenerator.generate();
            write(programInterface);
            for (KernelModel kernelModel : programModel.getKernels()) {
                KernelInterfaceGenerator kernelInterfaceGenerator = new KernelInterfaceGenerator(kernelModel);
                CompilationUnit kernelInterface = kernelInterfaceGenerator.generate();
                write(kernelInterface);
                kernelInterfaces.add(kernelInterface);
            }
        }

        private void generateCLImplementation(Map<String, CLType> typeArguents) throws IOException {
            CLProgramGenerator clProgramGenerator = new CLProgramGenerator(programInterface, typeArguents);
            CompilationUnit clPorgram = clProgramGenerator.generate();
            write(clPorgram);
            for (CompilationUnit kernelInterface : kernelInterfaces) {
                CLKernelGenerator clKernelGenerator = new CLKernelGenerator(clPorgram, kernelInterface, typeArguents);
                write(clKernelGenerator.generate());
            }
        }

    }

}
