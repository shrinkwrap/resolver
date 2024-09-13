/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014, Red Hat Middleware LLC, and individual contributors
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

import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolvedArtifact;
import org.jboss.shrinkwrap.resolver.api.maven.PackagingType;
import org.jboss.shrinkwrap.resolver.impl.maven.bootstrap.MavenSettingsBuilder;
import org.jboss.shrinkwrap.resolver.impl.maven.util.ValidationUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Various tests for EJB packaging
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 *
 */
class EjbTestCase {

    private static final String TEST_REPOSITORY_ENABLED_SETTINGS = "target/settings/profiles/settings.xml";

    @BeforeAll
    static void setRemoteRepository() {
        System.setProperty(MavenSettingsBuilder.ALT_LOCAL_REPOSITORY_LOCATION, "target/the-other-repository");
    }

    @AfterAll
    static void clearRemoteRepository() {
        System.clearProperty(MavenSettingsBuilder.ALT_LOCAL_REPOSITORY_LOCATION);
    }

    // SHRINKRES-182
    @Test
    void resolveEjbFromCentral() {
        MavenResolvedArtifact ejb = Maven.resolver().resolve("org.wicketstuff:javaee-inject-example-ejb:ejb:6.15.0")
                .withoutTransitivity()
                .asSingleResolvedArtifact();

        Assertions.assertNotNull(ejb);
        Assertions.assertNotNull(ejb.asFile());
        Assertions.assertEquals("jar", ejb.getExtension());
        Assertions.assertEquals(PackagingType.EJB, ejb.getCoordinate().getPackaging());
    }

    // SHRINKRES-182
    @Test
    void resolveEjbFromLocalRepository() {
        MavenResolvedArtifact ejb = Maven.configureResolver().fromFile(TEST_REPOSITORY_ENABLED_SETTINGS)
                .resolve("org.jboss.shrinkwrap.test:test-ejb:ejb:1.0.0")
                .withoutTransitivity()
                .asSingleResolvedArtifact();

        Assertions.assertNotNull(ejb);
        Assertions.assertNotNull(ejb.asFile());
        Assertions.assertEquals("jar", ejb.getExtension());
        Assertions.assertEquals(PackagingType.EJB, ejb.getCoordinate().getPackaging());
    }

    // SHRINKRES-182
    @Test
    void resolveEjbFromPom() {
        MavenResolvedArtifact ejb = Maven.configureResolver().fromFile(TEST_REPOSITORY_ENABLED_SETTINGS)
                .loadPomFromFile("target/poms/test-deps-ejb.xml")
                .importCompileAndRuntimeDependencies()
                .resolve().withTransitivity().asSingleResolvedArtifact();

        Assertions.assertNotNull(ejb);
        Assertions.assertNotNull(ejb.asFile());
        Assertions.assertEquals("jar", ejb.getExtension());
        Assertions.assertEquals(PackagingType.EJB, ejb.getCoordinate().getPackaging());
        new ValidationUtil("test-ejb").validate(ejb.asFile());

    }
}
