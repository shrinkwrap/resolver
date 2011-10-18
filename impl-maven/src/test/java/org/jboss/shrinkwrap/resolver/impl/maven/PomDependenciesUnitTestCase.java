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
package org.jboss.shrinkwrap.resolver.impl.maven;

import java.io.File;

import org.jboss.shrinkwrap.api.GenericArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.ResolutionException;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 * @author <a href="http://community.jboss.org/people/silenius">Samuel Santos</a>
 */
public class PomDependenciesUnitTestCase {
    @BeforeClass
    public static void setRemoteRepository() {
        System.setProperty(MavenSettingsBuilder.ALT_LOCAL_REPOSITORY_LOCATION, "target/the-other-repository");
    }

    @AfterClass
    public static void clearRemoteRepository() {
        System.clearProperty(MavenSettingsBuilder.ALT_LOCAL_REPOSITORY_LOCATION);
    }

    /**
     * Tests loading of a POM file with parent not available on local file system
     *
     * @throws ResolutionException
     */
    @Test
    public void testParentPomRepositories() throws ResolutionException {
        String name = "parentPomRepositories";

        WebArchive war = ShrinkWrap.create(WebArchive.class, name + ".war").addAsLibraries(
<<<<<<< HEAD
                DependencyResolvers.use(MavenDependencyResolver.class).loadMetadataFromPom("target/poms/test-child.xml")
                        .artifact("org.jboss.shrinkwrap.test:test-child:1.0.0").resolveAs(GenericArchive.class));

        DependencyTreeDescription desc = new DependencyTreeDescription(new File(
                "src/test/resources/dependency-trees/test-child.tree"), "compile");
        desc.validateArchive(war).results();

        war.as(ZipExporter.class).exportTo(new File("target/" + name + ".war"), true);
    }

    /**
     * Tests loading of a POM file with parent not available on local file system
     *
     * @throws ResolutionException
     */
    @Test
    public void testShortcutParentPomRepositories() throws ResolutionException {
        String name = "shortcutParentPomRepositories";

        WebArchive war = ShrinkWrap.create(WebArchive.class, name + ".war").addAsLibraries(
                Maven.withPom("target/poms/test-child.xml").dependency("org.jboss.shrinkwrap.test:test-child:1.0.0"));

        DependencyTreeDescription desc = new DependencyTreeDescription(new File(
                "src/test/resources/dependency-trees/test-child-shortcut.tree"), "compile");
        desc.validateArchive(war).results();

        war.as(ZipExporter.class).exportTo(new File("target/" + name + ".war"), true);
    }

    /**
     * Tests loading of a POM file with parent not available on local file system
     *
     * @throws ResolutionException
     */
    @Test
    @Deprecated
    public void testParentPomRepositoriesDeprecated() throws ResolutionException {
        String name = "testParentPomRepositoriesDeprecated";

        WebArchive war = ShrinkWrap.create(WebArchive.class, name + ".war").addAsLibraries(
                DependencyResolvers.use(MavenDependencyResolver.class).loadReposFromPom("target/poms/test-child.xml")
=======
                DependencyResolvers.use(MavenDependencyResolver.class).loadEffectiveFromPom("target/poms/test-child.xml").up()
>>>>>>> SHRINKWRAP-344 Updated API to descent to lower-level objects
                        .artifact("org.jboss.shrinkwrap.test:test-child:1.0.0").resolveAs(GenericArchive.class));

        DependencyTreeDescription desc = new DependencyTreeDescription(new File(
                "src/test/resources/dependency-trees/test-child.tree"), "compile");
        desc.validateArchive(war).results();

        war.as(ZipExporter.class).exportTo(new File("target/" + name + ".war"), true);

    }

