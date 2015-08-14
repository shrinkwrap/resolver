package org.jboss.shrinkwrap.resolver.impl.maven.integration;

import java.io.File;

import org.jboss.shrinkwrap.resolver.api.InvalidConfigurationFileException;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolverSystem;
import org.jboss.shrinkwrap.resolver.api.maven.ScopeType;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependencies;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependency;
import org.jboss.shrinkwrap.resolver.api.maven.repository.MavenChecksumPolicy;
import org.jboss.shrinkwrap.resolver.api.maven.repository.MavenRemoteRepositories;
import org.jboss.shrinkwrap.resolver.impl.maven.bootstrap.MavenSettingsBuilder;
import org.jboss.shrinkwrap.resolver.impl.maven.util.TestFileUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test Case for SHRINKRES-226 - Loading of the settings.xml configuration has been postponed. This test case aims at
 * (no-)loading of the invalid settings.xml, not at the result of resolving etc...
 *
 * @author <a href="mailto:mjobanek@redhat.com">Matous Jobanek</a>
 */
public class InvalidSettingsTestCase {

    private static final String INVALID_SETTINGS = "target/settings/profiles/settings-invalid.xml";
    private static final String CENTRAL_SETTINGS = "target/settings/profiles/settings-central.xml";
    private static final String FROM_CLASSLOADER = "profiles/settings3-from-classpath.xml";
    private static final String JUNIT_CANONICAL = "junit:junit:4.11";
    private static final File TEST_BOM = new File("target/poms/test-bom.xml");

    @BeforeClass
    public static void setRemoteRepository() {
        System.setProperty(MavenSettingsBuilder.ALT_GLOBAL_SETTINGS_XML_LOCATION, INVALID_SETTINGS);
        System.setProperty(MavenSettingsBuilder.ALT_USER_SETTINGS_XML_LOCATION, INVALID_SETTINGS);
    }

    private final MavenDependency dependency = MavenDependencies.createDependency(
        "org.jboss.shrinkwrap.test:test-dependency-test:1.0.0", ScopeType.TEST, false);

    /**
     * Cleanup, remove the repositories from previous tests
     */
    @Before
    @After
    // For debugging you might want to temporarily remove the @After lifecycle call just to sanity-check for yourself
    // the repo
    public void cleanup() throws Exception {
        TestFileUtil.removeDirectory(new File("target/local-only-repository"));
    }

    /**
     * Testing methods, that doesn't need to have loaded configuration for its calling
     * During this test the settings-invalid.xml shouldn't be loaded so no exception should be thrown
     * <br/>
     * Uses method Maven.configureResolver()
     */
    @Test
    public void shouldNotLoadInvalidSettingsCR() {
        Maven.configureResolver().withRemoteRepo(MavenRemoteRepositories.createRemoteRepository("jboss",
            "https://repository.jboss.org/nexus/content/repositories/releases/", "default")
            .setChecksumPolicy(MavenChecksumPolicy.CHECKSUM_POLICY_IGNORE));

        Maven.configureResolver().workOffline();
        Maven.configureResolver().workOffline(true);
        Maven.configureResolver().useLegacyLocalRepo(true);
        Maven.configureResolver().withClassPathResolution(false);
        Maven.configureResolver().withMavenCentralRepo(false);
        Maven.configureResolver().addDependency(dependency);
    }

    /**
     * Testing methods, that doesn't need to have loaded configuration for its calling
     * During this test the settings-invalid.xml shouldn't be loaded so no exception should be thrown
     * <br/>
     * Uses method Maven.resolver()
     */
    @Test
    public void shouldNotLoadInvalidSettingsR() {
        Maven.resolver().offline();
        Maven.resolver().offline(true);
        Maven.resolver().addDependency(dependency);
    }

    /**
     * In this test the settings-invalid.xml should be loaded during the
     * <code>resolve("junit:junit:4.11").withTransitivity()</code> phase,
     * for this reason the InvalidConfigurationFileException should be thrown
     * <br/>
     * Uses method Maven.configureResolver()
     */
    @Test(expected = InvalidConfigurationFileException.class)
    public void shouldLoadInvalidSettingsDueResolvingCR() {
        Maven.configureResolver().resolve(JUNIT_CANONICAL).withTransitivity();
    }

