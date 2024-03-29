package org.yah.tools.opencl.mavenplugin;

import io.github.azagniotov.matcher.AntPathMatcherArrays;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.yah.tools.opencl.CLUtils;
import org.yah.tools.opencl.codegen.generator.ProgramGenerator;
import org.yah.tools.opencl.codegen.generator.ProgramGeneratorRequest;
import org.yah.tools.opencl.codegen.parser.CLTypeVariables;
import org.yah.tools.opencl.context.CLContext;
import org.yah.tools.opencl.program.CLCompilerOptions;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

/**
 * Generate opencl programs binding
 */
@Mojo(name = "generate", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class GenerateBindingMojo extends AbstractMojo {

    private static final String SYSTEM_PROPERTY_PREFIX = "clbindings.";

    private static final String DEFAULT_INCLUDE = "**/*.cl";

    private static final String DEFAULT_SOURCE_DIRECTORY = "src/main/resources";

    public static final String DEFAULT_OUTPUT_DIRECTORY = "target/generated-sources/opencl-support";

    /**
     * Base directory scanned for cl files
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
    @Parameter
    public String basePackage;

    /**
     * Included files ant pattern.
     */
    @Parameter
    public List<String> includes = Collections.singletonList(DEFAULT_INCLUDE);

    /**
     * Excluded files ant pattern.
     */
    @Parameter
    public List<String> excludes;

    /**
     * build program options
     */
    @Parameter
    public String compilerOptions;

    /**
     * prefix added to both program and kernel name
     */
    @Parameter
    public String namePrefix;

    /**
     * suffix added to both program and kernel name
     */
    @Parameter
    public String nameSuffix;

    /**
     * prefix added to program name
     */
    @Parameter
    public String programNamePrefix;

    /**
     * suffix added to program name
     */
    @Parameter
    public String programNameSuffix;

    /**
     * prefix added to program name
     */
    @Parameter
    public String kernelNamePrefix;

    /**
     * suffix added to program name
     */
    @Parameter
    public String kernelNameSuffix;

    /**
     * fully qualified names of extended kernel interface
     */
    @Parameter
    public List<String> kernelSuperInterfaces;

    /**
     * Type arguments formatted as : <i>typeName</i>=<i>typeDeclaration</i>;...
     * ex: T=int,float;U=int4;V=double
     */
    @Parameter
    public List<String> typeArguments = new ArrayList<>();

    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject project;

    @Parameter
    private List<Binding> bindings;

    private final AntPathMatcherArrays pathMatcher = new AntPathMatcherArrays.Builder().build();

    @Override
    public void execute() throws MojoExecutionException {

        Path sourcePath = resolvePath(sourceDirectory, DEFAULT_SOURCE_DIRECTORY);
        if (!Files.exists(sourcePath)) {
            getLog().info("source directory " + sourcePath + " not found");
            return;
        }

        if (bindings == null || bindings.isEmpty()) {
            bindings = Collections.singletonList(new Binding(this));
        }

        for (Binding config : bindings) {
            try (BindingGenerator bindingGenerator = new BindingGenerator(sourcePath, config)) {
                bindingGenerator.generateBindings();
                String sourceRoot = CLUtils.toStandardPath(bindingGenerator.outputPath.toString());
                getLog().info("Adding " + sourceRoot + " to source roots");
                project.addCompileSourceRoot(sourceRoot);
            }
        }
    }

    private Path resolvePath(String path, String defaultPath) {
        if (path == null)
            path = defaultPath;
        return project.getBasedir().toPath().resolve(path);
    }

    private class BindingGenerator implements AutoCloseable {
        private final Binding config;
        private final Path sourcePath;
        private final Path outputPath;
        private final CLContext context;
        private final ProgramGenerator programGenerator;

        public BindingGenerator(Path sourcePath, Binding config) {
            this.sourcePath = Objects.requireNonNull(sourcePath, "sourcePath is null");
            this.config = Objects.requireNonNull(config, "config is null");
            outputPath = resolvePath(outputDirectory, DEFAULT_OUTPUT_DIRECTORY);
            context = CLContext.builder().build();
            programGenerator = new ProgramGenerator(context, outputPath, config.createNamingStrategy());
        }

        public void close() {
            if (context != null)
                context.close();
        }

        public void generateBindings() throws MojoExecutionException {
            List<String> relativePaths = collectFiles();
            for (String relativePath : relativePaths) {
                generateBindings(relativePath);
            }
        }

        private void generateBindings(String relativePath) throws MojoExecutionException {
            getLog().info("Generating binding for " + relativePath);
            String programSource = loadProgram(relativePath);

            String programPath;
            if(isInClasspath(relativePath))
                programPath = "classpath:" + relativePath;
            else
                programPath = CLUtils.toStandardPath(sourceDirectory, relativePath);

            String options = config.compilerOptions != null ? config.compilerOptions : compilerOptions;
            CLTypeVariables typeVariables = parseTypeArguments();

            ProgramGeneratorRequest generatorRequest = ProgramGeneratorRequest.builder()
                    .withBasePackage(getBasePackage(relativePath))
                    .withCompilerOptions(CLCompilerOptions.parse(options))
                    .withTypeVariables(typeVariables)
                    .withProgramSource(programSource)
                    .withProgramPath(programPath)
                    .withKernelInterfaces(getKernelInterfaces())
                    .build();

            try {
                programGenerator.generate(generatorRequest);
            } catch (IOException e) {
                throw new MojoExecutionException("Error generating bindings for " + relativePath, e);
            }
        }

        private boolean isInClasspath(String relativePath) {
            // todo : improve by check project resources
            return sourceDirectory.equals("src/main/resources");
        }

        private List<String> getKernelInterfaces() {
            List<String> ksi = config.kernelSuperInterfaces != null
                    ? config.kernelSuperInterfaces
                    : kernelSuperInterfaces;
            if (ksi == null)
                return Collections.emptyList();
            return ksi;
        }

        private CLTypeVariables parseTypeArguments() {
            List<String> args = config.typeArguments != null ? config.typeArguments : typeArguments;
            return CLTypeVariables.parse(args);
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
            List<String> inc = config.includes != null ? config.includes : includes;
            List<String> exc = config.excludes != null ? config.excludes : excludes;
            if (inc == null && exc == null)
                return true;
            if (exc != null && match(relativePath, exc))
                return false;
            return inc == null || match(relativePath, inc);
        }

        private boolean match(String relativePath, List<String> patterns) {
            return patterns.stream().anyMatch(i -> pathMatcher.isMatch(i, relativePath));
        }

        private String loadProgram(String relativePath) {
            try {
                byte[] bytes = Files.readAllBytes(sourcePath.resolve(relativePath));
                return new String(bytes, StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }

        private String getBasePackage(String relativePath) throws MojoExecutionException {
            String[] names = relativePath.split("/");
            String bp = config.basePackage != null ? config.basePackage : basePackage;
            if (bp == null && names.length <= 1) {
                throw new MojoExecutionException(this, "No package resolved for '" + relativePath + "'",
                        "Use the basicPackage parameter or place the cl programs in nested directory");
            }
            List<String> res = new ArrayList<>(names.length);
            if (bp != null)
                res.add(bp);
            res.addAll(Arrays.asList(names).subList(0, names.length - 1));
            return String.join(".", res);
        }

    }

}