    /**
     * Tests loading of a POM file with parent available on local file system
     *
     * @throws ResolutionException
     */
    @Test
    public void testParentPomRemoteRepositories() throws ResolutionException {
        String name = "parentPomRemoteRepositories";

        WebArchive war = ShrinkWrap.create(WebArchive.class, name + ".war").addAsLibraries(
<<<<<<< HEAD
                DependencyResolvers.use(MavenDependencyResolver.class).loadMetadataFromPom("target/poms/test-remote-child.xml")
                        .artifact("org.jboss.shrinkwrap.test:test-deps-c:1.0.0").resolveAs(GenericArchive.class));

        DependencyTreeDescription desc = new DependencyTreeDescription(new File(
                "src/test/resources/dependency-trees/test-deps-c.tree"));
        desc.validateArchive(war).results();

        war.as(ZipExporter.class).exportTo(new File("target/" + name + ".war"), true);
    }

    /**
     * Tests loading of a POM file with parent available on local file system
     *
     * @throws ResolutionException
     */
    @Test
    public void testShortcutParentPomRemoteRepositories() throws ResolutionException {
        String name = "shortcutParentPomRemoteRepositories";

        WebArchive war = ShrinkWrap.create(WebArchive.class, name + ".war").addAsLibraries(
                Maven.withPom("target/poms/test-remote-child.xml").dependency("org.jboss.shrinkwrap.test:test-deps-c:1.0.0"));

        DependencyTreeDescription desc = new DependencyTreeDescription(new File(
                "src/test/resources/dependency-trees/test-deps-c-shortcut.tree"));
        desc.validateArchive(war).results();

        war.as(ZipExporter.class).exportTo(new File("target/" + name + ".war"), true);
    }

    /**
     * Tests loading of a POM file with parent available on local file system
     *
     * @throws ResolutionException
     */
    @Test
    @Deprecated
    public void testParentPomRemoteRepositoriesDeprecated() throws ResolutionException {
        String name = "testParentPomRemoteRepositoriesDeprecated";

        WebArchive war = ShrinkWrap.create(WebArchive.class, name + ".war").addAsLibraries(
                DependencyResolvers.use(MavenDependencyResolver.class).loadReposFromPom("target/poms/test-remote-child.xml")
=======
                DependencyResolvers.use(MavenDependencyResolver.class)
                        .loadEffectiveFromPom("target/poms/test-remote-child.xml").up()
>>>>>>> SHRINKWRAP-344 Updated API to descent to lower-level objects
                        .artifact("org.jboss.shrinkwrap.test:test-deps-c:1.0.0").resolveAs(GenericArchive.class));

        DependencyTreeDescription desc = new DependencyTreeDescription(new File(
                "src/test/resources/dependency-trees/test-deps-c.tree"));
        desc.validateArchive(war).results();

        war.as(ZipExporter.class).exportTo(new File("target/" + name + ".war"), true);
    }

    /**
     * Tests loading of a POM file with parent available on local file system Uses POM to get artifact version
     *
     * @throws ResolutionException
     */
    @Test
    public void testArtifactVersionRetrievalFromPom() throws ResolutionException {
        String name = "artifactVersionRetrievalFromPom";

        WebArchive war = ShrinkWrap.create(WebArchive.class, name + ".war").addAsLibraries(
<<<<<<< HEAD
                DependencyResolvers.use(MavenDependencyResolver.class).loadMetadataFromPom("target/poms/test-remote-child.xml")
                        .artifact("org.jboss.shrinkwrap.test:test-deps-c").resolveAs(GenericArchive.class));

        DependencyTreeDescription desc = new DependencyTreeDescription(new File(
                "src/test/resources/dependency-trees/test-deps-c.tree"));
        desc.validateArchive(war).results();

        war.as(ZipExporter.class).exportTo(new File("target/" + name + ".war"), true);
    }

    /**
     * Tests loading of a POM file with parent available on local file system Uses POM to get artifact version
     *
     * @throws ResolutionException
     */
    @Test
    public void testShortcutArtifactVersionRetrievalFromPom() throws ResolutionException {
        String name = "shortcutArtifactVersionRetrievalFromPom";

        WebArchive war = ShrinkWrap.create(WebArchive.class, name + ".war").addAsLibraries(
                Maven.withPom("target/poms/test-remote-child.xml").dependency("org.jboss.shrinkwrap.test:test-deps-c"));

        DependencyTreeDescription desc = new DependencyTreeDescription(new File(
                "src/test/resources/dependency-trees/test-deps-c-shortcut.tree"));
        desc.validateArchive(war).results();

        war.as(ZipExporter.class).exportTo(new File("target/" + name + ".war"), true);
    }

