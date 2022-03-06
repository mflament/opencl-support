package org.yah.tools.opencl.mavenplugin;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;

import java.io.File;

public class GenerateBindingMojoTest extends AbstractMojoTestCase {

    public void testGenerate() throws Exception {
        File pom = getTestFile("src/test/resources/test-project/pom.xml");
        assertNotNull(pom);
        assertTrue(pom.exists());

        GenerateBindingMojo generateMojo = (GenerateBindingMojo) lookupMojo("generate", pom);
        assertNotNull(generateMojo);
        generateMojo.execute();
    }
}