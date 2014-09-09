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
import java.util.List;

import org.jboss.shrinkwrap.resolver.api.ResolutionException;
import org.jboss.shrinkwrap.resolver.api.Resolvers;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolverSystem;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependency;
import org.jboss.shrinkwrap.resolver.api.maven.pom.ParsedPomFile;
import org.jboss.shrinkwrap.resolver.impl.maven.MavenWorkingSessionContainer;
import org.jboss.shrinkwrap.resolver.impl.maven.bootstrap.MavenSettingsBuilder;
import org.jboss.shrinkwrap.resolver.impl.maven.util.ValidationUtil;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests misc functionality
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 *
 */
public class MiscUnitTestCase {
    @BeforeClass
    public static void setRemoteRepository() {
        System.setProperty(MavenSettingsBuilder.ALT_USER_SETTINGS_XML_LOCATION, "target/settings/profiles/settings.xml");
        System.setProperty(MavenSettingsBuilder.ALT_LOCAL_REPOSITORY_LOCATION, "target/the-other-repository");
    }

    @AfterClass
    public static void clearRemoteRepository() {
        System.clearProperty(MavenSettingsBuilder.ALT_USER_SETTINGS_XML_LOCATION);
        System.clearProperty(MavenSettingsBuilder.ALT_LOCAL_REPOSITORY_LOCATION);
    }

    /**
     * Tests resolution of dependencies for a POM file with parent on local file system
     *
     * @throws ResolutionException
     */
    @Test
    public void testFilesResolution() {
        File[] files = Resolvers.use(MavenResolverSystem.class)
                .resolve("org.jboss.shrinkwrap.test:test-deps-a:1.0.0", "org.jboss.shrinkwrap.test:test-deps-c:1.0.0")
                .withTransitivity().as(File.class);

        new ValidationUtil("test-deps-a-1.0.0.jar", "test-deps-c-1.0.0.jar", "test-deps-b-1.0.0.jar").validate(files);
    }

    @Test
    public void testMavenSystemResolverAsSessionContainer() {
        MavenResolverSystem resolver = Maven.resolver();
        resolver.resolve("org.jboss.shrinkwrap.test:test-deps-a:1.0.0");

        List<MavenDependency> dependencies = ((MavenWorkingSessionContainer) resolver).getMavenWorkingSession()
                .getDependenciesForResolution();
        Assert.assertEquals("There is one dependency to be resolved", 1, dependencies.size());
        Assert.assertEquals("Dependency artifactId to be resolved matches", "test-deps-a", dependencies.iterator().next()
                .getArtifactId());
    }

    @Test
    public void testParsedPomFile() {
        PomEquippedResolveStage resolver = Maven.resolver().loadPomFromFile("target/poms/test-resources.xml");

        ParsedPomFile pom = ((MavenWorkingSessionContainer) resolver).getMavenWorkingSession().getParsedPomFile();

        Assert.assertTrue("Project resources are not null nor empty", pom.getResources() != null
                && !pom.getResources().isEmpty());

        Assert.assertTrue("Project test resources are not null nor empty", pom.getTestResources() != null
                && !pom.getTestResources().isEmpty());
    }
}