    /**
     * Tests loading of a POM file with parent available on local file system Uses POM to get artifact version
     *
     * @throws ResolutionException
     */
    @Test
    @Deprecated
    public void testArtifactVersionRetrievalFromPomDeprecated() throws ResolutionException {
        String name = "testArtifactVersionRetrievalFromPomDeprecated";

        WebArchive war = ShrinkWrap.create(WebArchive.class, name + ".war").addAsLibraries(
                DependencyResolvers.use(MavenDependencyResolver.class).loadReposFromPom("target/poms/test-remote-child.xml")
=======
                DependencyResolvers.use(MavenDependencyResolver.class)
                        .loadEffectiveFromPom("target/poms/test-remote-child.xml").up()
>>>>>>> SHRINKWRAP-344 Updated API to descent to lower-level objects
                        .artifact("org.jboss.shrinkwrap.test:test-deps-c").resolveAs(GenericArchive.class));

        DependencyTreeDescription desc = new DependencyTreeDescription(new File(
                "src/test/resources/dependency-trees/test-deps-c.tree"));
        desc.validateArchive(war).results();

        war.as(ZipExporter.class).exportTo(new File("target/" + name + ".war"), true);
    }

    /**
     * Tests loading of a POM file with parent available on local file system. However, the artifact version is not used from
     * there, but specified manually
     *
     * @throws ResolutionException
     */
    @Test
    public void testArtifactVersionRetrievalFromPomOverride() throws ResolutionException {
        String name = "artifactVersionRetrievalFromPomOverride";

        WebArchive war = ShrinkWrap.create(WebArchive.class, name + ".war").addAsLibraries(
<<<<<<< HEAD
                DependencyResolvers.use(MavenDependencyResolver.class).loadMetadataFromPom("target/poms/test-remote-child.xml")
                        .artifact("org.jboss.shrinkwrap.test:test-deps-c:2.0.0").resolveAs(GenericArchive.class));

        DependencyTreeDescription desc = new DependencyTreeDescription(new File(
                "src/test/resources/dependency-trees/test-deps-c-2.tree"));
        desc.validateArchive(war).results();

        war.as(ZipExporter.class).exportTo(new File("target/" + name + ".war"), true);
    }

    /**
     * Tests loading of a POM file with parent available on local file system. However, the artifact version is not used from
     * there, but specified manually
     *
     * @throws ResolutionException
     */
    @Test
    public void testShortcutArtifactVersionRetrievalFromPomOverride() throws ResolutionException {
        String name = "shortcutArtifactVersionRetrievalFromPomOverride";

        WebArchive war = ShrinkWrap.create(WebArchive.class, name + ".war").addAsLibraries(
                Maven.withPom("target/poms/test-remote-child.xml").dependency("org.jboss.shrinkwrap.test:test-deps-c:2.0.0"));

        DependencyTreeDescription desc = new DependencyTreeDescription(new File(
                "src/test/resources/dependency-trees/test-deps-c-2-shortcut.tree"));
        desc.validateArchive(war).results();

        war.as(ZipExporter.class).exportTo(new File("target/" + name + ".war"), true);
    }

    /**
     * Tests loading of a POM file with parent available on local file system. However, the artifact version is not used from
     * there, but specified manually
     *
     * @throws ResolutionException
     */
    @Test
    @Deprecated
    public void testArtifactVersionRetrievalFromPomOverrideDeprecated() throws ResolutionException {
        String name = "testArtifactVersionRetrievalFromPomOverrideDeprecated";

        WebArchive war = ShrinkWrap.create(WebArchive.class, name + ".war").addAsLibraries(
                DependencyResolvers.use(MavenDependencyResolver.class).loadReposFromPom("target/poms/test-remote-child.xml")
=======
                DependencyResolvers.use(MavenDependencyResolver.class)
                        .loadEffectiveFromPom("target/poms/test-remote-child.xml").up()
>>>>>>> SHRINKWRAP-344 Updated API to descent to lower-level objects
                        .artifact("org.jboss.shrinkwrap.test:test-deps-c:2.0.0").resolveAs(GenericArchive.class));

        DependencyTreeDescription desc = new DependencyTreeDescription(new File(
                "src/test/resources/dependency-trees/test-deps-c-2.tree"));
        desc.validateArchive(war).results();

        war.as(ZipExporter.class).exportTo(new File("target/" + name + ".war"), true);
    }

