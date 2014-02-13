package org.jboss.shrinkwrap.resolver.impl.maven.integration;

import java.io.File;

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
 * Ensures that remote repositories can be added without modifying settings.xml. 
 * This test will fail outside the presence of an internet
 * connection and access to repo1.maven.org
 *
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
public class AdditionalRemoteRepositoryTestCase {

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
        final File file = Maven.resolver().loadPomFromFile("pom.xml").resolve("junit:junit")
            .withClassPathResolution(false).withoutTransitivity().asSingle(File.class);
        // Ensure we get JUnit
        new ValidationUtil("junit").validate(file);
        final File localRepo = new File(FAKE_REPO);
        // Ensure we're pulling from the alternate repo we've designated above
        Assert.assertTrue(file.getAbsolutePath().contains(localRepo.getAbsolutePath()));
    }

    /**
     * Tests the addition of a remote repository
     */
    @Test
    public void shouldFindArtifactWithExplicitRemoteRepository() {
        final File file = Maven.resolver().loadPomFromFile("pom.xml")
                .resolve("org.hornetq:hornetq-core:2.0.0.GA").withClassPathResolution(false)
                .withMavenCentralRepo(false)
                .withMavenRemoteRepo("jboss", "https://repository.jboss.org/nexus/content/repositories/releases/", "default")
                .withoutTransitivity().asSingle(File.class);
        
        final File localRepo = new File(FAKE_REPO);
        // Ensure we're pulling from the alternate repo we've designated above
        Assert.assertTrue(file.getAbsolutePath().contains(localRepo.getAbsolutePath()));
    }

}
