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
package org.jboss.shrinkwrap.resolver.impl.maven.aether;

import java.io.File;

import org.eclipse.aether.artifact.DefaultArtifact;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;

/**
 *
 * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
 *
 */
// SHRINKRES-178
public class ClassPathScanningTestCase {

    private static final String SUREFIRE_CP_KEY = "surefire.test.class.path";
    private static String originalSurefireClasspath;

    @Before
    public void storeSurefireCP() {
        originalSurefireClasspath = System.getProperty(SUREFIRE_CP_KEY);
    }

    @After
    public void restoreSurefireCP() {
        if (originalSurefireClasspath == null) {
            System.clearProperty(SUREFIRE_CP_KEY);
        }
        else {
            System.setProperty(SUREFIRE_CP_KEY, originalSurefireClasspath);
        }
    }

    @Test
    public void classpathWithDanglingDirs() throws Exception {

        System.setProperty(SUREFIRE_CP_KEY, createFakeClassPath());

        ClasspathWorkspaceReader reader = new ClasspathWorkspaceReader();

        // this should not fail
        File file = reader.findArtifact(new DefaultArtifact("foo:bar:1"));
        Assert.assertThat(file, is(nullValue()));
    }

    // create a classpath that contain entries that does not have parent directories
    private String createFakeClassPath() {

        File currentFile = new File(System.getProperty("user.dir"));
        StringBuilder cp = new StringBuilder();
        String delimiter = "";
        while (currentFile != null) {
            cp.append(delimiter).append(currentFile.getAbsolutePath());
            currentFile = currentFile.getParentFile();
            delimiter = ":";
        }
        return cp.toString();
    }
}
