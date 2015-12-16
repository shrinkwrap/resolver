/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.shrinkwrap.resolver.impl.maven.bootstrap;

import java.io.File;

import org.jboss.shrinkwrap.resolver.api.NoResolvedResultException;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.impl.maven.util.ValidationUtil;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test cases for System property precedence in ShrinkWrap configuration
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 *
 */
public class SystemPropertyPrecedenceTestCase {

    private static final String SETTINGS_XML_PATH = "target/settings/profiles/settings.xml";

    @BeforeClass
    public static void initialize() {
        System.clearProperty("maven.repo.local"); // May conflict with release settings
        System.setProperty(MavenSettingsBuilder.ALT_USER_SETTINGS_XML_LOCATION, " "); // without space it will be
                                                                                      // ignored, and users settings
                                                                                      // will be used!
    }

    @Test
    public void overrideUserSettings() {
        System.setProperty(MavenSettingsBuilder.ALT_USER_SETTINGS_XML_LOCATION, SETTINGS_XML_PATH);

        File[] files = Maven.resolver().resolve("org.jboss.shrinkwrap.test:test-deps-c:1.0.0").withTransitivity()
            .as(File.class);

        ValidationUtil.fromDependencyTree(new File("src/test/resources/dependency-trees/test-deps-c.tree")).validate(
            true, files);

        System.setProperty(MavenSettingsBuilder.ALT_USER_SETTINGS_XML_LOCATION, " ");
    }

    @Test
    public void overrideGlobalSettings() {
        System.setProperty(MavenSettingsBuilder.ALT_GLOBAL_SETTINGS_XML_LOCATION,
            "target/settings/profiles/settings.xml");

        File[] files = Maven.resolver().resolve("org.jboss.shrinkwrap.test:test-deps-c:1.0.0").withTransitivity()
            .as(File.class);

        ValidationUtil.fromDependencyTree(new File("src/test/resources/dependency-trees/test-deps-c.tree")).validate(true,
            files);

        System.clearProperty(MavenSettingsBuilder.ALT_GLOBAL_SETTINGS_XML_LOCATION);

    }

    @Test(expected = NoResolvedResultException.class)
    public void overrideOfflineFlag() {

        try {
            System.setProperty(MavenSettingsBuilder.ALT_MAVEN_OFFLINE, "true");

            Maven.configureResolver().fromFile(SETTINGS_XML_PATH).resolve("junit:junit:3.8.2").withTransitivity()
                .as(File.class);

            Assert.fail("Artifact junit:junit:3.8.2 should not be present in local repository");
        } finally {
            // this has to be executed in finally block
            System.clearProperty(MavenSettingsBuilder.ALT_MAVEN_OFFLINE);
        }
    }

    @Test
    public void overrideLocalRepositoryLocation() {
        System.setProperty(MavenSettingsBuilder.ALT_LOCAL_REPOSITORY_LOCATION, "target/syspropertyrepo");

        File[] files = Maven.configureResolver().fromFile(SETTINGS_XML_PATH)
            .resolve("org.jboss.shrinkwrap.test:test-deps-c:1.0.0").withTransitivity().as(File.class);

        ValidationUtil.fromDependencyTree(new File("src/test/resources/dependency-trees/test-deps-c.tree")).validate(true,
            files);

        // Assert file was downloaded into syspropertyrepo directory
        File testDep = new File(
            "target/syspropertyrepo/org/jboss/shrinkwrap/test/test-deps-c/1.0.0/test-deps-c-1.0.0.jar");
        Assert.assertTrue("Sysproperty local repository took precedence", testDep.exists());

        System.clearProperty(MavenSettingsBuilder.ALT_LOCAL_REPOSITORY_LOCATION);
    }

}
