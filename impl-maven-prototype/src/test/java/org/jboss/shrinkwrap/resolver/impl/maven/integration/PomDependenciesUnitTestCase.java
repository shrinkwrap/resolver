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

import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.impl.maven.bootstrap.MavenSettingsBuilder;
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
     */
    @Test
    public void testParentPomRepositories() {
        String name = "parentPomRepositories";

        WebArchive war = ShrinkWrap.create(WebArchive.class, name + ".war").addAsLibraries(
                DependencyResolvers.use(MavenDependencyResolver.class).loadEffectivePom("target/poms/test-child.xml")
                        .artifact("org.jboss.shrinkwrap.test:test-child:1.0.0").resolveAs(GenericArchive.class));

        DependencyTreeDescription desc = new DependencyTreeDescription(new File(
                "src/test/resources/dependency-trees/test-child.tree"), "compile");
        desc.validateArchive(war).results();

        war.as(ZipExporter.class).exportTo(new File("target/" + name + ".war"), true);
    }

    /**
     * Tests loading of a POM file with parent not available on local file system
     *
     */
    @Test
    public void testShortcutParentPomRepositories() {
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
     */
    @Test
    public void testShortcutParentPomRepositoriesAsFile() {
        File file = Maven.withPom("target/poms/test-child.xml").resolveAsFile("org.jboss.shrinkwrap.test:test-child:1.0.0");

        DependencyTreeDescription desc = new DependencyTreeDescription(new File(
                "src/test/resources/dependency-trees/test-child-shortcut.tree"), "compile");
        desc.validateFiles(file).results();
    }

    /**
     * Tests loading of a POM file with parent available on local file system
     *
     */
    @Test
    public void testParentPomRemoteRepositories() {
        String name = "parentPomRemoteRepositories";

        WebArchive war = ShrinkWrap.create(WebArchive.class, name + ".war").addAsLibraries(
                DependencyResolvers.use(MavenDependencyResolver.class).loadEffectivePom("target/poms/test-remote-child.xml")
                        .artifact("org.jboss.shrinkwrap.test:test-deps-c:1.0.0").resolveAs(GenericArchive.class));

        DependencyTreeDescription desc = new DependencyTreeDescription(new File(
                "src/test/resources/dependency-trees/test-deps-c.tree"));
        desc.validateArchive(war).results();

        war.as(ZipExporter.class).exportTo(new File("target/" + name + ".war"), true);
    }

    /**
     * Tests loading of a POM file with parent available on local file system
     *
     */
    @Test
    public void testShortcutParentPomRemoteRepositories() {
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
     */
    @Test
    public void testShortcutParentPomRemoteRepositoriesAsFile() {
        File file = Maven.withPom("target/poms/test-remote-child.xml").resolveAsFile(
                "org.jboss.shrinkwrap.test:test-deps-c:1.0.0");

        DependencyTreeDescription desc = new DependencyTreeDescription(new File(
                "src/test/resources/dependency-trees/test-deps-c-shortcut.tree"));
        desc.validateFiles(file).results();
    }

    /**
     * Tests loading of a POM file with parent available on local file system Uses POM to get artifact version
     *
     */
    @Test
    public void testArtifactVersionRetrievalFromPom() {
        String name = "artifactVersionRetrievalFromPom";

        WebArchive war = ShrinkWrap.create(WebArchive.class, name + ".war").addAsLibraries(
                DependencyResolvers.use(MavenDependencyResolver.class).loadEffectivePom("target/poms/test-remote-child.xml")
                        .artifact("org.jboss.shrinkwrap.test:test-deps-c").resolveAs(GenericArchive.class));

        DependencyTreeDescription desc = new DependencyTreeDescription(new File(
                "src/test/resources/dependency-trees/test-deps-c.tree"));
        desc.validateArchive(war).results();

        war.as(ZipExporter.class).exportTo(new File("target/" + name + ".war"), true);
    }

    /**
     * Tests loading of a POM file with parent available on local file system Uses POM to get artifact version
     *
     */
    @Test
    public void testShortcutArtifactVersionRetrievalFromPom() {
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
     */
    @Test
    public void testShortcutArtifactVersionRetrievalFromPomAsFile() {
        File file = Maven.withPom("target/poms/test-remote-child.xml").resolveAsFile("org.jboss.shrinkwrap.test:test-deps-c");

        DependencyTreeDescription desc = new DependencyTreeDescription(new File(
                "src/test/resources/dependency-trees/test-deps-c-shortcut.tree"));
        desc.validateFiles(file).results();
    }

    /**
     * Tests loading of a POM file with parent available on local file system. However, the artifact version is not used from
     * there, but specified manually
     *
     */
    @Test
    public void testArtifactVersionRetrievalFromPomOverride() {
        String name = "artifactVersionRetrievalFromPomOverride";

        WebArchive war = ShrinkWrap.create(WebArchive.class, name + ".war").addAsLibraries(
                DependencyResolvers.use(MavenDependencyResolver.class).loadEffectivePom("target/poms/test-remote-child.xml")
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
     */
    @Test
    public void testShortcutArtifactVersionRetrievalFromPomOverride() {
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
     */
    @Test
    public void testShortcutArtifactVersionRetrievalFromPomOverrideAsFile() {
        File file = Maven.withPom("target/poms/test-remote-child.xml").resolveAsFile(
                "org.jboss.shrinkwrap.test:test-deps-c:2.0.0");

        DependencyTreeDescription desc = new DependencyTreeDescription(new File(
                "src/test/resources/dependency-trees/test-deps-c-2-shortcut.tree"));
        desc.validateFiles(file).results();
    }

    /**
     * Tests resolution of dependencies for a POM file with parent on local file system
     *
     */
    @Test
    public void testPomBasedDependencies() {
        String name = "pomBasedDependencies";

        WebArchive war = ShrinkWrap.create(WebArchive.class, name + ".war").addAsLibraries(
                DependencyResolvers.use(MavenDependencyResolver.class).loadEffectivePom("target/poms/test-child.xml")
                        .importAllDependencies().resolveAs(JavaArchive.class));

        DependencyTreeDescription desc = new DependencyTreeDescription(new File(
                "src/test/resources/dependency-trees/test-child.tree"));
        desc.validateArchive(war).results();

        war.as(ZipExporter.class).exportTo(new File("target/" + name + ".war"), true);
    }

    /**
     * Tests resolution of dependencies for a POM file without parent on local file system
     *
     */
    @Test
    public void testPomRemoteBasedDependencies() {
        String name = "pomRemoteBasedDependencies";

        WebArchive war = ShrinkWrap.create(WebArchive.class, name + ".war").addAsLibraries(
                DependencyResolvers.use(MavenDependencyResolver.class).loadEffectivePom("target/poms/test-remote-child.xml")
                        .importAllDependencies().resolveAs(JavaArchive.class));

        DependencyTreeDescription desc = new DependencyTreeDescription(new File(
                "src/test/resources/dependency-trees/test-remote-child.tree"));
        desc.validateArchive(war).results();

        war.as(ZipExporter.class).exportTo(new File("target/" + name + ".war"), true);
    }
}
