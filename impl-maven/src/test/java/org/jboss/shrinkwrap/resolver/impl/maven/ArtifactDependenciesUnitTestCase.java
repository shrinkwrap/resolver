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
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.ResolutionException;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;
import org.jboss.shrinkwrap.resolver.util.FileUtil;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 *
 */
public class ArtifactDependenciesUnitTestCase {

    /**
     * Cleanup, remove the repositories from previous tests
     */
    @Before
    public void cleanup() throws Exception {
        FileUtil.removeDirectory(new File("target/profile-repository"));
    }

    /**
     * Tests a resolution of an artifact from central with custom settings
     *
     * @throws ResolutionException
     */
    @Test
    public void testPomBasedArtifact() throws ResolutionException {
        String name = "pomBasedArtifact";

        WebArchive war = ShrinkWrap.create(WebArchive.class, name + ".war").addAsLibraries(
            DependencyResolvers.use(MavenDependencyResolver.class)
                .loadSettings("target/settings/profiles/settings.xml")
                .artifact("org.jboss.shrinkwrap.test:test-parent:pom:1.0.0").resolveAs(GenericArchive.class));

        // only default and compile scoped artifacts are resolved
        DependencyTreeDescription desc = new DependencyTreeDescription(new File(
            "src/test/resources/dependency-trees/test-parent.tree"), "compile");
        desc.validateArchive(war).results();

        war.as(ZipExporter.class).exportTo(new File("target/" + name + ".war"), true);
    }

    @Test
    public void testPomBasedArtifactWithFileQualifier() throws ResolutionException {
        String name = "testPomBasedArtifactWithFileQualifier";

        WebArchive war = ShrinkWrap.create(WebArchive.class, name + ".war").addAsLibraries(
            DependencyResolvers.use(MavenDependencyResolver.class)
                .loadSettings("file:target/settings/profiles/settings.xml")
                .artifact("org.jboss.shrinkwrap.test:test-parent:pom:1.0.0").resolveAs(GenericArchive.class));

        // only default and compile scoped artifacts are resolved
        DependencyTreeDescription desc = new DependencyTreeDescription(new File(
            "src/test/resources/dependency-trees/test-parent.tree"), "compile");
        desc.validateArchive(war).results();

        war.as(ZipExporter.class).exportTo(new File("target/" + name + ".war"), true);
    }

    @Test
    public void testPomBasedArtifactLocatedInClassPath() throws ResolutionException {
        String name = "pomBasedArtifactLocatedInClassPath";

        WebArchive war = ShrinkWrap.create(WebArchive.class, name + ".war").addAsLibraries(
            DependencyResolvers.use(MavenDependencyResolver.class).loadSettings("classpath:profiles/settings3.xml")
                .loadEffectivePom("classpath:poms/test-parent.xml").importAllDependencies()
                .resolveAs(GenericArchive.class));

        // only default and compile scoped artifacts are resolved
        DependencyTreeDescription desc = new DependencyTreeDescription(new File(
            "src/test/resources/dependency-trees/test-parent.tree"), "compile");
        desc.validateArchive(war).results();

        war.as(ZipExporter.class).exportTo(new File("target/" + name + ".war"), true);
    }

    // this test won't run on IDE since it uses a surefire configuration
    @Test
    public void testPomBasedArtifactLocatedInsideJar() throws ResolutionException {
        String name = "pomBasedArtifactLocatedInsideJar";

        WebArchive war = ShrinkWrap.create(WebArchive.class, name + ".war").addAsLibraries(
            DependencyResolvers.use(MavenDependencyResolver.class)
                .loadSettings("classpath:org/jboss/shrinkwrap/profiles/settings3.xml")
                .loadEffectivePom("classpath:org/jboss/shrinkwrap/poms/test-parent.xml").importAllDependencies()
                .resolveAs(GenericArchive.class));

        // only default and compile scoped artifacts are resolved
        DependencyTreeDescription desc = new DependencyTreeDescription(new File(
            "src/test/resources/dependency-trees/test-parent.tree"), "compile");
        desc.validateArchive(war).results();

        war.as(ZipExporter.class).exportTo(new File("target/" + name + ".war"), true);
    }

}
