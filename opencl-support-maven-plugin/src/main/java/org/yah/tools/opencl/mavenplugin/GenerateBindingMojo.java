package org.yah.tools.opencl.mavenplugin;

import io.github.azagniotov.matcher.AntPathMatcherArrays;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.yah.tools.opencl.CLUtils;
import org.yah.tools.opencl.codegen.generator.DefaultProgramGenerator;
import org.yah.tools.opencl.codegen.parser.model.ParsedProgram;
import org.yah.tools.opencl.codegen.parser.impl.DefaultProgramParser;
import org.yah.tools.opencl.context.CLContext;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Generate opencl programs binding
 */
@Mojo(name = "generate", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class GenerateBindingMojo extends AbstractMojo {

    private static final File DEFAULT_DIRECTORY = new File("src/main/resources/cl");

    private static final String DEFAULT_FILE_PATTERN = "*.cl";

    public static final String DEFAULT_OUTPUT_DIRECTORY = "target/generated-sources/opencl-support";
    public static final String CL_KERNEL_ARG_INFO = "-cl-kernel-arg-info";

    /**
     * Directories scanned for cl files.
     */
    @Parameter
    private List<File> directories;

    /**
     * CL files name pattern.
     */
    @Parameter(defaultValue = DEFAULT_FILE_PATTERN, property = "clbindings.pattern")
    private String fileNamePattern;

    /**
     * CL files name pattern.
     */
    @Parameter(defaultValue = DEFAULT_OUTPUT_DIRECTORY, property = "clbindings.output")
    private File outputDirectory;

    /**
     * base package for generated programs
     */
    @Parameter(property = "clbindings.basePackage")
    private String basePackage;

    /**
     * build program options
     */
    @Parameter(property = "clbindings.programOptions")
    private String compilerOptions;

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;

    // TODO add a Map<String,String> to customize java program class name, from program path

    private final AntPathMatcherArrays pathMatcher = new AntPathMatcherArrays.Builder().build();

    public void setDirectories(List<File> directories) {
        this.directories = directories;
    }

    public void setFileNamePattern(String fileNamePattern) {
        this.fileNamePattern = fileNamePattern;
    }

    public void setOutputDirectory(File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    public void setProject(MavenProject project) {
        this.project = project;
    }

    @Override
    public void execute() throws MojoExecutionException {
        Path projectPath = project.getBasedir().toPath();
        List<String> resourcePaths = getResourcePaths();
        List<String> dirs = directories == null ? resourcePaths : directories.stream().map(File::toString).collect(Collectors.toList());
        Path outputPath = outputDirectory == null ? Paths.get(DEFAULT_OUTPUT_DIRECTORY) : outputDirectory.toPath();
        Set<String> uniqueResourcePaths = new HashSet<>(resourcePaths);
        try (CLContext context = CLContext.builder().build()) {
            for (String dir : dirs) {
                Path path = projectPath.resolve(dir);
                getLog().debug(String.format("collecting cl files from %s", path.toAbsolutePath()));
                visitDirectory(path, context, uniqueResourcePaths);
                getLog().debug(String.format("bindings for %s generated", dir));
            }
            if (project != null) {
                project.addCompileSourceRoot(outputDirectory.getPath());
            }
        } catch (UncheckedMojoExecutionException e) {
            throw e.getCause();
        } catch (Exception e) {
            throw new MojoExecutionException("Error generating cl program bindings", e);
        }
    }

    @SuppressWarnings("unchecked")
    private List<String> getResourcePaths() {
        List<Resource> resources = project.getResources();
        return resources.stream()
                .map(resource -> CLUtils.toStandardPath(resource.getDirectory()))
                .collect(Collectors.toList());
    }

    private void visitDirectory(Path directory, CLContext context, Set<String> resourcePaths) throws IOException {
        List<Path> res = new LinkedList<>();
        BindingGenerator bindingGenerator = new BindingGenerator(context, directory, resourcePaths);
        Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (isCLFile(file)) {
                    try {
                        bindingGenerator.generateBindings(file);
                    } catch (MojoExecutionException e) {
                        throw new UncheckedMojoExecutionException(e);
                    }
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private boolean isCLFile(Path path) {
        return Files.isRegularFile(path) && matchPattern(path.getFileName().toString());
    }

    private boolean matchPattern(String fileName) {
        String pattern = fileNamePattern == null ? DEFAULT_FILE_PATTERN : fileNamePattern;
        return pathMatcher.isMatch(pattern, fileName);
    }

    private class BindingGenerator implements AutoCloseable {
        private final CLContext context;
        private final DefaultProgramParser programParser;
        private final DefaultProgramGenerator programGenerator;

        public BindingGenerator(CLContext context,
                                Path baseDirectory,
                                Set<String> resourcePaths) {
            this.context = context;
            programParser = new DefaultProgramParser(context, baseDirectory, resourcePaths);
            programGenerator = new DefaultProgramGenerator(outputDirectory.toPath(), null);
        }

        public void generateBindings(Path filePath) throws IOException, MojoExecutionException {
            getLog().debug(String.format("generating binding for %s", filePath));
            try (ParsedProgram parsedProgram = programParser.parse(filePath, compilerOptions)) {
                String programFile = parsedProgram.getMetadata().getProgramFile();
                String packageName = packageName(programFile);
                programGenerator.generate(packageName, parsedProgram);
            }
        }

        public void close() {
            context.close();
        }
    }

    private String packageName(String path) throws MojoExecutionException {
        String resourcePath = CLUtils.getResourcePath(path);
        if (resourcePath != null) path = resourcePath;

        String[] names = path.split("/");
        if (basePackage == null && names.length <= 1) {
            throw new MojoExecutionException(this, "No package resolved for '" + path + "'",
                    "Either add a basicPackage to plugin configuration, or place the cl programs in nested directories");
        }
        List<String> res = new ArrayList<>(names.length);
        if (basePackage != null)
            res.add(basePackage);
        for (int i = 0; i < names.length - 1; i++)
            res.add(names[i]);
        return String.join(".", res);
    }

    private static final class UncheckedMojoExecutionException extends RuntimeException {
        public UncheckedMojoExecutionException(MojoExecutionException cause) {
            super(cause);
        }

        @Override
        public synchronized MojoExecutionException getCause() {
            return (MojoExecutionException) super.getCause();
        }
    }
}
