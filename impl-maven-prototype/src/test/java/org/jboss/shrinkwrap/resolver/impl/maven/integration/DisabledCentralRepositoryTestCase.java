package org.jboss.shrinkwrap.resolver.impl.maven.integration;

import java.io.File;

import org.jboss.shrinkwrap.resolver.api.NoResolvedResultException;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.impl.maven.bootstrap.MavenSettingsBuilder;
import org.jboss.shrinkwrap.resolver.impl.maven.util.FileUtil;
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

    @BeforeClass
    public static void setRemoteRepository() {
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
        FileUtil.removeDirectory(new File(FAKE_REPO));
    }

    /**
     * Ensures that we can contact Maven Central (as a control test)
     */
    @Test
    public void control() {
        // This should resolve from Maven Central
        final File file = Maven.resolver().configureFromPom("pom.xml").resolve("junit:junit")
            .withClassPathResolution(false).withTransitivity().asSingle(File.class);
        // Ensure we get JUnit
        new ValidationUtil("junit").validate(file);
        final File localRepo = new File(FAKE_REPO);
        // Ensure we're pulling from the alternate repo we've designated above
        Assert.assertTrue(file.getAbsolutePath().contains(localRepo.getAbsolutePath()));
    }

    /**
     * Tests the disabling of the Maven central repository
     */
    @Test(expected = NoResolvedResultException.class)
    public void shouldHaveCentralMavenRepositoryDisabled() {
        // This should resolve from Maven Central
        final File f = Maven.resolver().configureFromPom("pom.xml").resolve("junit:junit")
            .withClassPathResolution(false).withMavenCentralRepo(false).withTransitivity().asSingle(File.class);
        System.out.println(f.getAbsolutePath());
    }

}
