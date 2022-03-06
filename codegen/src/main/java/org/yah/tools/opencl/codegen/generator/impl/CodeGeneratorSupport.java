package org.yah.tools.opencl.codegen.generator.impl;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.DataKey;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.nodeTypes.NodeWithName;
import com.github.javaparser.ast.stmt.BlockStmt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yah.tools.opencl.codegen.generator.kernel.MethodKind;
import org.yah.tools.opencl.codegen.generator.kernel.ParameterKind;
import org.yah.tools.opencl.codegen.generator.model.old.GeneratedKernelArgument;
import org.yah.tools.opencl.generated.TypeName;

import java.util.function.Consumer;

public final class CodeGeneratorSupport {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CodeGeneratorSupport.class);

    public static final NodeList<Modifier> PUBLIC_MODIFIERS = NodeList.nodeList(Modifier.publicModifier());

    /**
     * Used to tag kernel interface method
     */
    public static final DataKey<Integer> METHOD_KINDS = new DataKey<Integer>() {
    };

    /**
     * Used to tag kernel interface method paramters
     */
    public static final DataKey<ParameterKind> PARAMETER_KIND = new DataKey<ParameterKind>() {
    };

    public static final DataKey<GeneratedKernelArgument> ARGUMENT = new DataKey<GeneratedKernelArgument>() {
    };

    private CodeGeneratorSupport() {
    }

    public static TypeName createTypeName(CompilationUnit compilationUnit) {
        return new TypeName(compilationUnit.getPackageDeclaration().map(NodeWithName::getNameAsString).orElse(null),
                compilationUnit.getType(0).getNameAsString());
    }

    public static void extendsAutoCloseable(ClassOrInterfaceDeclaration declaration) {
        declaration.addExtendedType(AutoCloseable.class)
                .addMethod("close")
                .addMarkerAnnotation(Override.class).removeBody()
                .setData(METHOD_KINDS, MethodKind.CLOSE.getId());
    }

}
