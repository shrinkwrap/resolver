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
import java.util.HashMap;
import java.util.Map;

import org.eclipse.aether.artifact.DefaultArtifact;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.jboss.shrinkwrap.resolver.impl.maven.aether.ClasspathWorkspaceReader.FLATTENED_POM_PATH_KEY;
import static org.jboss.shrinkwrap.resolver.impl.maven.aether.ClasspathWorkspaceReader.SUREFIRE_CLASS_PATH_KEY;

/**
 * Tests {@link ClasspathWorkspaceReader}.
 *
 * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
 * @author <a href="https://github.com/famod">Falko Modler</a>
 * @since <a href="https://issues.redhat.com/browse/SHRINKRES-178">SHRINKRES-178</a>
 */
class ClassPathScanningTestCase {

    private final Map<String, String> originalProperties = new HashMap<>();

    @BeforeEach
    void setUp() {
        // Save the original system properties
        originalProperties.put(SUREFIRE_CLASS_PATH_KEY, System.getProperty(SUREFIRE_CLASS_PATH_KEY));
        originalProperties.put(FLATTENED_POM_PATH_KEY, System.getProperty(FLATTENED_POM_PATH_KEY));
    }

    @AfterEach
    void tearDown() {
        // Restore the original system properties
        for (Map.Entry<String, String> entry : originalProperties.entrySet()) {
            if (entry.getValue() == null) {
                System.clearProperty(entry.getKey());
            } else {
                System.setProperty(entry.getKey(), entry.getValue());
            }
        }
        originalProperties.clear();
    }

    @Test
    void classpathWithDanglingDirs() {

        System.setProperty(SUREFIRE_CLASS_PATH_KEY, createFakeClassPathWithDanglingDirs());

        ClasspathWorkspaceReader reader = new ClasspathWorkspaceReader();

        // this should not fail
        File file = reader.findArtifact(new DefaultArtifact("foo:bar:1"));
        assertThat(file, is(nullValue()));
    }

    // create a classpath that contain entries that does not have parent directories
    private String createFakeClassPathWithDanglingDirs() {

        File currentFile = new File(System.getProperty("user.dir"));
        StringBuilder cp = new StringBuilder();
        String delimiter = "";
        while (currentFile != null) {
            cp.append(delimiter).append(currentFile.getAbsolutePath());
            currentFile = currentFile.getParentFile();
            delimiter = File.pathSeparator;
        }
        return cp.toString();
    }

    /**
     * Tests that {@code ClasspathWorkspaceReader} finds the artifact for a pretty ordinary parent child setup.
     * This also tests that {@code ClasspathWorkspaceReader} does not choke on a missing {@code .flattened-pom.xml}.
     * <p/>
     * Test data: {@code src/test/resources/poms/test-ordinary}
     *
     * @since <a href="https://issues.redhat.com/browse/SHRINKRES-299">SHRINKRES-299</a>
     */
    @Test
    void ordinaryParentChild() {
        testFindArtifactReturnsNotNull("test-ordinary");
    }

    /**
     * Tests that {@code ClasspathWorkspaceReader} prefers a {@code .flattened-pom.xml} over the regular {@code pom.xml}
     * to support "Maven CI Friendly Versions".
     * <p/>
     * Test data: {@code src/test/resources/poms/test-revision}
     *
     * @since <a href="https://issues.redhat.com/browse/SHRINKRES-299">SHRINKRES-299</a>
     */
    @Test
    void mavenCiFriendlyVersion() {
        testFindArtifactReturnsNotNull("test-revision");
    }

    /**
     * Tests that {@code ClasspathWorkspaceReader} prefers a custom {@code target/my-flat-pom.xml} over the regular {@code pom.xml}
     * to support "Maven CI Friendly Versions".
     * <p/>
     * Test data: {@code src/test/resources/poms/test-revision-custom}
     *
     * @see ClasspathWorkspaceReader#FLATTENED_POM_PATH_KEY
     * @since <a href="https://issues.redhat.com/browse/SHRINKRES-299">SHRINKRES-299</a>
     */
    @Test
    void mavenCiFriendlyVersion_customFlattenedPom() {
        System.setProperty(FLATTENED_POM_PATH_KEY, "target/my-flat-pom.xml");

        testFindArtifactReturnsNotNull("test-revision-custom");
    }

    private void testFindArtifactReturnsNotNull(String testDirName) {
        final File classesDir = new File("target/poms/" + testDirName + "/child/target/classes");
        // create empty target/classes dir (would otherwise require a dummy file in src because git does not like empty dirs)
        if (!classesDir.isDirectory() && !classesDir.mkdirs()) {
            throw new IllegalStateException("Could not create " + classesDir.getAbsolutePath());
        }
        System.setProperty(SUREFIRE_CLASS_PATH_KEY, classesDir.getAbsolutePath());

        ClasspathWorkspaceReader reader = new ClasspathWorkspaceReader();

        File file = reader.findArtifact(new DefaultArtifact("org.jboss.shrinkwrap.test:" + testDirName + "-child:1.0.0"));
        assertThat(file, is(notNullValue()));
    }
}
