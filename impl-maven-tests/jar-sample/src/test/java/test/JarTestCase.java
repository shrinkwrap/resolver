package test;

import junit.framework.Assert;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.resolver.api.maven.MavenImporter;
import org.junit.Test;

public class JarTestCase {

    @Test
    public void testJar() {
        JavaArchive archive = ShrinkWrap.create(MavenImporter.class).loadEffectivePom("pom.xml").importBuildOutput()
                .as(JavaArchive.class);

        System.out.println(archive.toString(true));

        Assert.assertNotNull("Archive is not null", archive);
        Assert.assertTrue("Archive contains jar class", archive.contains("test/JarClass.class"));
    }

    @Test
    public void testJarWithTestClasses() {
        JavaArchive archive = ShrinkWrap.create(MavenImporter.class).loadEffectivePom("pom.xml").importBuildOutput()
                .importTestBuildOutput().as(JavaArchive.class);

        System.out.println(archive.toString(true));

        Assert.assertNotNull("Archive is not null", archive);
        Assert.assertTrue("Archive contains jar class", archive.contains("test/JarClass.class"));
        Assert.assertTrue("Archive contains jar test class", archive.contains("test/JarTestCase.class"));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testJarWithTestArtifacts() {
        ShrinkWrap.create(MavenImporter.class).loadEffectivePom("pom.xml").importBuildOutput().importTestBuildOutput()
                .importTestDependencies().as(JavaArchive.class);

        Assert.fail("UnsupportedOperationException should have been thrown for jar packaging");
    }
}