    /**
     * In this test the settings-invalid.xml should be loaded during the
     * <code>resolve("junit:junit:4.11").withTransitivity()</code> phase,
     * for this reason the InvalidConfigurationFileException should be thrown
     * <br/>
     * Uses method Maven.resolver()
     */
    @Test(expected = InvalidConfigurationFileException.class)
    public void shouldLoadInvalidSettingsDueResolvingR() {
        Maven.resolver().resolve(JUNIT_CANONICAL).withTransitivity();
    }

    /**
     * In this test the settings-invalid.xml should be loaded during the
     * <code>resolveVersionRange("junit:junit")</code> phase,
     * for this reason the InvalidConfigurationFileException should be thrown
     * <br/>
     * Uses method Maven.configureResolver()
     */
    @Test(expected = InvalidConfigurationFileException.class)
    public void shouldLoadInvalidSettingsDueResolvingVersionsCR() {
        Maven.configureResolver().resolveVersionRange(JUNIT_CANONICAL);
    }

    /**
     * In this test the settings-invalid.xml should be loaded during the
     * <code>resolveVersionRange("junit:junit")</code> phase,
     * for this reason the InvalidConfigurationFileException should be thrown
     * <br/>
     * Uses method Maven.resolver()
     */
    @Test(expected = InvalidConfigurationFileException.class)
    public void shouldLoadInvalidSettingsDueResolvingVersionsR() {
        Maven.resolver().resolveVersionRange(JUNIT_CANONICAL);
    }

    /**
     * In this test the settings-invalid.xml should be loaded during the
     * <code>loadPomFromFile(new File("target/poms/test-bom.xml"))</code> phase,
     * for this reason the InvalidConfigurationFileException should be thrown
     * <br/>
     * Uses method Maven.configureResolver()
     */
    @Test(expected = InvalidConfigurationFileException.class)
    public void shouldLoadInvalidSettingsDueLoadingPomCR() {
        Maven.configureResolver().loadPomFromFile(TEST_BOM);
    }

    /**
     * In this test the settings-invalid.xml should be loaded during the
     * <code>loadPomFromFile(new File("target/poms/test-bom.xml")</code> phase,
     * for this reason the InvalidConfigurationFileException should be thrown
     * <br/>
     * Uses method Maven.resolver()
     */
    @Test(expected = InvalidConfigurationFileException.class)
    public void shouldLoadInvalidSettingsDueLoadingPomR() {
        Maven.resolver().loadPomFromFile(TEST_BOM);
    }

    /**
     * During this test the settings-invalid.xml should NOT be loaded, but the settings-central.xml should.
     * No exception should be thrown.
     * <br/>
     * Uses method Maven.configureResolver()
     */
    @Test
    public void shouldLoadCentralSettingsFromFileCR() {
        // from file
        MavenResolverSystem centralFromFile = Maven.configureResolver().fromFile(CENTRAL_SETTINGS);
        shouldResolveAndLoadPom(centralFromFile);

        // configure from file
        MavenResolverSystem centralConfigureFromFile = Maven.configureResolver().configureFromFile(CENTRAL_SETTINGS);
        shouldResolveAndLoadPom(centralConfigureFromFile);

        //from classloader resource
        MavenResolverSystem fromClassloaderRes =
            Maven.configureResolver().fromClassloaderResource(FROM_CLASSLOADER);
        shouldResolveAndLoadPom(fromClassloaderRes);

        // configure from classloader resource
        MavenResolverSystem configFromClassloaderRes =
            Maven.configureResolver().configureFromClassloaderResource(FROM_CLASSLOADER);
        shouldResolveAndLoadPom(configFromClassloaderRes);
    }

    private void shouldResolveAndLoadPom(MavenResolverSystem mavenResolverSystem) {
        mavenResolverSystem.resolve(JUNIT_CANONICAL).withTransitivity();
        mavenResolverSystem.loadPomFromFile(TEST_BOM);
    }

}
