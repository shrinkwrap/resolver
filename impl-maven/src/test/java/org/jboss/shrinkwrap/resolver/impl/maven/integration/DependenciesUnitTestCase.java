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
import java.util.ArrayList;
import java.util.Collection;

import org.jboss.shrinkwrap.resolver.api.InvalidConfigurationFileException;
import org.jboss.shrinkwrap.resolver.api.Resolvers;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolverSystem;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependencies;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependency;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependencyExclusion;
import org.jboss.shrinkwrap.resolver.impl.maven.bootstrap.MavenSettingsBuilder;
import org.jboss.shrinkwrap.resolver.impl.maven.util.ValidationUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests to ensure Dependencies resolves dependencies correctly.
 *
 * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
 * @author <a href="http://community.jboss.org/people/silenius">Samuel Santos</a>
 */
public class DependenciesUnitTestCase {

    @BeforeClass
    public static void setRemoteRepository() {
        System
            .setProperty(MavenSettingsBuilder.ALT_USER_SETTINGS_XML_LOCATION, "target/settings/profiles/settings.xml");
        System.setProperty(MavenSettingsBuilder.ALT_LOCAL_REPOSITORY_LOCATION, "target/the-other-repository");
    }

    @AfterClass
    public static void clearRemoteRepository() {
        System.clearProperty(MavenSettingsBuilder.ALT_USER_SETTINGS_XML_LOCATION);
        System.clearProperty(MavenSettingsBuilder.ALT_LOCAL_REPOSITORY_LOCATION);
    }

    // -------------------------------------------------------------------------------------||
    // Tests -------------------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Tests that artifact is cannot be packaged, but is is resolved right. This test is not executed but shows that
     * some jars cannot be packaged
     */
    // @Test(expected =
    // org.jboss.shrinkwrap.api.importer.ArchiveImportException.class)
    // @Ignore
    // FIXME? seems to work now
    public void simpleResolutionWrongArtifact() {
        Resolvers.use(MavenResolverSystem.class).resolve("org.apache.maven.plugins:maven-compiler-plugin:2.3.2")
            .withTransitivity().as(File.class);
    }

    /**
     * Tests a resolution of an artifact from central
     */
    @Test
    public void simpleResolution() {
        File[] files = Resolvers.use(MavenResolverSystem.class).resolve("org.jboss.shrinkwrap.test:test-deps-c:1.0.0")
            .withTransitivity().as(File.class);

        ValidationUtil.fromDependencyTree(new File("src/test/resources/dependency-trees/test-deps-c.tree"))
            .validate(true, files);
    }

    /**
     * Tests a resolution of an artifact from central
     */
    @Test
    public void simpleResolutionWithoutTransitivity() {
        File[] files = Maven.resolver().resolve("org.jboss.shrinkwrap.test:test-deps-c:1.0.0").withoutTransitivity()
            .as(File.class);

        ValidationUtil.fromDependencyTree(new File("src/test/resources/dependency-trees/test-deps-c-shortcut.tree"))
            .validate(true, files);
    }

    /**
     * Tests a resolution of an artifact from central with custom settings
     */
    @Test
    public void simpleResolutionWithCustomSettings() {

        File[] files = Maven.configureResolver().fromFile("target/settings/profiles/settings.xml")
            .resolve("org.jboss.shrinkwrap.test:test-deps-c:1.0.0").withTransitivity().as(File.class);

        ValidationUtil.fromDependencyTree(new File("src/test/resources/dependency-trees/test-deps-c.tree"))
        	.validate(true, files);
    }

    /**
     * Tests passing invalid format settings XML; SHRINKRES-72
     */
    @Test(expected = InvalidConfigurationFileException.class)
    public void invalidSettingsFormat() {

        // Cannot pass POM file where settings.xml is expected
        Maven.configureResolver().fromFile("target/poms/install-all.xml")
            .resolve("org.jboss.shrinkwrap.test:test-deps-c:1.0.0").withTransitivity().as(File.class);
    }

    /**
     * Tests passing invalid path to a settings XML
     */
    @Test(expected = IllegalArgumentException.class)
    public void invalidSettingsPath() {

        // this should fail
        Maven.configureResolver().fromFile("src/test/invalid/custom-settings.xml")
            .resolve("org.jboss.shrinkwrap.test:test-deps-c:1.0.0").withTransitivity().as(File.class);
    }

    /**
     * Tests a resolution of two artifacts from central
     */
    @Test
    public void multipleResolution() {

        File[] files = Maven.resolver()
            .resolve("org.jboss.shrinkwrap.test:test-deps-c:1.0.0", "org.jboss.shrinkwrap.test:test-deps-g:1.0.0")
            .withTransitivity().as(File.class);

        ValidationUtil.fromDependencyTree(new File("src/test/resources/dependency-trees/test-deps-c+g.tree"))
            .validate(files);
    }

    /**
     * Tests a resolution of two artifacts from central using a {@link Collection} of {@link String} canonical forms
     */
    @Test
    public void multipleResolutionCollectionCanonicalForm() {

        final Collection<String> canonicalForms = new ArrayList<String>();
        canonicalForms.add("org.jboss.shrinkwrap.test:test-deps-c:1.0.0");
        canonicalForms.add("org.jboss.shrinkwrap.test:test-deps-g:1.0.0");
        File[] files = Maven.resolver().resolve(canonicalForms).withTransitivity().as(File.class);

        ValidationUtil.fromDependencyTree(new File("src/test/resources/dependency-trees/test-deps-c+g.tree"))
        	.validate(files);
    }

    /**
     * Tests a resolution of two artifacts from central using a {@link Collection} of {@link String} canonical forms
     */
    @Test
    public void multipleResolutionCollectionDependencies() {

        final Collection<MavenDependency> dependencies = new ArrayList<MavenDependency>();
        dependencies.add(MavenDependencies.createDependency("org.jboss.shrinkwrap.test:test-deps-c:1.0.0", null, false,
            (MavenDependencyExclusion) null));
        dependencies.add(MavenDependencies.createDependency("org.jboss.shrinkwrap.test:test-deps-g:1.0.0", null, false,
            (MavenDependencyExclusion) null));
        File[] files = Maven.resolver().addDependencies(dependencies).resolve().withTransitivity().as(File.class);
        ValidationUtil.fromDependencyTree(new File("src/test/resources/dependency-trees/test-deps-c+g.tree")).validate(
            true, files);
    }

    /**
     * Tests a resolution of two artifacts from central using single call
     */
    @Test
    public void multipleResolutionSingleCall() {
        File[] files = Maven.resolver()
            .resolve("org.jboss.shrinkwrap.test:test-deps-c:1.0.0", "org.jboss.shrinkwrap.test:test-deps-g:1.0.0")
            .withTransitivity().as(File.class);

        ValidationUtil.fromDependencyTree(new File("src/test/resources/dependency-trees/test-deps-c+g.tree"))
            .validate(files);
    }

    /**
     * Tests a resolution of two artifacts from central using single call
     */
    @Test
    public void multipleResolutionSingleCallWithoutTransitivity() {
        File[] files = Maven.resolver()
            .resolve("org.jboss.shrinkwrap.test:test-deps-c:1.0.0", "org.jboss.shrinkwrap.test:test-deps-g:1.0.0")
            .withoutTransitivity().as(File.class);

        ValidationUtil.fromDependencyTree(new File("src/test/resources/dependency-trees/test-deps-c+g-shortcut.tree"))
            .validate(files);
    }

}
