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
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests for System property interpolation in Maven metadata.
 *
 * SHRINKRES-42
 *
 * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
 *
 */
public class PropertiesInterpolationTestCase {

    private static final File INTERPOLATED_REPOSITORY;
    static {
        String javaVersion = System.getProperty("java.version");
        INTERPOLATED_REPOSITORY = new File("target/repository-" + javaVersion);
    }

    @BeforeClass
    public static void initialize() throws IOException {
        System.clearProperty("maven.repo.local"); // May conflict with release settings
        TestFileUtil.removeDirectory(INTERPOLATED_REPOSITORY);
    }

    @Test
    public void interpolateSettingsXml() {

        File file = Maven.configureResolver().fromFile("target/settings/profiles/settings-interpolation.xml")
                .resolve("org.jboss.shrinkwrap.test:test-deps-c:1.0.0")
                .withoutTransitivity().asSingle(File.class);
        Assert.assertEquals("The file is packaged as test-deps-c-1.0.0.jar", "test-deps-c-1.0.0.jar", file.getName());

        // check that repository was created
        Assert.assertTrue("Local repository was created using interpolated ${java.version}", INTERPOLATED_REPOSITORY.exists());
    }

    private static int javaVersion() {
        String version = System.getProperty("java.version");
        if(version.startsWith("1.")) {
            version = version.substring(2, 3);
        } else {
            int dot = version.indexOf(".");
            if(dot != -1) { version = version.substring(0, dot); }
        }
        // handle when jvm reports 13-ea for early-access versions and similar.
        version = version.replace("-ea", "");
        return Integer.parseInt(version);
    }

    @Test
    public void interpolatePomWithSystemScopeXml() {
        // looks for tools.jar
        String filePrefix = "tools";
        String systemScopeArtifact = "org.jboss.shrinkwrap.test:test-system-scope:pom:1.0.0";
        if(javaVersion()>9) { // after 9 we look for jrt-fs.jar
            filePrefix = "jrt-fs";
            systemScopeArtifact = "org.jboss.shrinkwrap.test:test-system-scope:pom:9.0.0";
        }

        File[] files = Maven.configureResolver().fromFile("target/settings/profiles/settings.xml")
                .resolve(systemScopeArtifact)
                .withTransitivity().asFile();

        new ValidationUtil(filePrefix).validate(files);
    }
}
