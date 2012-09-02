/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
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
import org.jboss.shrinkwrap.resolver.api.maven.ConfiguredResolveStage;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.impl.maven.bootstrap.MavenSettingsBuilder;
import org.jboss.shrinkwrap.resolver.impl.maven.util.FileUtil;
import org.jboss.shrinkwrap.resolver.impl.maven.util.ValidationUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests that resolution of archives from a ClassPath-based repository works as expected
 *
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
public class ClasspathWorkspaceReaderTestCase {

    @BeforeClass
    public static void setRemoteRepository() {
        System.setProperty(MavenSettingsBuilder.ALT_LOCAL_REPOSITORY_LOCATION, "target/non-existing-repository");
    }

    /**
     * Cleanup, remove the repositories from previous tests
     */
    @Before
    public void cleanup() throws Exception {
        FileUtil.removeDirectory(new File("target/non-existing-repository"));
    }

    @Test(expected = NoResolvedResultException.class)
    public void shouldFailWhileNotReadingReactor() {

        ConfiguredResolveStage resolver = Maven.resolver().configureFromPom("pom.xml");
        // Ensure we can disable ClassPath resolution
        resolver.resolve("org.jboss.shrinkwrap.resolver:shrinkwrap-resolver-api-maven-prototype")
            .withClassPathResolution(false).withoutTransitivity().asSingle(File.class);
        Assert.fail("Reactor is not activated, resolution of another module should fail.");
    }

    @Test
    public void shouldBeAbleToLoadArtifactDirectlyFromClassPath() {

        // Ensure we can use ClassPath resolution to get the results of the "current" build
        final ConfiguredResolveStage resolver = Maven.resolver().configureFromPom("pom.xml");
        File[] files = resolver.resolve("org.jboss.shrinkwrap.resolver:shrinkwrap-resolver-api-maven-prototype")
            .withTransitivity().as(File.class);
        new ValidationUtil("shrinkwrap-resolver-api-prototype", "shrinkwrap-resolver-api-maven-prototype")
            .validate(files);
    }

}
