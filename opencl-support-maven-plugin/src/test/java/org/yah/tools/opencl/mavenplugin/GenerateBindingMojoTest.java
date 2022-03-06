package org.yah.tools.opencl.mavenplugin;

import org.apache.maven.model.Resource;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Collections;

class GenerateBindingMojoTest {

    @Test
    void testGenerate() throws Exception {
        GenerateBindingMojo mojo = new GenerateBindingMojo();
        mojo.setDirectories(Collections.singletonList(new File("src/it/test_project/src/main/resources/cl")));
        mojo.setBasePackage("org.yah.test");
        mojo.setOutputDirectory(new File("target/tests/bindings"));

        MavenProject mavenProject = new MavenProject();
        Resource resource = new Resource();
        resource.setDirectory("src/main/resources");
        mavenProject.addResource(resource);
        mojo.setProject(mavenProject);

        mojo.execute();
    }
}