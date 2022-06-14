package org.it;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.soebes.itf.jupiter.extension.MavenJupiterExtension;
import com.soebes.itf.jupiter.extension.MavenTest;
import com.soebes.itf.jupiter.maven.MavenExecutionResult;
import junit.framework.AssertionFailedError;

import java.io.File;
import java.io.FileNotFoundException;

import static com.soebes.itf.extension.assertj.MavenExecutionResultAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

@MavenJupiterExtension
class GenerateBindingMojoIT {

    @MavenTest
    void test_generate_from_resource(MavenExecutionResult result) throws FileNotFoundException {
        assertThat(result).isSuccessful();
        File targetDirectory = result.getMavenProjectResult().getTargetProjectDirectory();
        File baseDir = new File(targetDirectory, "target/generated-sources/opencl-support/org/yah/test/opencl");
        assertThat(new File(baseDir, "Program1.java")).isFile();
        assertThat(new File(baseDir, "kernels/TestKernel.java")).isFile();

        File clProgramFile = new File(baseDir, "cl/CLProgram1.java");
        assertThat(clProgramFile).isFile();
        String path = getProgramFileConstant(clProgramFile);
        assertThat(path).isEqualTo("\"classpath:program_1.cl\"");

        assertThat(new File(baseDir, "cl/kernels/CLTestKernel.java")).isFile();
        assertThat(new File(baseDir, "subpackage/Program2.java")).isFile();
        assertThat(new File(baseDir, "subpackage/kernels/TestKernel.java")).isFile();
        assertThat(new File(baseDir, "subpackage/cl/CLProgram2.java")).isFile();
        assertThat(new File(baseDir, "subpackage/cl/kernels/CLTestKernel.java")).isFile();

        baseDir = new File(targetDirectory, "target/classes/org/yah/test/opencl");
        assertThat(new File(baseDir, "Program1.class")).isFile();
        assertThat(new File(baseDir, "kernels/TestKernel.class")).isFile();
        assertThat(new File(baseDir, "cl/CLProgram1.class")).isFile();
        assertThat(new File(baseDir, "cl/kernels/CLTestKernel.class")).isFile();
        assertThat(new File(baseDir, "subpackage/Program2.class")).isFile();
        assertThat(new File(baseDir, "subpackage/kernels/TestKernel.class")).isFile();
        assertThat(new File(baseDir, "subpackage/cl/CLProgram2.class")).isFile();
        assertThat(new File(baseDir, "subpackage/cl/kernels/CLTestKernel.class")).isFile();
    }

    @MavenTest
    void test_generate_from_file(MavenExecutionResult result) throws FileNotFoundException {
        assertThat(result).isSuccessful();
        File targetDirectory = result.getMavenProjectResult().getTargetProjectDirectory();
        File baseDir = new File(targetDirectory, "target/generated-sources/opencl-support/org/yah/test/opencl");
        assertThat(new File(baseDir, "Program1.java")).isFile();
        assertThat(new File(baseDir, "kernels/TestKernel.java")).isFile();

        File clProgramFile = new File(baseDir, "cl/CLProgram1.java");
        assertThat(clProgramFile).isFile();
        String path = getProgramFileConstant(clProgramFile);
        assertThat(path).isEqualTo("\"./cl/program_1.cl\"");

        assertThat(new File(baseDir, "cl/kernels/CLTestKernel.java")).isFile();
        assertThat(new File(baseDir, "subpackage/Program2.java")).isFile();
        assertThat(new File(baseDir, "subpackage/kernels/TestKernel.java")).isFile();
        assertThat(new File(baseDir, "subpackage/cl/CLProgram2.java")).isFile();
        assertThat(new File(baseDir, "subpackage/cl/kernels/CLTestKernel.java")).isFile();

        baseDir = new File(targetDirectory, "target/classes/org/yah/test/opencl");
        assertThat(new File(baseDir, "Program1.class")).isFile();
        assertThat(new File(baseDir, "kernels/TestKernel.class")).isFile();
        assertThat(new File(baseDir, "cl/CLProgram1.class")).isFile();
        assertThat(new File(baseDir, "cl/kernels/CLTestKernel.class")).isFile();
        assertThat(new File(baseDir, "subpackage/Program2.class")).isFile();
        assertThat(new File(baseDir, "subpackage/kernels/TestKernel.class")).isFile();
        assertThat(new File(baseDir, "subpackage/cl/CLProgram2.class")).isFile();
        assertThat(new File(baseDir, "subpackage/cl/kernels/CLTestKernel.class")).isFile();
    }

    private static String getProgramFileConstant(File clProgramFile) throws FileNotFoundException {
        JavaParser javaParser = new JavaParser();
        CompilationUnit cu = javaParser.parse(clProgramFile).getResult().orElseThrow(() -> new AssertionFailedError("CLProgram does not compile"));
        ClassOrInterfaceDeclaration declaration = cu.getPrimaryType().orElseThrow(IllegalStateException::new).asClassOrInterfaceDeclaration();
        FieldDeclaration pcDecl = declaration.getFields().stream()
                .filter(fd -> fd.getVariable(0).getNameAsString().equals("PROGRAM_PATH"))
                .findFirst().orElseThrow(IllegalStateException::new);
        return pcDecl.getVariable(0).getInitializer()
                .map(Node::toString)
                .orElseThrow(IllegalStateException::new);
    }

}