    /**
     * Tests resolution of dependencies for a POM file with parent on local file system
     *
     * @throws ResolutionException
     */
    @Test
    public void testPomBasedDependencies() throws ResolutionException {
        String name = "pomBasedDependencies";

        WebArchive war = ShrinkWrap.create(WebArchive.class, name + ".war").addAsLibraries(
<<<<<<< HEAD
                DependencyResolvers.use(MavenDependencyResolver.class).includeDependenciesFromPom("target/poms/test-child.xml")
                        .resolveAs(JavaArchive.class));

        DependencyTreeDescription desc = new DependencyTreeDescription(new File(
                "src/test/resources/dependency-trees/test-child.tree"));
        desc.validateArchive(war).results();

        war.as(ZipExporter.class).exportTo(new File("target/" + name + ".war"), true);
    }

    /**
     * Tests resolution of dependencies for a POM file with parent on local file system
     *
     * @throws ResolutionException
     */
    @Test
    @Deprecated
    public void testPomBasedDependenciesDeprecated() throws ResolutionException {
        String name = "testPomBasedDependenciesDeprecated";

        WebArchive war = ShrinkWrap.create(WebArchive.class, name + ".war").addAsLibraries(
                DependencyResolvers.use(MavenDependencyResolver.class).loadDependenciesFromPom("target/poms/test-child.xml")
                        .resolveAs(JavaArchive.class));
=======
                DependencyResolvers.use(MavenDependencyResolver.class).loadEffectiveFromPom("target/poms/test-child.xml")
                        .importAllDependencies().resolveAs(JavaArchive.class));
>>>>>>> SHRINKWRAP-344 Updated API to descent to lower-level objects

        DependencyTreeDescription desc = new DependencyTreeDescription(new File(
                "src/test/resources/dependency-trees/test-child.tree"));
        desc.validateArchive(war).results();

        war.as(ZipExporter.class).exportTo(new File("target/" + name + ".war"), true);
    }

    /**
     * Tests resolution of dependencies for a POM file without parent on local file system
     *
     * @throws ResolutionException
     */
    @Test
    public void testPomRemoteBasedDependencies() throws ResolutionException {
        String name = "pomRemoteBasedDependencies";

        WebArchive war = ShrinkWrap.create(WebArchive.class, name + ".war").addAsLibraries(
                DependencyResolvers.use(MavenDependencyResolver.class)
                        .loadEffectiveFromPom("target/poms/test-remote-child.xml").importAllDependencies()
                        .resolveAs(JavaArchive.class));

        DependencyTreeDescription desc = new DependencyTreeDescription(new File(
                "src/test/resources/dependency-trees/test-remote-child.tree"));
        desc.validateArchive(war).results();

        war.as(ZipExporter.class).exportTo(new File("target/" + name + ".war"), true);
    }

<<<<<<< HEAD
    /**
     * Tests resolution of dependencies for a POM file without parent on local file system
     *
     * @throws ResolutionException
     */
    @Test
    @Deprecated
    public void testPomRemoteBasedDependenciesDeprecated() throws ResolutionException {
        String name = "testPomRemoteBasedDependenciesDeprecated";

        WebArchive war = ShrinkWrap.create(WebArchive.class, name + ".war").addAsLibraries(
                DependencyResolvers.use(MavenDependencyResolver.class)
                        .loadDependenciesFromPom("target/poms/test-remote-child.xml").resolveAs(JavaArchive.class));

        DependencyTreeDescription desc = new DependencyTreeDescription(new File(
                "src/test/resources/dependency-trees/test-remote-child.tree"));
        desc.validateArchive(war).results();

        war.as(ZipExporter.class).exportTo(new File("target/" + name + ".war"), true);
    }
=======
>>>>>>> SHRINKWRAP-344 Updated API to descent to lower-level objects
}
