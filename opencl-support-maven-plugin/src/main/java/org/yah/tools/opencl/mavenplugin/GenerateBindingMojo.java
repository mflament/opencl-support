package org.yah.tools.opencl.mavenplugin;

import io.github.azagniotov.matcher.AntPathMatcherArrays;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.yah.tools.opencl.CLUtils;
import org.yah.tools.opencl.codegen.DefaultNamingStrategy;
import org.yah.tools.opencl.codegen.NamingStrategy;
import org.yah.tools.opencl.codegen.generator.ProgramGenerator;
import org.yah.tools.opencl.context.CLContext;
import org.yah.tools.opencl.program.CLCompilerOptions;
import org.yah.tools.opencl.program.CLProgram;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static org.yah.tools.opencl.codegen.DefaultNamingStrategy.TypeNameDecorator;

/**
 * Generate opencl programs binding
 */
@Mojo(name = "generate", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class GenerateBindingMojo extends AbstractMojo {

    private static final String SYSTEM_PROPERTY_PREFIX = "clbindings.";

    private static final String DEFAULT_SOURCE_DIRECTORY = "src/main/resources";

    private static final String DEFAULT_INCLUDE = "**/*.cl";

    public static final String DEFAULT_OUTPUT_DIRECTORY = "target/generated-sources/opencl-support";

    /**
     * Base directory scaned for
     * If not set, relative file path from resourcePath will be used.
     */
    @Parameter(property = SYSTEM_PROPERTY_PREFIX + "sourceDirectory", defaultValue = DEFAULT_SOURCE_DIRECTORY)
    private String sourceDirectory;

    /**
     * Base directory scaned for
     * If not set, relative file path from resourcePath will be used.
     */
    @Parameter(property = SYSTEM_PROPERTY_PREFIX + "outputDirectory", defaultValue = DEFAULT_OUTPUT_DIRECTORY)
    private String outputDirectory;

    /**
     * Base package of generated program classs.
     * If not set, relative file path from resourcePath will be used.
     */
    @Parameter(property = SYSTEM_PROPERTY_PREFIX + "basePackage")
    private String basePackage;

    /**
     * Included files ant pattern.
     */
    @Parameter(defaultValue = DEFAULT_INCLUDE, property = SYSTEM_PROPERTY_PREFIX + "includes")
    private List<String> includes;

    /**
     * Excluded files ant pattern.
     */
    @Parameter(property = SYSTEM_PROPERTY_PREFIX + "excludes")
    private List<String> excludes;

    /**
     * build program options
     */
    @Parameter(property = SYSTEM_PROPERTY_PREFIX + "programOptions")
    private String compilerOptions;

    /**
     * prefix added to both program and kernel name
     */
    @Parameter
    private String namePrefix;

    /**
     * suffix added to both program and kernel name
     */
    @Parameter
    private String nameSuffix;

    /**
     * prefix added to program name
     */
    @Parameter
    private String programNamePrefix;

    /**
     * suffix added to program name
     */
    @Parameter
    private String programNameSuffix;

    /**
     * prefix added to program name
     */
    @Parameter
    private String kernelNamePrefix;

    /**
     * suffix added to program name
     */
    @Parameter
    private String kernelNameSuffix;

    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject project;

    private final AntPathMatcherArrays pathMatcher = new AntPathMatcherArrays.Builder().build();

    // TODO add a Map<String,String> to customize java program class name, from program path

    @Override
    public void execute() throws MojoExecutionException {
        NamingStrategy namingStrategy = createNamingStrategy();
        Path sourcePath = resolvePath(sourceDirectory, DEFAULT_SOURCE_DIRECTORY);
        if (!Files.exists(sourcePath)) {
            getLog().info("source directory " + sourcePath + " not found");
            return;
        }

        try (BindingGenerator bindingGenerator = new BindingGenerator(sourcePath, namingStrategy)) {
            bindingGenerator.generateBindings();
            String sourceRoot = CLUtils.toStandardPath(bindingGenerator.outputPath.toString());
            getLog().info("Adding " + sourceRoot + " to source roots");
            project.addCompileSourceRoot(sourceRoot);
        }
    }

    private Path resolvePath(String path, String defaultPath) {
        if (path == null)
            path = defaultPath;
        return project.getBasedir().toPath().resolve(path);
    }

    private class BindingGenerator implements AutoCloseable {
        private final Path sourcePath;
        private final Path outputPath;
        private final CLContext context;
        private final ProgramGenerator programGenerator;

        public BindingGenerator(Path sourcePath, NamingStrategy namingStrategy) {
            this.sourcePath = Objects.requireNonNull(sourcePath, "sourcePath is null");
            outputPath = resolvePath(outputDirectory, DEFAULT_OUTPUT_DIRECTORY);
            programGenerator = new ProgramGenerator(outputPath, namingStrategy);
            context = CLContext.builder().build();
        }

        public void close() {
            if (context != null)
                context.close();
        }

        public void generateBindings() throws MojoExecutionException {
            getLog().info("Scanning directory " + sourcePath + " for cl files");
            List<String> relativePaths = collectFiles();
            for (String relativePath : relativePaths) {
                generateBindings(relativePath);
            }
        }

        private void generateBindings(String relativePath) throws MojoExecutionException {
            getLog().info("Generating binding for " + relativePath);
            String programPath = "classpath:" + relativePath;
            try (CLProgram program = loadProgram(relativePath)) {
                try {
                    programGenerator.generate(program, programPath, getBasePackage(relativePath));
                } catch (IOException e) {
                    throw new MojoExecutionException("Error generating bindings for " + relativePath, e);
                }
            }
        }

        private List<String> collectFiles() throws MojoExecutionException {
            try {
                List<String> res = new LinkedList<>();
                Files.walkFileTree(sourcePath, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                        String relativePath = CLUtils.toStandardPath(sourcePath.relativize(file).toString());
                        if (acceptFile(relativePath))
                            res.add(relativePath);
                        return FileVisitResult.CONTINUE;
                    }
                });
                return res;
            } catch (IOException e) {
                throw new MojoExecutionException("Error collecting cl files", e);
            }
        }

        private boolean acceptFile(String relativePath) {
            if (excludes == null && includes == null)
                return true;
            if (excludes != null && match(relativePath, excludes))
                return false;
            return includes == null || match(relativePath, includes);
        }

        private boolean match(String relativePath, List<String> patterns) {
            return patterns.stream().anyMatch(i -> pathMatcher.isMatch(i, relativePath));
        }

        private CLProgram loadProgram(String relativePath) {
            return context.programBuilder()
                    .withCompilerOptions(CLCompilerOptions.parse(compilerOptions).withKernelArgInfo())
                    .withFile(sourcePath.resolve(relativePath).toString())
                    .build();
        }

        private String getBasePackage(String relativePath) throws MojoExecutionException {
            String[] names = relativePath.split("/");
            if (basePackage == null && names.length <= 1) {
                throw new MojoExecutionException(this, "No package resolved for '" + relativePath + "'",
                        "Use the basicPackage parameter or place the cl programs in nested directory");
            }
            List<String> res = new ArrayList<>(names.length);
            if (basePackage != null)
                res.add(basePackage);
            for (int i = 0; i < names.length - 1; i++)
                res.add(names[i]);
            return String.join(".", res);
        }

    }

    private NamingStrategy createNamingStrategy() {
        TypeNameDecorator programDecorator = createNameDecorator(programNamePrefix, programNameSuffix);
        TypeNameDecorator kernelDecorator = createNameDecorator(kernelNamePrefix, kernelNameSuffix);
        if (programDecorator != null || kernelDecorator != null)
            return new DefaultNamingStrategy(programDecorator, kernelDecorator);
        return DefaultNamingStrategy.get();
    }

    private TypeNameDecorator createNameDecorator(String prefix, String suffix) {
        if (prefix == null) prefix = namePrefix;
        if (suffix == null) suffix = nameSuffix;
        if (prefix != null || suffix != null) return new TypeNameDecorator(prefix, suffix);
        return null;
    }

}
