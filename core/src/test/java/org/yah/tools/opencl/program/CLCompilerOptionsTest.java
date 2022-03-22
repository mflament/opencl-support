package org.yah.tools.opencl.program;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class CLCompilerOptionsTest {

    @Test
    void parse() {
        CLCompilerOptions options = CLCompilerOptions.parse("");
        assertThat(options).usingRecursiveComparison().isEqualTo(new CLCompilerOptions());
        assertThat(new CLCompilerOptions().toString()).isEqualTo("");

        options = CLCompilerOptions.parse("-D novalue -D withvalue=thevalue " +
                "-I some/dir -I some/other/dir " +
                "-cl-single-precision-constant " +
                "-cl-denorms-are-zero " +
                "-cl-fp32-correctly-rounded-divide-sqrt " +
                "-cl-opt-disable " +
                "-cl-mad-enable " +
                "-cl-no-signed-zeros " +
                "-cl-unsafe-math-optimizations " +
                "-cl-finite-math-only " +
                "-cl-fast-relaxed-math " +
                "-w " +
                "-cl-kernel-arg-info " +
                "-Werror " +
                "-cl-std=CL1.2");

        Map<String, String> macros = options.getMacros();
        assertThat(macros).hasSize(2);
        assertThat(macros).containsEntry("novalue", null);
        assertThat(macros).containsEntry("withvalue", "thevalue");

        assertThat(options.getIncludes()).isEqualTo(Arrays.asList("some/dir", "some/other/dir"));

        assertThat(options.isSinglePrecisionConstant()).isTrue();
        assertThat(options.isDenormsAreZero()).isTrue();
        assertThat(options.isFp32CorrectlyRoundedDivideSqrt()).isTrue();
        assertThat(options.isOptDisable()).isTrue();
        assertThat(options.isMadEnable()).isTrue();
        assertThat(options.isNoSignedZeros()).isTrue();
        assertThat(options.isUnsafeMathOptimizations()).isTrue();
        assertThat(options.isFiniteMathOnly()).isTrue();
        assertThat(options.isFastRelaxedMath()).isTrue();
        assertThat(options.isNoWarning()).isTrue();
        assertThat(options.isWarningIsError()).isTrue();
        assertThat(options.getClStd()).isEqualTo("CL1.2");
    }


    @Test
    void testToString() {
    }

}