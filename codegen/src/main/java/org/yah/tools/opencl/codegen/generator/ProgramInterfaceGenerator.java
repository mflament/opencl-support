package org.yah.tools.opencl.codegen.generator;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.TypeParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yah.tools.opencl.codegen.TypeParametersConfig;
import org.yah.tools.opencl.codegen.model.kernel.KernelArgumentMethod;
import org.yah.tools.opencl.codegen.model.kernel.KernelArgumentMethodParameter;
import org.yah.tools.opencl.codegen.model.kernel.KernelMethodParameter;
import org.yah.tools.opencl.codegen.model.kernel.KernelModel;
import org.yah.tools.opencl.codegen.model.program.ProgramMethod;
import org.yah.tools.opencl.codegen.model.program.ProgramModel;
import org.yah.tools.opencl.codegen.parser.ParsedKernelArgument;
import org.yah.tools.opencl.codegen.parser.ParsedProgram;
import org.yah.tools.opencl.codegen.parser.type.CLType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.yah.tools.opencl.codegen.model.program.ProgramMethods.CreateKernel;

public class ProgramInterfaceGenerator extends AbstractCodeGenerator<ProgramModel> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProgramInterfaceGenerator.class);

    public ProgramInterfaceGenerator(ProgramModel programModel) {
        super(programModel, programModel.getBasePackage(), programModel.getName());
        declaration.setInterface(true);
        declaration.setData(PROGRAM_MODEL, source);

        Collection<TypeParameterReferences> typeParameterReferences = collectTypeParameterReferences(programModel);
        if (!typeParameterReferences.isEmpty()) {
            Collection<GeneratedTypeParameter> typeParameters = createTypeArguments(typeParameterReferences);
            addTypeParameters(typeParameters.stream().map(GeneratedTypeParameter::getName).collect(Collectors.toList()));
        }
    }

    private Collection<TypeParameterReferences> collectTypeParameterReferences(ProgramModel programModel) {
        ParsedProgram parsedProgram = programModel.getParsedProgram();
        TypeParametersConfig typeParametersConfig = parsedProgram.getTypeParametersConfig();
        if (typeParametersConfig == null)
            return Collections.emptyList();
        return typeParametersConfig.getNames().stream()
                .map(name -> createTypeParameterReferences(programModel, name))
                .collect(Collectors.toList());
    }

    private TypeParameterReferences createTypeParameterReferences(ProgramModel programModel, String typeParameterName) {
        List<KernelArgumentMethodParameter> referencedBy = programModel.getKernels().stream()
                .flatMap(km -> km.getMethods().stream())
                .flatMap(pm -> pm.getParameters().stream())
                .filter(KernelArgumentMethodParameter.class::isInstance)
                .map(KernelArgumentMethodParameter.class::cast)
                .filter(p -> isReferencing(p, typeParameterName))
                .collect(Collectors.toList());
        return new TypeParameterReferences(typeParameterName, referencedBy);
    }

    private static boolean isReferencing(KernelMethodParameter parameter, String typeParameterName) {
        if (parameter.isBuffer() || parameter.isValue() ||parameter.isValueComponent()) {
            KernelArgumentMethod kernelArgumentMethod = parameter.getMethod().asKernelArgumentMethod();
            ParsedKernelArgument parsedKernelArgument = kernelArgumentMethod.getParsedKernelArgument();
            CLType componentType = parsedKernelArgument.getType().getComponentType();
            return componentType.isCLTypeParameter() && componentType.asCLTypeParameter().getName().equals(typeParameterName);
        }
        return false;
    }

    private Collection<GeneratedTypeParameter> createTypeArguments(Collection<TypeParameterReferences> references) {
        return references.stream()
                .flatMap(ref -> createGeneratedTypeParameters(ref).stream())
                .collect(Collectors.toList());
    }

    private List<GeneratedTypeParameter> createGeneratedTypeParameters(TypeParameterReferences references) {
        String name = references.getTypeParameterName();
        boolean buffer = false, value = false;
        for (KernelArgumentMethodParameter referencer : references.getReferencedBy()) {
            CLType type = referencer.getParsedKernelArgument().getType();
            buffer |= referencer.isBuffer();
            buffer |= referencer.isValue() && type.isVector();
            value |= referencer.isValue() && type.isScalar();
            value |= referencer.isValueComponent() && type.isVector();
        }
        List<GeneratedTypeParameter> res = new ArrayList<>();
        if (value && buffer) {
            res.add(new GeneratedTypeParameter(name, false));
            res.add(new GeneratedTypeParameter(name + "_PTR", true));
        } else if (buffer || value) {
            res.add(new GeneratedTypeParameter(name, buffer));
        } else
            throw new IllegalStateException("Invalid type parameter " + references);
        return res;
    }

    @Override
    public CompilationUnit generate() {
        source.getMethods().forEach(this::generateMethod);
        return compilationUnit;
    }

    private void generateMethod(ProgramMethod programMethod) {
        MethodDeclaration methodDeclaration = declaration.addMethod(programMethod.getMethodName()).removeBody();
        methodDeclaration.setData(PROGRAM_METHOD, programMethod);
        methodDeclaration.setData(PROGRAM_MODEL, source);

        if (programMethod.isCreateKernel()) {
            generateCreateKernelMethod(programMethod.asCreateKernel(), methodDeclaration);
        } else if (programMethod.isCloseProgram()) {
            declaration.addExtendedType(AutoCloseable.class);
            methodDeclaration.addMarkerAnnotation(Override.class);
        } else {
            LOGGER.error("Unhandled program method {}", programMethod);
        }
    }

    private void generateCreateKernelMethod(CreateKernel programMethod, MethodDeclaration methodDeclaration) {
        KernelModel kernelModel = programMethod.getKernelModel();
        compilationUnit.addImport(kernelModel.getQualifiedName());
        ClassOrInterfaceType kernelType = new ClassOrInterfaceType(null, kernelModel.getName());

        List<String> typeParameters = kernelModel.getReferencedTypeParameters();
        if (!typeParameters.isEmpty()) {
            kernelType.setTypeArguments(typeParameters.stream()
                    .map(TypeParameter::new)
                    .collect(toNodeList()));
        }

        methodDeclaration.setType(kernelType);
        methodDeclaration.setData(KERNEL_MODEL, kernelModel);
    }

    private static class TypeParameterReferences {
        private final String typeParameterName;
        private final List<KernelArgumentMethodParameter> referencedBy;

        public TypeParameterReferences(String typeParameterName, List<KernelArgumentMethodParameter> referencedBy) {
            this.typeParameterName = typeParameterName;
            this.referencedBy = referencedBy;
        }

        public String getTypeParameterName() {
            return typeParameterName;
        }

        public List<KernelArgumentMethodParameter> getReferencedBy() {
            return referencedBy;
        }

        @Override
        public String toString() {
            return "TypeParameterReferences{" +
                    "typeParameterName='" + typeParameterName + '\'' +
                    ", referencedBy=" + referencedBy +
                    '}';
        }
    }

}
