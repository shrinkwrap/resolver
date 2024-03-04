/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.shrinkwrap.resolver.impl.maven.integration;

import java.io.File;

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
 * Ensures that artifact is correctly resolved from local repository.
 * This test will fail outside the presence of an internet connection and access to repo1.maven.org
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 */
public class LocalRepositoryTestCase {

    private static final String LOCAL_REPOSITORY = "target/local-only-repository";
    private static final String REMOTE_ENABLED_SETTINGS = "target/settings/profiles/settings.xml";
    private static final String CENTRAL_ONLY_SETTINGS = "target/settings/profiles/settings-central.xml";

    @BeforeClass
    public static void setRemoteRepository() {
        System.setProperty(MavenSettingsBuilder.ALT_LOCAL_REPOSITORY_LOCATION, LOCAL_REPOSITORY);
    }

    /**
     * Cleanup, remove the repositories from previous tests
     */
    @Before
    @After
    // For debugging you might want to temporarily remove the @After lifecycle call just to sanity-check for yourself
    // the repo
    public void cleanup() throws Exception {
        TestFileUtil.removeDirectory(new File(LOCAL_REPOSITORY));
    }

    /**
     * Ensures that we can contact Maven Central (as a control test)
     */
    @Test
    public void resolveFromLocalRepository() throws Exception {

        // fixture
        prepareLocalRepository();

        // now, we need to remove Maven repository checks
        // https://cwiki.apache.org/confluence/display/MAVEN/Maven+3.x+Compatibility+Notes#Maven3.xCompatibilityNotes-ResolutionfromLocalRepository
        // this behavior can be switched using non-tracking LocalRepositoryManager
        // the other way is to ensure
        TestFileUtil.removeFilesRecursively(new File(LOCAL_REPOSITORY), "_maven.repositories");
        // Maven 3.1.0 renamed tracking file to _remote.repositories
        TestFileUtil.removeFilesRecursively(new File(LOCAL_REPOSITORY), "_remote.repositories");

        // now, we disable remote repository and rely only on what's available in local repository
        File[] filesLocal = Maven.configureResolver().fromFile(CENTRAL_ONLY_SETTINGS)
                .resolve("org.jboss.shrinkwrap.test:test-deps-c:1.0.0")
                .withTransitivity().as(File.class);

        ValidationUtil.fromDependencyTree(new File("src/test/resources/dependency-trees/test-deps-c.tree")).validate(
                filesLocal);
    }

    /**
     * Sets legacy local repository
     */
    @Test
    public void legacyLocalRepository() {

        // fixture
        prepareLocalRepository();

        // now, we disable remote repository and rely only on what's available in local repository
        File[] filesLocal = Maven.configureResolver().useLegacyLocalRepo(true).fromFile(CENTRAL_ONLY_SETTINGS)
                .resolve("org.jboss.shrinkwrap.test:test-deps-c:1.0.0")
                .withTransitivity().as(File.class);

        ValidationUtil.fromDependencyTree(new File("src/test/resources/dependency-trees/test-deps-c.tree")).validate(
                filesLocal);
    }

    /**
     * Sets legacy local repository via property
     */
    @Test
    public void legacyLocalRepositoryViaProperty() {

        // fixture
        prepareLocalRepository();

        try {
            System.setProperty("maven.legacyLocalRepo", "true");
            // now, we disable remote repository and rely only on what's available in local repository
            File[] filesLocal = Maven.configureResolver().fromFile(CENTRAL_ONLY_SETTINGS)
                    .resolve("org.jboss.shrinkwrap.test:test-deps-c:1.0.0")
                    .withTransitivity().as(File.class);

            ValidationUtil.fromDependencyTree(new File("src/test/resources/dependency-trees/test-deps-c.tree")).validate(
                    filesLocal);
        } finally {
            System.clearProperty("maven.legacyLocalRepo");
        }
    }

    /**
     * Ensures that we can contact Maven Central (as a control test)
     */
    @Test
    public void resolveFromLocalRepositoryOffline() {

        // fixture
        prepareLocalRepository();

        // now, we disable remote repository and rely only on what's available in local repository
        // test will work in offline mode, so it won't touch remote repositories
        File[] filesLocal = Maven.configureResolver().workOffline().fromFile(CENTRAL_ONLY_SETTINGS)
                .resolve("org.jboss.shrinkwrap.test:test-deps-c:1.0.0")
                .withTransitivity().as(File.class);

        ValidationUtil.fromDependencyTree(new File("src/test/resources/dependency-trees/test-deps-c.tree")).validate(
                filesLocal);
    }

    /**
     * Ensures that we can contact Maven Central (as a control test)
     */
    @Test(expected = NoResolvedResultException.class)
    public void resolveFromLocalRepositoryTrackingRemotes() {

        // fixture
        prepareLocalRepository();

        // now, following will fail because remote repository is no longer available and tracking remotes will reject locally
        // cached dependencies
        Maven.configureResolver().fromFile(CENTRAL_ONLY_SETTINGS)
                .resolve("org.jboss.shrinkwrap.test:test-deps-c:1.0.0")
                .withTransitivity().as(File.class);

        Assert.fail("Maven 3 is tracking artifact remote repositories origin by default, which should cause test to fail.");
    }

    private void prepareLocalRepository() {

        // ensure we get an artifact from remote repository
        File[] filesRemote = Maven.configureResolver().fromFile(REMOTE_ENABLED_SETTINGS)
                .resolve("org.jboss.shrinkwrap.test:test-deps-c:1.0.0")
                .withTransitivity().as(File.class);

        ValidationUtil.fromDependencyTree(new File("src/test/resources/dependency-trees/test-deps-c.tree")).validate(
                filesRemote);
    }
}
