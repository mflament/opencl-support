package org.it;

import com.soebes.itf.jupiter.extension.MavenJupiterExtension;
import com.soebes.itf.jupiter.extension.MavenTest;
import com.soebes.itf.jupiter.maven.MavenExecutionResult;

import java.io.File;

import static com.soebes.itf.extension.assertj.MavenExecutionResultAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("NewClassNamingConvention")
@MavenJupiterExtension
class GenerateBindingMojoIT {

    @MavenTest
    void test_generate(MavenExecutionResult result) {
        assertThat(result).isSuccessful();
        File targetDirectory = result.getMavenProjectResult().getTargetProjectDirectory();
        File baseDir = new File(targetDirectory, "target/generated-sources/opencl-support/org/yah/test/opencl");
        assertThat(new File(baseDir, "Program1.java")).isFile();
        assertThat(new File(baseDir, "kernels/TestKernel.java")).isFile();
        assertThat(new File(baseDir, "cl/CLProgram1.java")).isFile();
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

}