package org.jboss.shrinkwrap.resolver.impl.maven.integration;

import java.io.File;

import org.jboss.shrinkwrap.resolver.api.InvalidConfigurationFileException;
import org.jboss.shrinkwrap.resolver.api.NoResolvedResultException;
import org.jboss.shrinkwrap.resolver.api.Resolvers;
import org.jboss.shrinkwrap.resolver.api.maven.ConfigurableMavenResolverSystem;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.repository.MavenChecksumPolicy;
import org.jboss.shrinkwrap.resolver.api.maven.repository.MavenRemoteRepositories;
import org.jboss.shrinkwrap.resolver.impl.maven.bootstrap.MavenSettingsBuilder;
import org.jboss.shrinkwrap.resolver.impl.maven.util.TestFileUtil;
import org.jboss.shrinkwrap.resolver.impl.maven.util.ValidationUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Ensures that remote repositories can be added without modifying settings.xml. This test will fail outside the presence of
 * an internet connection and access to repo1.maven.org as well as repository.jboss.org
 *
 * @author <a href="mailto:marsu_pilami@msn.com">Marc-Antoine Gouillart</a>
 */
class AdditionalRemoteRepositoryTestCase {

    private static final String FAKE_REPO = "target/disabled-central-repo";
    private static final String FAKE_SETTINGS = "target/settings/profile/settings.xml";

    @BeforeAll
    static void setRemoteRepository() {
        System.setProperty(MavenSettingsBuilder.ALT_GLOBAL_SETTINGS_XML_LOCATION, FAKE_SETTINGS);
        System.setProperty(MavenSettingsBuilder.ALT_USER_SETTINGS_XML_LOCATION, FAKE_SETTINGS);
        System.setProperty(MavenSettingsBuilder.ALT_LOCAL_REPOSITORY_LOCATION, FAKE_REPO);
    }

    /**
     * Cleanup, remove the repositories from previous tests
     */
    @BeforeEach
    @AfterEach
    // For debugging, you might want to temporarily remove the @After lifecycle call just to sanity-check for yourself
    // the repo
    void cleanup() throws Exception {
        TestFileUtil.removeDirectory(new File(FAKE_REPO));
    }

    /**
     * Ensures that we can contact Maven Central, i.e. web is accessible (as a control test)
     */
    @Test
    void control1() {
        // This should resolve from Maven Central
        final File file = Maven.configureResolver().withClassPathResolution(false).resolve("org.junit.jupiter:junit-jupiter:5.11.3")
                .withoutTransitivity().asSingle(File.class);
        // Ensure we get JUnit
        new ValidationUtil("junit-jupiter").validate(file);
        final File localRepo = new File(FAKE_REPO);
        // Ensure we're pulling from the alternate repo we've designated above
        Assertions.assertTrue(file.getAbsolutePath().contains(localRepo.getAbsolutePath()));
    }

    /**
     * Ensures the artifact we will fetch in the actual tests is no present in any default repositories.
     */
    @Test
    void control2() {
        Assertions.assertThrows(NoResolvedResultException.class, () -> {
            final File file = Maven.configureResolver().withClassPathResolution(false).withMavenCentralRepo(false)
                    .resolve("org.junit.jupiter:junit-jupiter:5.11.3").withoutTransitivity().asSingle(File.class);
            new ValidationUtil("junit-jupiter").validate(file);
            final File localRepo = new File(FAKE_REPO);
            Assertions.assertTrue(file.getAbsolutePath().contains(localRepo.getAbsolutePath()));
        });
    }

    /**
     * Ensures we can resolve an artifact from an explicitly pom-declared repository (this repo will be overloaded in a test)
     */
    @Test
    void control3() {
        File[] files = Resolvers.use(ConfigurableMavenResolverSystem.class).withMavenCentralRepo(false).loadPomFromFile(
            "target/poms/test-remote-overload.xml").importCompileAndRuntimeDependencies().resolve().withTransitivity()
            .as(File.class);

        Assertions.assertNotEquals(0, files.length, "there were 0 dependencies!");
    }

    /**
     * Ensures the artifact we will fetch in the actual tests is no present in any default repositories.
     */
    @Test
    void control4() {
        Assertions.assertThrows(InvalidConfigurationFileException.class, () -> {
            final File file = Maven.configureResolver().withClassPathResolution(false)
                    .withMavenCentralRepo(false).loadPomFromFile("pom.xml").resolve("org.junit.jupiter:junit-jupiter").withoutTransitivity().asSingle(File.class);
            new ValidationUtil("junit-jupiter").validate(file);
            final File localRepo = new File(FAKE_REPO);
            Assertions.assertTrue(file.getAbsolutePath().contains(localRepo.getAbsolutePath()));
        });
    }

    /**
     * Tests the addition of a remote repository
     */
    @Test
    void shouldFindArtifactWithExplicitRemoteRepository() {
        final File file = Maven.configureResolver()
                .withClassPathResolution(false).withMavenCentralRepo(false)
                .withRemoteRepo("jboss", "https://repository.jboss.org/nexus/content/repositories/releases/", "default")
                .resolve("org.hornetq:hornetq-core:2.0.0.GA")
                .withoutTransitivity().asSingle(File.class);

        final File localRepo = new File(FAKE_REPO);
        // Ensure we're pulling from the alternate repo we've designated above
        Assertions.assertTrue(file.getAbsolutePath().contains(localRepo.getAbsolutePath()));
    }

