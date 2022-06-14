package org.yah.tools.opencl.codegen.generator;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;
import org.yah.tools.opencl.codegen.generator.type.JavaTypeVariable;
import org.yah.tools.opencl.codegen.generator.type.JavaTypeVariables;
import org.yah.tools.opencl.codegen.naming.NamingStrategy;
import org.yah.tools.opencl.codegen.naming.NamingStrategy.ProgramNamingStrategy;
import org.yah.tools.opencl.codegen.parser.CLTypeResolver;
import org.yah.tools.opencl.codegen.parser.CLTypeVariables;
import org.yah.tools.opencl.codegen.parser.ParsedProgram;
import org.yah.tools.opencl.codegen.parser.ProgramParser;
import org.yah.tools.opencl.codegen.parser.type.CLType;
import org.yah.tools.opencl.codegen.parser.type.CLTypeVariable;
import org.yah.tools.opencl.context.CLContext;
import org.yah.tools.opencl.program.CLCompilerOptions;
import org.yah.tools.opencl.program.CLProgram;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class ProgramGenerator {

    private final CLContext context;
    private final Path outputDirectory;
    private final NamingStrategy namingStrategy;

    public ProgramGenerator(CLContext context, Path outputDirectory, NamingStrategy namingStrategy) {
        this.context = Objects.requireNonNull(context, "context is null");
        this.outputDirectory = Objects.requireNonNull(outputDirectory, "outputDirectory is null");
        this.namingStrategy = Objects.requireNonNull(namingStrategy, "namingStrategy is null");
    }

    public void generate(ProgramGeneratorRequest request) throws IOException {
        Objects.requireNonNull(request, "request is null");

        CLTypeResolver clTypeResolver = new CLTypeResolver();
        ProgramParser parser = new ProgramParser(clTypeResolver);
        CLTypeVariables clTypeVariables = request.getTypeParametersConfig();
        CLProgram program;
        if (clTypeVariables.isEmpty()) {
            program = loadProgram(request.getProgramSource(), request.getCompilerOptions());
        } else {
            registerTypeParameters(clTypeVariables, clTypeResolver);
            program = loadProgram(request.getProgramSource(), request.getCompilerOptions(), clTypeVariables);
        }
        if (program.getKernelNames().isEmpty())
            return;
        ParsedProgram parsedProgram = parser.parse(program, request.getProgramPath());

        Files.createDirectories(outputDirectory);

        JavaTypeVariables typeVariables = JavaTypeVariables.builder(clTypeVariables)
                .withKernels(parsedProgram.getKernels())
                .build();
        List<Map<String, CLType>> typeArguments = clTypeVariables.resolve(clTypeResolver);

        List<CompilationUnit> compilationUnits = generate(request, parsedProgram, namingStrategy.program(parsedProgram),
                typeVariables, typeArguments);
        for (CompilationUnit compilationUnit : compilationUnits) {
            write(compilationUnit);
        }
    }

    private List<CompilationUnit> generate(ProgramGeneratorRequest request, ParsedProgram program, ProgramNamingStrategy namingStrategy,
                                           JavaTypeVariables typeVariables, List<Map<String, CLType>> typeArguments) {


        ProgramInterfaceGenerator programInterfaceGenerator = new ProgramInterfaceGenerator(program, request, namingStrategy, typeVariables);
        List<CompilationUnit> compilationUnits = new ArrayList<>(programInterfaceGenerator.generate());

        List<CLProgramGenerator> clProgramGenerators;
        if (typeArguments.isEmpty()) {
            CLProgramGenerator generator = new CLProgramGenerator(request, namingStrategy.clImplementation(Collections.emptyMap()), typeVariables, Collections.emptyMap());
            clProgramGenerators = Collections.singletonList(generator);
        } else {
            clProgramGenerators = typeArguments.stream()
                    .map(ta -> new CLProgramGenerator(request, namingStrategy.clImplementation(ta), typeVariables, ta))
                    .collect(Collectors.toList());
        }

        clProgramGenerators.stream()
                .flatMap(g -> g.generate(programInterfaceGenerator.getDeclaration()).stream())
                .collect(Collectors.toCollection(() -> compilationUnits));

        return compilationUnits;
    }

    private CLProgram loadProgram(String source, CLCompilerOptions compilerOptions) {
        return loadProgram(source, compilerOptions, null);
    }

    /**
     * Add typedefs named macroName_TYPE to expose the macro name in type name resolution.
     */
    private CLProgram loadProgram(String source, CLCompilerOptions compilerOptions, @Nullable CLTypeVariables clTypeVariables) {
        StringBuilder typedefs = new StringBuilder();
        CLCompilerOptions newOptions = new CLCompilerOptions(compilerOptions).withKernelArgInfo();
        if (clTypeVariables != null) {
            List<String> names = clTypeVariables.getNames();
            String[] declarations = clTypeVariables.getDeclarations(0);
            for (int i = 0; i < names.size(); i++) {
                String typeName = names.get(i);
                String newName = typeName + "_TYPE";
                String typeDeclaration = declarations[i];
                newOptions.putMacro(newName, typeDeclaration);
                typedefs.append("typedef ").append(newName).append(" ").append(typeName).append(";\n");
            }
        }

        return context.programBuilder()
                .withCompilerOptions(newOptions)
                .withDevices(context.getDevices())
                .withSource(typedefs.append(source).toString())
                .build();
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

    private static void registerTypeParameters(CLTypeVariables clTypeVariables, CLTypeResolver clTypeResolver) {
        checkTypeParameters(clTypeVariables, clTypeResolver);

        String[] declarations = clTypeVariables.getDeclarations(0);
        List<String> names = clTypeVariables.getNames();
        for (int i = 0; i < names.size(); i++) {
            String typeDeclaration = declarations[i];
            CLType referenceType = clTypeResolver.resolve(typeDeclaration);
            CLTypeVariable parameter = new CLTypeVariable(names.get(i), referenceType);
            clTypeResolver.registerTypeVariable(parameter);
        }
    }

    private static void checkTypeParameters(CLTypeVariables clTypeVariables, CLTypeResolver clTypeResolver) {
        List<String> names = clTypeVariables.getNames();
        for (String name : names) {
            List<String> declarations = clTypeVariables.getVariableDeclarations(name);
            if (declarations.isEmpty())
                throw newInvalidTypeException("no declarations for parameter '" + name + "' in " + clTypeVariables);
            Iterator<String> iterator = declarations.iterator();
            CLType type = clTypeResolver.resolve(iterator.next());
            boolean isPointer = JavaTypeVariable.isBuffer(type);
            while (iterator.hasNext()) {
                String next = iterator.next();
                type = clTypeResolver.resolve(next);
                if (isPointer != JavaTypeVariable.isBuffer(type))
                    throw newInvalidTypeException("type " + type + " mismatch for parameter '" + name + "' in " + clTypeVariables);
            }
        }
    }

    private static IllegalArgumentException newInvalidTypeException(String s) {
        return new IllegalArgumentException("Invalid type arguments: " + s);
    }


}
