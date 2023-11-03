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

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;
import java.util.Properties;

import org.jboss.shrinkwrap.resolver.api.NoResolvedResultException;
import org.jboss.shrinkwrap.resolver.api.Resolvers;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolverSystem;
import org.jboss.shrinkwrap.resolver.api.maven.MavenWorkingSession;
import org.jboss.shrinkwrap.resolver.impl.maven.MavenWorkingSessionContainer;
import org.jboss.shrinkwrap.resolver.impl.maven.bootstrap.MavenSettingsBuilder;
import org.jboss.shrinkwrap.resolver.impl.maven.util.ValidationUtil;
import org.junit.Assert;
import org.junit.Test;

/**
 * Exercise parsing of Maven profiles
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 */
public class ProfilesUnitTestCase {
    /**
     * Tests a resolution of an artifact from local repository specified in settings.xml as active profile
     */
    @Test
    public void activeByDefault() {

        File file = Maven.configureResolver().fromFile("target/settings/profiles/settings.xml")
                .resolve("org.jboss.shrinkwrap.test:test-deps-c:1.0.0").withoutTransitivity().asSingle(File.class);

        Assert.assertEquals("The file is packaged as test-deps-c-1.0.0.jar", "test-deps-c-1.0.0.jar", file.getName());
    }

    /**
     * Tests a resolution of an artifact from JBoss repository specified in settings.xml within activeProfiles
     */
    @Test
    public void activeProfiles() {

        File file = Maven.configureResolver().fromFile("target/settings/profiles/settings2.xml")
                .resolve("org.jboss.shrinkwrap.test:test-deps-c:1.0.0").withoutTransitivity().asSingle(File.class);

        Assert.assertEquals("The file is packaged as test-deps-c-1.0.0.jar", "test-deps-c-1.0.0.jar", file.getName());
    }

    @Test
    public void activeByMissingFile() {

        File file = Maven.configureResolver().fromFile("target/settings/profiles/settings-file.xml")
                .resolve("org.jboss.shrinkwrap.test:test-deps-c:1.0.0").withoutTransitivity().asSingle(File.class);

        Assert.assertEquals("The file is packaged as test-deps-c-1.0.0.jar", "test-deps-c-1.0.0.jar", file.getName());

    }

    @Test
    public void activeByProperty() {

        System.setProperty("foobar", "foobar-value");

        File file = Maven.configureResolver().fromFile("target/settings/profiles/settings-property.xml")
                .resolve("org.jboss.shrinkwrap.test:test-deps-c:1.0.0").withoutTransitivity().asSingle(File.class);

        Assert.assertEquals("The file is packaged as test-deps-c-1.0.0.jar", "test-deps-c-1.0.0.jar", file.getName());
    }

    @Test(expected = NoResolvedResultException.class)
    public void testNonActiveByProperty() {

        System.setProperty("foobar", "foobar-bad-value");

        File file = Maven.configureResolver().fromFile("target/settings/profiles/settings-property.xml")
                .resolve("org.jboss.shrinkwrap.test:test-deps-c:1.0.0").withoutTransitivity().asSingle(File.class);

        Assert.assertEquals("The file is packaged as test-deps-c-1.0.0.jar", "test-deps-c-1.0.0.jar", file.getName());
    }

    /**
     * Tests a resolution of an artifact from JBoss repository specified in settings.xml within activeProfiles. The path
     * to do file is defined via system property.
     */
    @Test
    public void testSystemPropertiesSettingsProfiles() {
        System.setProperty(MavenSettingsBuilder.ALT_USER_SETTINGS_XML_LOCATION,
                "target/settings/profiles/settings3.xml");
        System.setProperty(MavenSettingsBuilder.ALT_LOCAL_REPOSITORY_LOCATION, "target/prop-profiles");

        File file = Resolvers.use(MavenResolverSystem.class).resolve("org.jboss.shrinkwrap.test:test-deps-c:1.0.0")
                .withoutTransitivity().asSingle(File.class);

        Assert.assertEquals("The file is packaged as test-deps-c-1.0.0.jar", "test-deps-c-1.0.0.jar", file.getName());

    }

    @Test
    public void testProfileSelection1() {

        File[] files = Resolvers.use(MavenResolverSystem.class)
                .loadPomFromFile("target/poms/test-profiles.xml", "version1")
                .importCompileAndRuntimeDependencies()
                .resolve()
                .withTransitivity()
                .as(File.class);

        new ValidationUtil("test-deps-a-1.0.0", "test-managed-dependency-1.0.0").validate(files);
    }

    @Test
    public void testProfileSelection2() {

        File[] files = Resolvers.use(MavenResolverSystem.class)
                .loadPomFromFile("target/poms/test-profiles.xml", "version2")
                .importCompileAndRuntimeDependencies()
                .resolve()
                .withTransitivity()
                .as(File.class);

        new ValidationUtil("test-deps-d-1.0.0", "test-managed-dependency-2.0.0").validate(files);
    }

    @Test
    public void testActiveProfileByFile() {

        File[] files = Resolvers.use(MavenResolverSystem.class)
                .loadPomFromFile("target/poms/test-profiles-file-activation.xml")
                .importCompileAndRuntimeDependencies()
                .resolve()
                .withTransitivity()
                .as(File.class);

        new ValidationUtil("test-deps-d-1.0.0", "test-deps-a-1.0.0").validate(files);
    }

    @Test
    public void testDisabledProfile() {

        File[] files = Resolvers.use(MavenResolverSystem.class)
                .loadPomFromFile("target/poms/test-profiles-file-activation.xml", "!add-dependency-a")
                .importCompileAndRuntimeDependencies()
                .resolve()
                .withTransitivity()
                .as(File.class);

        new ValidationUtil("test-deps-d-1.0.0").validate(files);
    }

    // SHRINKRES-195
    @Test
    public void testSystemPropertyOverrideFromProfile() {

        System.setProperty("shrinkres-195-enable-me", "enabled");

        MavenWorkingSession session = ((MavenWorkingSessionContainer) Maven.configureResolver()
                .fromFile("target/settings/profiles/settings-property-override.xml")
                .loadPomFromFile("target/poms/test-profiles.xml")).getMavenWorkingSession();

        Properties props = session.getParsedPomFile().getProperties();

        assertThat(props.keySet(), hasItem("myproperty"));
        assertThat(props.getProperty("myproperty"), is("hello"));
    }

}
