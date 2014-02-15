package org.jboss.shrinkwrap.resolver.impl.maven.integration;

import java.io.File;
import java.net.MalformedURLException;

import org.jboss.shrinkwrap.resolver.api.NoResolvedResultException;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.repository.MavenChecksumPolicy;
import org.jboss.shrinkwrap.resolver.api.maven.repository.MavenRemoteRepositories;
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
     * Ensures that we can contact Maven Central, i.e. web is accessible (as a control test)
     */
    @Test
    public void control1() {
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
     * Ensures the artifact we will fetch in the actual tests is no present in any default repositories.
     */
    @Test(expected = NoResolvedResultException.class)
    public void control2() {
        final File file = Maven.resolver().loadPomFromFile("pom.xml").resolve("junit:junit")
            .withClassPathResolution(false).withMavenCentralRepo(false).withoutTransitivity().asSingle(File.class);
        new ValidationUtil("junit").validate(file);
        final File localRepo = new File(FAKE_REPO);
        Assert.assertTrue(file.getAbsolutePath().contains(localRepo.getAbsolutePath()));
    }

    /**
     * Tests the addition of a remote repository
     */
    @Test
    public void shouldFindArtifactWithExplicitRemoteRepository() throws Exception {
        final File file = Maven.resolver().loadPomFromFile("pom.xml")
                .resolve("org.hornetq:hornetq-core:2.0.0.GA").withClassPathResolution(false)
                .withMavenCentralRepo(false)
                .withRemoteRepo("jboss", "https://repository.jboss.org/nexus/content/repositories/releases/", "default")
                .withoutTransitivity().asSingle(File.class);
        
        final File localRepo = new File(FAKE_REPO);
        // Ensure we're pulling from the alternate repo we've designated above
        Assert.assertTrue(file.getAbsolutePath().contains(localRepo.getAbsolutePath()));
    }
    
    /**
     * Tests the addition of a remote repository through the builder API
     */
    @Test
    public void shouldFindArtifactWithExplicitRemoteRepositoryBuilder() throws Exception {
        final File file = Maven.resolver().loadPomFromFile("pom.xml")
                .resolve("org.hornetq:hornetq-core:2.0.0.GA").withClassPathResolution(false)
                .withMavenCentralRepo(false)
                .withRemoteRepo(MavenRemoteRepositories.createRemoteRepository( "jboss", "https://repository.jboss.org/nexus/content/repositories/releases/", "default").setChecksumPolicy(MavenChecksumPolicy.CHECKSUM_POLICY_IGNORE))
                .withoutTransitivity().asSingle(File.class);
        
        final File localRepo = new File(FAKE_REPO);
        // Ensure we're pulling from the alternate repo we've designated above
        Assert.assertTrue(file.getAbsolutePath().contains(localRepo.getAbsolutePath()));
    }

    /**
     * Test behaviour with an invalid URL
     */
    @Test(expected = MalformedURLException.class)
    public void shouldThrowMalformedURLException() throws Exception {
    	Maven.resolver().loadPomFromFile("pom.xml")
                .resolve("org.hornetq:hornetq-core:2.0.0.GA").withClassPathResolution(false)
                .withMavenCentralRepo(false)
                .withRemoteRepo("jboss", "wrong://repository.jboss.org/nexus/content/repositories/releases/", "default")
                .withoutTransitivity().asSingle(File.class);
    }

    /**
     * Test behaviour with a wrong URL
     */
    @Test(expected = NoResolvedResultException.class)
    public void shouldThrowNoResolvedResultException() throws Exception {
    	Maven.resolver().loadPomFromFile("pom.xml")
                .resolve("org.hornetq:hornetq-core:2.0.0.GA").withClassPathResolution(false)
                .withMavenCentralRepo(false)
                .withRemoteRepo("jboss", "https://repository123.jboss.org/nexus/content/repositories/releases/", "default")
                .withoutTransitivity().asSingle(File.class);
    }
    
    /**
     * Test behaviour with a null repository ID
     */
    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentException1() throws Exception {
    	Maven.resolver().loadPomFromFile("pom.xml")
                .resolve("org.hornetq:hornetq-core:2.0.0.GA").withClassPathResolution(false)
                .withMavenCentralRepo(false)
                .withRemoteRepo(null, "https://repository.jboss.org/nexus/content/repositories/releases/", "default")
                .withoutTransitivity().asSingle(File.class);
    }
    
    /**
     * Test behaviour with a null URL
     */
    @Test(expected = MalformedURLException.class)
    public void shouldThrowMalformedURLException2() throws Exception {
    	Maven.resolver().loadPomFromFile("pom.xml")
                .resolve("org.hornetq:hornetq-core:2.0.0.GA").withClassPathResolution(false)
                .withMavenCentralRepo(false)
                .withRemoteRepo("jboss", (String)null, "default")
                .withoutTransitivity().asSingle(File.class);
    }
    
    /**
     * Test behaviour with a non default layout (which is impossible in Maven 3)
     */
    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentException2() throws Exception {
    	Maven.resolver().loadPomFromFile("pom.xml")
                .resolve("org.hornetq:hornetq-core:2.0.0.GA").withClassPathResolution(false)
                .withMavenCentralRepo(false)
                .withRemoteRepo("jboss", "https://repository.jboss.org/nexus/content/repositories/releases/", "legacy")
                .withoutTransitivity().asSingle(File.class);
    }
    
    /**
     * Test behaviour with a null layout
     */
    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentException3() throws Exception {
    	Maven.resolver().loadPomFromFile("pom.xml")
                .resolve("org.hornetq:hornetq-core:2.0.0.GA").withClassPathResolution(false)
                .withMavenCentralRepo(false)
                .withRemoteRepo("jboss", "https://repository.jboss.org/nexus/content/repositories/releases/", null)
                .withoutTransitivity().asSingle(File.class);
    }
    
    
}
