package org.yah.tools.opencl.mavenplugin;

import org.yah.tools.opencl.codegen.naming.DefaultNamingStrategy;
import org.yah.tools.opencl.codegen.naming.NamingStrategy;
import org.yah.tools.opencl.codegen.naming.TypeNameDecorator;

import java.util.List;

public class Binding {

    /**
     * Base package of generated program classs.
     * If not set, relative file path from resourcePath will be used.
     */
    public String basePackage;

    /**
     * Included files ant pattern.
     */
    public List<String> includes;

    /**
     * Excluded files ant pattern.
     */
    public List<String> excludes;

    /**
     * build program options
     */
    public String compilerOptions;

    /**
     * prefix added to both program and kernel name
     */
    public String namePrefix;

    /**
     * suffix added to both program and kernel name
     */
    public String nameSuffix;

    /**
     * prefix added to program name
     */
    public String programNamePrefix;

    /**
     * suffix added to program name
     */
    public String programNameSuffix;

    /**
     * prefix added to program name
     */
    public String kernelNamePrefix;

    /**
     * suffix added to program name
     */
    public String kernelNameSuffix;

    /**
     * Type arguments formatted as : <i>typeName</i>=<i>typeDeclaration</i>;...
     * ex: T=int,float;U=int4;V=double
     */
    public List<String> typeArguments;

    public List<String> kernelSuperInterfaces;

    public Binding() {
    }

    public Binding(GenerateBindingMojo mojo) {
        basePackage = mojo.basePackage;
        includes = mojo.includes;
        excludes = mojo.excludes;
        compilerOptions = mojo.compilerOptions;
        namePrefix = mojo.namePrefix;
        nameSuffix = mojo.nameSuffix;
        programNamePrefix = mojo.programNamePrefix;
        programNameSuffix = mojo.programNameSuffix;
        kernelNamePrefix = mojo.kernelNamePrefix;
        kernelNameSuffix = mojo.kernelNameSuffix;
        typeArguments = mojo.typeArguments;
    }

    public NamingStrategy createNamingStrategy() {
        TypeNameDecorator programDecorator = createNameDecorator(programNamePrefix, programNameSuffix);
        TypeNameDecorator kernelDecorator = createNameDecorator(kernelNamePrefix, kernelNameSuffix);
        if (programDecorator != null || kernelDecorator != null)
            return new DefaultNamingStrategy(programDecorator, kernelDecorator);
        return DefaultNamingStrategy.get();
    }

    public TypeNameDecorator createNameDecorator(String prefix, String suffix) {
        if (prefix == null) prefix = namePrefix;
        if (suffix == null) suffix = nameSuffix;
        if (prefix != null || suffix != null)
            return new TypeNameDecorator(prefix, suffix);
        return null;
    }

}
