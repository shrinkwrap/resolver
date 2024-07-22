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
import java.io.IOException;

import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.impl.maven.util.TestFileUtil;
import org.jboss.shrinkwrap.resolver.impl.maven.util.ValidationUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Tests for System property interpolation in Maven metadata.
 * <p>
 * See: <a href="https://issues.redhat.com/browse/SHRINKRES-42">SHRINKRES-42</a>
 *
 * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
 *
 */
class PropertiesInterpolationTestCase {

    private static final File INTERPOLATED_REPOSITORY;
    static {
        String javaVersion = System.getProperty("java.version");
        INTERPOLATED_REPOSITORY = new File("target/repository-" + javaVersion);
    }

    @BeforeAll
    static void initialize() throws IOException {
        System.clearProperty("maven.repo.local"); // May conflict with release settings
        TestFileUtil.removeDirectory(INTERPOLATED_REPOSITORY);
    }

    @Test
    void interpolateSettingsXml() {

        File file = Maven.configureResolver().fromFile("target/settings/profiles/settings-interpolation.xml")
                .resolve("org.jboss.shrinkwrap.test:test-deps-c:1.0.0")
                .withoutTransitivity().asSingle(File.class);
        Assertions.assertEquals("test-deps-c-1.0.0.jar", file.getName(), "The file is packaged as test-deps-c-1.0.0.jar");

        // check that repository was created
        Assertions.assertTrue(INTERPOLATED_REPOSITORY.exists(), "Local repository was created using interpolated ${java.version}");
    }

    @Test
    void interpolatePomWithSystemScopeXml() {

        File[] files = Maven.configureResolver().fromFile("target/settings/profiles/settings.xml")
                .resolve("org.jboss.shrinkwrap.test:test-system-scope:pom:1.0.0")
                .withTransitivity().asFile();

        new ValidationUtil("test-system-scope").validate(files);
    }
}