    /**
     * Tests the addition of a remote repository through the builder API
     */
    @Test
    void shouldFindArtifactWithExplicitRemoteRepositoryBuilder() {
        final File file = Maven
                .configureResolver()
                .withClassPathResolution(false)
                .withMavenCentralRepo(false)
                .withRemoteRepo(
                        MavenRemoteRepositories.createRemoteRepository("jboss",
                                "https://repository.jboss.org/nexus/content/repositories/releases/", "default")
                                .setChecksumPolicy(MavenChecksumPolicy.CHECKSUM_POLICY_IGNORE))
                .resolve("org.hornetq:hornetq-core:2.0.0.GA")
                .withoutTransitivity()
                .asSingle(File.class);

        final File localRepo = new File(FAKE_REPO);
        // Ensure we're pulling from the alternate repo we've designated above
        Assertions.assertTrue(file.getAbsolutePath().contains(localRepo.getAbsolutePath()));
    }

    /**
     * Test behaviour with an invalid URL
     */
    @Test
    void shouldThrowIllegalArgumentException4() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Maven.configureResolver()
                    .withClassPathResolution(false).withMavenCentralRepo(false)
                    .withRemoteRepo("jboss", "wrong://repository.jboss.org/nexus/content/repositories/releases/", "default")
                    .loadPomFromFile("pom.xml").resolve("org.hornetq:hornetq-core:2.0.0.GA")
                    .withoutTransitivity().asSingle(File.class);
        });
    }

    /**
     * Test behaviour with a wrong URL
     */
    @Test
    void shouldThrowNoResolvedResultException() {
        Assertions.assertThrows(NoResolvedResultException.class, () -> {
            Maven.configureResolver()
                    .withClassPathResolution(false).withMavenCentralRepo(false)
                    .withRemoteRepo("jboss", "https://repository123.jboss.org/nexus/content/repositories/releases/", "default")
                    .resolve("org.hornetq:hornetq-core:2.0.0.GA")
                    .withoutTransitivity().asSingle(File.class);
        });
    }

    /**
     * Test behaviour with a null repository ID
     */
    @Test
    void shouldThrowIllegalArgumentException1() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Maven.configureResolver()
                    .withClassPathResolution(false).withMavenCentralRepo(false)
                    .withRemoteRepo(null, "https://repository.jboss.org/nexus/content/repositories/releases/", "default")
                    .loadPomFromFile("pom.xml").resolve("org.hornetq:hornetq-core:2.0.0.GA")
                    .withoutTransitivity().asSingle(File.class);
        });
    }

    /**
     * Test behaviour with a null URL
     */
    @Test
    void shouldThrowIllegalArgumentException5() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Maven.configureResolver()
                    .withClassPathResolution(false).withMavenCentralRepo(false)
                    .withRemoteRepo("jboss", (String) null, "default").loadPomFromFile("pom.xml")
                    .resolve("org.hornetq:hornetq-core:2.0.0.GA")
                    .withoutTransitivity().asSingle(File.class);
        });
    }

    /**
     * Test behaviour with a non default layout (which is impossible in Maven 3)
     */
    @Test()
    void shouldThrowIllegalArgumentException2() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Maven.configureResolver()
                    .withClassPathResolution(false).withMavenCentralRepo(false)
                    .withRemoteRepo("jboss", "https://repository.jboss.org/nexus/content/repositories/releases/", "legacy")
                    .loadPomFromFile("pom.xml").resolve("org.hornetq:hornetq-core:2.0.0.GA")
                    .withoutTransitivity().asSingle(File.class);
        });
    }

    /**
     * Test behaviour with a null layout
     */
    @Test()
    void shouldThrowIllegalArgumentException3() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
        Maven.configureResolver()
                .withClassPathResolution(false).withMavenCentralRepo(false)
                .withRemoteRepo("jboss", "https://repository.jboss.org/nexus/content/repositories/releases/", null)
                .loadPomFromFile("pom.xml").resolve("org.hornetq:hornetq-core:2.0.0.GA")
                .withoutTransitivity().asSingle(File.class);
        });
    }

    /**
     * This test overloads a valid (tested in a control test) repository by a non-working repository. Artifact should
     * therefore not resolve.
     */
    @Test
    void shouldOverloadRepository() {
        Assertions.assertThrows(NoResolvedResultException.class, () -> {
            Resolvers.use(ConfigurableMavenResolverSystem.class)
                    .withMavenCentralRepo(false)
                    .withRemoteRepo("test-repository", "http://127.0.0.1", "default")
                    .withClassPathResolution(false)
                    .loadPomFromFile("target/poms/test-remote-overload.xml")
                    .importCompileAndRuntimeDependencies().resolve()
                    .withTransitivity().as(File.class);
        });
    }

    /**
     * This test overloads Maven Central repository
     *
     */
    @Test
    void shouldOverloadCentral() {
        Assertions.assertThrows(NoResolvedResultException.class, () -> {
            Maven.configureResolver()
                    .withRemoteRepo("central", "http://127.0.0.1", "default")
                    .withClassPathResolution(false).resolve("org.junit.jupiter:junit-jupiter:5.11.3")
                    .withTransitivity().as(File.class);
        });
    }

}
