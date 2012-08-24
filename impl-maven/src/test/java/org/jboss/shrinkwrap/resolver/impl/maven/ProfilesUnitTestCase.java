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

import junit.framework.Assert;

import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.ResolutionException;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;
import org.jboss.shrinkwrap.resolver.api.maven.filter.StrictFilter;
import org.junit.Test;

/**
 * Exercise parsing of Maven profiles
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 *
 */
public class ProfilesUnitTestCase {
    /**
     * Tests a resolution of an artifact from local repository specified in settings.xml as active profile
     *
     * @throws ResolutionException
     */
    @Test
    public void testActiveByDefault() throws ResolutionException {
        File[] files = DependencyResolvers.use(MavenDependencyResolver.class)
            .loadSettings("target/settings/profiles/settings.xml")
            .artifact("org.jboss.shrinkwrap.test:test-deps-c:1.0.0").resolveAsFiles(new StrictFilter());

        Assert.assertEquals("There is only one jar in the package", 1, files.length);
        Assert.assertEquals("The file is packaged as test-deps-c-1.0.0.jar", "test-deps-c-1.0.0.jar",
            files[0].getName());
    }

    /**
     * Tests a resolution of an artifact from JBoss repository specified in settings.xml within activeProfiles
     *
     * @throws ResolutionException
     */
    @Test
    public void testActiveProfiles() throws ResolutionException {
        File[] files = DependencyResolvers.use(MavenDependencyResolver.class)
            .loadSettings("target/settings/profiles/settings2.xml")
            .artifact("org.jboss.shrinkwrap.test:test-deps-c:1.0.0").resolveAsFiles(new StrictFilter());

        Assert.assertEquals("There is only one jar in the package", 1, files.length);
        Assert.assertEquals("The file is packaged as test-deps-c-1.0.0.jar", "test-deps-c-1.0.0.jar",
            files[0].getName());
    }

    @Test
    public void testActiveByMissingFile() throws ResolutionException {
        File[] files = DependencyResolvers.use(MavenDependencyResolver.class)
            .loadSettings("target/settings/profiles/settings-file.xml")
            .artifact("org.jboss.shrinkwrap.test:test-deps-c:1.0.0").resolveAsFiles(new StrictFilter());

        Assert.assertEquals("There is only one jar in the package", 1, files.length);
        Assert.assertEquals("The file is packaged as test-deps-c-1.0.0.jar", "test-deps-c-1.0.0.jar",
            files[0].getName());
    }

    @Test
    public void testActiveByProperty() throws ResolutionException {

        System.setProperty("foobar", "foobar-value");

        File[] files = DependencyResolvers.use(MavenDependencyResolver.class)
            .loadSettings("target/settings/profiles/settings-property.xml")
            .artifact("org.jboss.shrinkwrap.test:test-deps-c:1.0.0").resolveAsFiles(new StrictFilter());

        Assert.assertEquals("There is only one jar in the package", 1, files.length);
        Assert.assertEquals("The file is packaged as test-deps-c-1.0.0.jar", "test-deps-c-1.0.0.jar",
            files[0].getName());
    }

    @Test(expected = ResolutionException.class)
    public void testNonActiveByProperty() throws ResolutionException {

        System.setProperty("foobar", "foobar-bad-value");

        File[] files = DependencyResolvers.use(MavenDependencyResolver.class)
            .loadSettings("target/settings/profiles/settings-property.xml")
            .artifact("org.jboss.shrinkwrap.test:test-deps-c:1.0.0").resolveAsFiles(new StrictFilter());

        Assert.assertEquals("There is only one jar in the package", 1, files.length);
        Assert.assertEquals("The file is packaged as test-deps-c-1.0.0.jar", "test-deps-c-1.0.0.jar",
            files[0].getName());
    }

    /**
     * Tests a resolution of an artifact from JBoss repository specified in settings.xml within activeProfiles. The path
     * to do file is defined via system property.
     *
     * @throws ResolutionException
     */
    @Test
    public void testSystemPropertiesSettingsProfiles() throws ResolutionException {
        System.setProperty(MavenSettingsBuilder.ALT_USER_SETTINGS_XML_LOCATION,
            "target/settings/profiles/settings3.xml");
        System.setProperty(MavenSettingsBuilder.ALT_LOCAL_REPOSITORY_LOCATION, "target/prop-profiles");

        File[] files = DependencyResolvers.use(MavenDependencyResolver.class)
            .artifact("org.jboss.shrinkwrap.test:test-deps-c:1.0.0").resolveAsFiles(new StrictFilter());

        Assert.assertEquals("There is only one jar in the package", 1, files.length);
        Assert.assertEquals("The file is packaged as test-deps-c-1.0.0.jar", "test-deps-c-1.0.0.jar",
            files[0].getName());
    }

    @Test
    public void testProfileSelection1() {
        File[] jars = DependencyResolvers.use(MavenDependencyResolver.class).disableMavenCentral()
            .loadEffectivePom("target/poms/test-profiles.xml", "version1").importAllDependencies().resolveAsFiles();

        Assert.assertEquals("Exactly 2 files were resolved", 2, jars.length);
        new ValidationUtil("test-deps-a-1.0.0", "test-managed-dependency-1.0.0").validate(jars);
    }

    @Test
    public void testProfileSelection2() {
        File[] jars = DependencyResolvers.use(MavenDependencyResolver.class).disableMavenCentral()
            .loadEffectivePom("target/poms/test-profiles.xml", "version2").importAllDependencies().resolveAsFiles();

        Assert.assertEquals("Exactly 2 files were resolved", 2, jars.length);
        new ValidationUtil("test-deps-d-1.0.0", "test-managed-dependency-2.0.0").validate(jars);
    }

    @Test
    public void testActiveProfileByFile() {
        File[] jars = DependencyResolvers.use(MavenDependencyResolver.class).disableMavenCentral()
            .loadEffectivePom("target/poms/test-profiles-file-activation.xml").importAllDependencies().resolveAsFiles();

        Assert.assertEquals("Exactly 2 files were resolved", 2, jars.length);
        new ValidationUtil("test-deps-d-1.0.0", "test-deps-a-1.0.0").validate(jars);
    }

    @Test
    public void testDisabledProfile() {
        File[] jars = DependencyResolvers.use(MavenDependencyResolver.class).disableMavenCentral()
            .loadEffectivePom("target/poms/test-profiles-file-activation.xml", "!add-dependency-a")
            .importAllDependencies().resolveAsFiles();

        Assert.assertEquals("Exactly 1 files was resolved", 1, jars.length);
        new ValidationUtil("test-deps-d-1.0.0").validate(jars);
    }

}
