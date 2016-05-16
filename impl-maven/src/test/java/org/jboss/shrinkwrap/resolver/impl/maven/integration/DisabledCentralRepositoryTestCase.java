package org.jboss.shrinkwrap.resolver.impl.maven.integration;

import java.io.File;

import org.jboss.shrinkwrap.resolver.api.InvalidConfigurationFileException;
import org.jboss.shrinkwrap.resolver.api.NoResolvedResultException;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.impl.maven.bootstrap.MavenSettingsBuilder;
import org.jboss.shrinkwrap.resolver.impl.maven.util.TestFileUtil;
import org.jboss.shrinkwrap.resolver.impl.maven.util.ValidationUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Ensures that Maven Central may be disabled as a repository. This test will fail outside the presence of an internet
 * connection and access to repo1.maven.org
 *
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
public class DisabledCentralRepositoryTestCase {

    private static final String FAKE_REPO = "target/disabled-central-repo";
    private static final String FAKE_SETTINGS = "target/settings/profile/settings.xml";

    @BeforeClass
    public static void setRemoteRepository() {
        System.setProperty(MavenSettingsBuilder.ALT_GLOBAL_SETTINGS_XML_LOCATION, FAKE_SETTINGS);
        System.setProperty(MavenSettingsBuilder.ALT_USER_SETTINGS_XML_LOCATION, FAKE_SETTINGS);
        System.setProperty(MavenSettingsBuilder.ALT_LOCAL_REPOSITORY_LOCATION, FAKE_REPO);
    }

    /**
     * Cleanup, remove the repositories from previous tests
     */
    @Before
    @After
    // For debugging you might want to temporarily remove the @After lifecycle call just to sanity-check for yourself
    // the repo
    public void cleanup() throws Exception {
        TestFileUtil.removeDirectory(new File(FAKE_REPO));
    }

    /**
     * Ensures that we can contact Maven Central (as a control test)
     */
    @Test
    public void control() {
        // This should resolve from Maven Central
        final File file =
            Maven.configureResolver().withClassPathResolution(false).loadPomFromFile("pom.xml").resolve("junit:junit")
                .withoutTransitivity().asSingle(File.class);
        // Ensure we get JUnit
        new ValidationUtil("junit").validate(file);
        final File localRepo = new File(FAKE_REPO);
        // Ensure we're pulling from the alternate repo we've designated above
        Assert.assertTrue(file.getAbsolutePath().contains(localRepo.getAbsolutePath()));
    }

    /**
     * Ensures that we can contact Maven Central (as a control test)
     */
    @Test
    public void controlWithNewAPI() {
        // This should resolve from Maven Central
        final File file = Maven.configureResolver().withClassPathResolution(false).loadPomFromFile("pom.xml").resolve("junit:junit")
            .withoutTransitivity().asSingle(File.class);
        // Ensure we get JUnit
        new ValidationUtil("junit").validate(file);
        final File localRepo = new File(FAKE_REPO);
        // Ensure we're pulling from the alternate repo we've designated above
        Assert.assertTrue(file.getAbsolutePath().contains(localRepo.getAbsolutePath()));
    }

    /**
     * Tests the disabling of the Maven central repository and class path resolution when loading root pom file
     */
    @Test(expected = InvalidConfigurationFileException.class)
    public void shouldHaveCentralMavenRepositoryAndClassPathResolutionDisabledWhenLoadingRootPom() {
        // This should NOT connect to Maven Central and not use the class path resolution and therefore should not load the pom file
        Maven.configureResolver().withClassPathResolution(false).withMavenCentralRepo(false).loadPomFromFile("pom.xml");
    }

    /**
     * Tests the disabling of the Maven central repository and class path resolution
     */
    @Test(expected = NoResolvedResultException.class)
    public void shouldHaveCentralMavenRepositoryAndClassPathResolutionDisabled() {
        // This should resolve neither from Maven Central nor from class path
        Maven.configureResolver().withClassPathResolution(false).withMavenCentralRepo(false)
            .resolve("junit:junit:4.11").withoutTransitivity().asSingle(File.class);
    }

    /**
     * Tests the disabling of the Maven central repository and class path resolution when loading simple pom file
     */
    @Test(expected = NoResolvedResultException.class)
    public void shouldHaveCentralMavenRepositoryAndClassPathResolutionDisabledWhenLoadingSimplePom() {
        // This should resolve neither from Maven Central nor from class path
        Maven.configureResolver().withClassPathResolution(false).withMavenCentralRepo(false).loadPomFromFile(
            "target/poms/test-junit.xml").resolve("junit:junit").withoutTransitivity().asSingle(File.class);
    }
}
