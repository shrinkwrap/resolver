/*
 * JBoss, Home of Professional Open Source
 * Copyright 2026, Red Hat Middleware LLC, and individual contributors
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
import java.util.Arrays;
import java.util.List;

import org.eclipse.aether.ConfigurationProperties;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.impl.maven.bootstrap.MavenSettingsBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Tests the order of resolved artifacts. Resolver 2.x defaults to level-order, ShrinkWrap keeps the pre-order
 * ordering of Resolver 1.x unless it is overridden by a system property.
 */
class ResolutionOrderTestCase {

    @BeforeAll
    static void setRemoteRepository() {
        System
                .setProperty(MavenSettingsBuilder.ALT_USER_SETTINGS_XML_LOCATION, "target/settings/profiles/settings.xml");
        System.setProperty(MavenSettingsBuilder.ALT_LOCAL_REPOSITORY_LOCATION, "target/the-other-repository");
    }

    @AfterAll
    static void clearRemoteRepository() {
        System.clearProperty(MavenSettingsBuilder.ALT_USER_SETTINGS_XML_LOCATION);
        System.clearProperty(MavenSettingsBuilder.ALT_LOCAL_REPOSITORY_LOCATION);
    }

    @AfterEach
    void clearDependencyVisitor() {
        System.clearProperty(ConfigurationProperties.REPOSITORY_SYSTEM_DEPENDENCY_VISITOR);
    }

    // test-filter depends on test-deps-a, -c, -d and -e, test-deps-c depends on test-deps-b
    @Test
    void preOrderByDefault() {
        File[] files = Maven.resolver().resolve("org.jboss.shrinkwrap.test:test-filter:pom:1.0.0")
                .withTransitivity().asFile();

        assertOrder(files, "test-deps-a", "test-deps-c", "test-deps-b", "test-deps-d", "test-deps-e");
    }

    @Test
    void levelOrderViaSystemProperty() {
        System.setProperty(ConfigurationProperties.REPOSITORY_SYSTEM_DEPENDENCY_VISITOR,
                ConfigurationProperties.REPOSITORY_SYSTEM_DEPENDENCY_VISITOR_LEVELORDER);

        File[] files = Maven.resolver().resolve("org.jboss.shrinkwrap.test:test-filter:pom:1.0.0")
                .withTransitivity().asFile();

        assertOrder(files, "test-deps-a", "test-deps-c", "test-deps-d", "test-deps-e", "test-deps-b");
    }

    private static void assertOrder(File[] files, String... expectedPrefixes) {
        List<String> actual = new ArrayList<>();
        for (File file : files) {
            String name = file.getName();
            actual.add(name.substring(0, name.lastIndexOf('-')));
        }
        Assertions.assertEquals(Arrays.asList(expectedPrefixes), actual);
    }
}
