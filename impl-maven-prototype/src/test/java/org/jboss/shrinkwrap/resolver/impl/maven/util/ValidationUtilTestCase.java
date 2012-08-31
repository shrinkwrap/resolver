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
package org.jboss.shrinkwrap.resolver.impl.maven.util;

import java.io.File;

import org.jboss.shrinkwrap.resolver.api.maven.ScopeType;
import org.junit.Test;

/**
 * Test cases to assert that the {@link ValidationUtil} is working as expected
 *
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
public class ValidationUtilTestCase {

    private static final String SUFFIX_FILE = "-1.0.0.jar";

    private static final File FILE_TREE = new File("src/test/resources/dependency-trees/test-child.tree");

    /**
     * Ensures that all scopes may be validated
     */
    @Test
    public void fromDependencyTreeAllScopes() {
        final String[] expectedFiles = new String[] { "test-child", "test-managed-dependency", "test-dependency",
            "test-deps-a", "test-deps-b", "test-dependency-with-exclusion", "test-deps-i", "test-dependency-provided",
            "test-dependency-test", "test-deps-d", "test-deps-f", "test-deps-g", "test-deps-h" };
        validate(expectedFiles, ScopeType.values());
    }

    @Test(expected = AssertionError.class)
    public void fails() {
        validate(new String[] { "fakeFile" });
    }

    /**
     * Ensures that only "compile" scope (and the root) is to be validated
     */
    @Test
    public void fromDependencyTreeCompileScope() {
        final String[] expectedFiles = new String[] { "test-child", "test-managed-dependency", "test-dependency",
            "test-deps-a", "test-dependency-with-exclusion", "test-deps-i" };
        validate(expectedFiles, ScopeType.COMPILE);
    }

    /**
     * Ensures that only "compile" and "provided" scopes (and the root) are to be validated
     */
    @Test
    public void fromDependencyTreeCompileAndProvidedScopes() {
        final String[] expectedFiles = new String[] { "test-child", "test-managed-dependency", "test-dependency",
            "test-deps-a", "test-dependency-with-exclusion", "test-dependency-provided", "test-deps-i" };
        validate(expectedFiles, ScopeType.COMPILE, ScopeType.PROVIDED);
    }

    /**
     * Ensures that only "test" scope (and the root) is to be validated
     */
    @Test
    public void fromDependencyTreeTestScope() {
        final String[] expectedFiles = new String[] { "test-child", "test-dependency-test", "test-deps-d",
            "test-deps-f", "test-deps-g", "test-deps-h" };
        validate(expectedFiles, ScopeType.TEST);
    }

    private void validate(final String[] expectedFilePrefixes, final ScopeType... scopeTypes) {
        final ValidationUtil util = ValidationUtil.fromDependencyTree(FILE_TREE, scopeTypes);
        util.validate(this.fromStrings(expectedFilePrefixes));
    }

    private File[] fromStrings(final String... fileNames) {
        assert fileNames != null : "File names are required";
        final int fileNamesCount = fileNames.length;
        final File[] files = new File[fileNamesCount];
        for (int i = 0; i < fileNamesCount; i++) {
            files[i] = fromPrefix(fileNames[i]);
        }
        return files;
    }

    private File fromPrefix(final String name) {
        return new File(name + SUFFIX_FILE);
    }

}
