package org.jboss.shrinkwrap.resolver.impl.maven.embedded;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * Sets up an exclusive working directory for each test method. {@link #prepareProject(String)} can then be used
 * by the test method to copy the entire project of the given test pom file to that working directory.
 * <p/>
 * Example: A test class {@code org.jboss.shrinkwrap.resolver.impl.maven.embedded.foo.SomeTestCase} with test method
 * {@code someTestMethod} using {@code "src/it/jar-sample/pom.xml"} will receive the following working directory:
 * <pre>
 * target/.foo.SomeTestCase/someTestMethod/jar-sample
 * </pre>
 *
 * @author <a href="https://github.com/famod">Falko Modler</a>
 */
public class TestWorkDirExtension implements BeforeEachCallback {

    private static final int THIS_PCKG_LENGTH = TestWorkDirExtension.class.getPackage().getName().length();

    private File workDir;

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        final String qualifiedClassName = context.getRequiredTestClass().getName().substring(THIS_PCKG_LENGTH);
        final String methodName = context.getRequiredTestMethod().getName();
        workDir = new File("target/" + qualifiedClassName + "/" + methodName);
        if (workDir.exists()) {
            FileUtils.cleanDirectory(workDir);
        } else if (!workDir.mkdirs()) {
            throw new IllegalStateException("Could not create " + workDir);
        }
    }

    public File prepareProject(String pathToSrcPomFile) {
        final File srcPomFile = new File(pathToSrcPomFile);
        final File srcPomFileParent = srcPomFile.getParentFile();
        final File pomFile = new File(new File(workDir, srcPomFileParent.getName()), srcPomFile.getName());
        try {
            FileUtils.copyDirectoryToDirectory(srcPomFileParent, workDir);
        } catch (IOException e) {
            throw new IllegalStateException("prepareProject failed for: " + pathToSrcPomFile, e);
        }
        return pomFile;
    }
}
