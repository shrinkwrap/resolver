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
package org.jboss.shrinkwrap.resolver.impl.maven.integration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import org.jboss.shrinkwrap.resolver.api.CoordinateParseException;
import org.jboss.shrinkwrap.resolver.api.maven.ScopeType;
import org.junit.Assert;

/**
 * Sets a set of files or archives and checks that returned files start with the same names.
 *
 * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
public class ValidationUtil {
    private final Collection<String> requiredFileNamePrefixes;

    /**
     * Creates a new instance, specifying only the valid file name prefixes permitted in the
     * {@link ValidationUtil#validate(File[])} calls.
     *
     * @param requiredFileNamePrefixes
     */
    public ValidationUtil(final String... requiredFileNamePrefixes) {
        this.requiredFileNamePrefixes = new ArrayList<String>(requiredFileNamePrefixes.length);
        for (final String file : requiredFileNamePrefixes) {
            this.requiredFileNamePrefixes.add(file);
        }
    }

    public static ValidationUtil fromDependencyTree(File dependencyTree, ScopeType... allowedScopesArray)
        throws IllegalArgumentException {
        List<String> allowedScopes = new ArrayList<String>();
        for (ScopeType scope : allowedScopesArray) {
            allowedScopes.add(scope.toString());
        }
        return fromDependencyTree(dependencyTree, allowedScopes);
    }

    public static ValidationUtil fromDependencyTree(final File dependencyTree, final List<String> allowedScopes)
        throws IllegalArgumentException {

        List<String> files = new ArrayList<String>();

        // Adjust; if COMPILE scope is specified, then "runtime" is inferred
        if (allowedScopes.contains("compile") && !allowedScopes.contains("runtime")) {
            allowedScopes.add("runtime");
        }

        BufferedReader input = null;
        try {
            input = new BufferedReader(new FileReader(dependencyTree));

            String line = null;
            while ((line = input.readLine()) != null) {
                final ArtifactMetaData artifact = new ArtifactMetaData(line);
                if (artifact.root == true) {
                    if (!"jar".equals(artifact.extension)) {
                        // skip non-jar from dependency tree
                        continue;
                    }

                    // Add, scope doesn't matter for the root
                    files.add(artifact.filename());
                }
                // add artifact if in allowed scope
                else if (allowedScopes.isEmpty() || allowedScopes.contains(artifact.scope)) {
                    files.add(artifact.filename());
                }
            }
        } catch (final IOException e) {
            throw new CoordinateParseException(MessageFormat.format(
                "Unable to load dependency tree from {0} to verify", dependencyTree));
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (final IOException ioe) {
                    // Swallow
                }
            }
        }

        return new ValidationUtil(files.toArray(new String[0]));
    }

    public void validate(final File single) throws AssertionError {
        validate(new File[] { single });
    }

    public void validate(final File[] resolvedFiles) throws AssertionError {
        Assert.assertNotNull("There must be some files passed for validation, but the array was null", resolvedFiles);

        final Collection<String> resolvedFileNames = new ArrayList<String>(resolvedFiles.length);
        for (final File resolvedFile : resolvedFiles) {
            resolvedFileNames.add(resolvedFile.getName());
        }

        final Collection<String> foundNotAllowed = new ArrayList<String>();
        final Collection<String> requiredNotFound = new ArrayList<String>();

        // Check for resolved files found but not allowed
        for (final String resolvedFileName : resolvedFileNames) {
            boolean found = false;
            for (final String requiredFileName : this.requiredFileNamePrefixes) {
                if (resolvedFileName.startsWith(requiredFileName)) {
                    found = true;
                }
            }
            if (!found) {
                foundNotAllowed.add(resolvedFileName);
            }
        }
        // Check for required files not found in those resolved
        for (final String requiredFileName : this.requiredFileNamePrefixes) {
            boolean found = false;
            for (final String resolvedFileName : resolvedFileNames) {
                if (resolvedFileName.startsWith(requiredFileName)) {
                    found = true;
                }
            }
            if (!found) {
                requiredNotFound.add(requiredFileName);
            }
        }

        // We're all good in the hood
        if (foundNotAllowed.size() == 0 && requiredNotFound.size() == 0) {
            // Get outta here
            return;
        }

        // Problems; report 'em
        final StringBuilder errorMessage = new StringBuilder().append(requiredFileNamePrefixes.size())
            .append(" files required to be resolved, however ").append(resolvedFiles.length)
            .append(" files were resolved. ").append("Resolution contains: \n");
        if (foundNotAllowed.size() > 0) {
            errorMessage.append("\tFound but not allowed:\n\t\t");
            errorMessage.append(foundNotAllowed.toString());
        }
        if (requiredNotFound.size() > 0) {
            errorMessage.append("\tRequired but not found:\n\t\t");
            errorMessage.append(requiredNotFound.toString());
        }
        Assert.fail(errorMessage.toString());
    }

    /**
     * A holder for a line generated from Maven dependency tree plugin
     *
     * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
     *
     */
    private static class ArtifactMetaData {

        private static final String SCOPE_ROOT = "";

        final String groupId;
        final String artifactId;
        final String extension;
        final String classifier;
        final String version;
        final String scope;

        final boolean root;

        /**
         * Creates an artifact holder from the input lien
         *
         * @param dependencyCoords
         */
        ArtifactMetaData(String dependencyCoords) {

            int index = 0;
            while (index < dependencyCoords.length()) {
                char c = dependencyCoords.charAt(index);
                if (c == '\\' || c == '|' || c == ' ' || c == '+' || c == '-') {
                    index++;
                } else {
                    break;
                }
            }

            for (int testIndex = index, i = 0; i < 4; i++) {
                testIndex = dependencyCoords.substring(testIndex).indexOf(":");
                if (testIndex == -1) {
                    throw new IllegalArgumentException("Invalid format of the dependency coordinates for "
                        + dependencyCoords);
                }
            }

            StringTokenizer st = new StringTokenizer(dependencyCoords.substring(index), ":");

            this.groupId = st.nextToken();
            this.artifactId = st.nextToken();
            this.extension = st.nextToken();

            // this is the root artifact
            if (index == 0) {
                this.root = true;

                if (st.countTokens() == 1) {
                    this.classifier = "";
                    this.version = st.nextToken();
                } else if (st.countTokens() == 2) {
                    this.classifier = st.nextToken();
                    this.version = st.nextToken();
                } else {
                    throw new IllegalArgumentException("Invalid format of the dependency coordinates for "
                        + dependencyCoords);
                }

                this.scope = SCOPE_ROOT;
            }
            // otherwise
            else {
                this.root = false;

                if (st.countTokens() == 2) {
                    this.classifier = "";
                    this.version = st.nextToken();
                    this.scope = extractScope(st.nextToken());
                } else if (st.countTokens() == 3) {
                    this.classifier = st.nextToken();
                    this.version = st.nextToken();
                    this.scope = extractScope(st.nextToken());
                } else {
                    throw new IllegalArgumentException("Invalid format of the dependency coordinates for "
                        + dependencyCoords);
                }
            }
        }

        public String filename() {
            StringBuilder sb = new StringBuilder();
            sb.append(artifactId).append("-").append(version);
            if (classifier.length() != 0) {
                sb.append("-").append(classifier);
            }
            sb.append(".").append(extension);

            return sb.toString();
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();

            sb.append("groupId=").append(groupId).append(", ");
            sb.append("artifactId=").append(artifactId).append(", ");
            sb.append("type=").append(extension).append(", ");
            sb.append("version=").append(version);

            if (scope != "") {
                sb.append(", scope=").append(scope);
            }

            if (classifier != "") {
                sb.append(", classifier=").append(classifier);
            }

            return sb.toString();
        }

        /*
         * (non-Javadoc)
         *
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((artifactId == null) ? 0 : artifactId.hashCode());
            result = prime * result + ((classifier == null) ? 0 : classifier.hashCode());
            result = prime * result + ((extension == null) ? 0 : extension.hashCode());
            result = prime * result + ((groupId == null) ? 0 : groupId.hashCode());
            result = prime * result + (root ? 1231 : 1237);
            result = prime * result + ((scope == null) ? 0 : scope.hashCode());
            result = prime * result + ((version == null) ? 0 : version.hashCode());
            return result;
        }

        /*
         * (non-Javadoc)
         *
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            ArtifactMetaData other = (ArtifactMetaData) obj;
            if (artifactId == null) {
                if (other.artifactId != null) {
                    return false;
                }
            } else if (!artifactId.equals(other.artifactId)) {
                return false;
            }
            if (classifier == null) {
                if (other.classifier != null) {
                    return false;
                }
            } else if (!classifier.equals(other.classifier)) {
                return false;
            }
            if (extension == null) {
                if (other.extension != null) {
                    return false;
                }
            } else if (!extension.equals(other.extension)) {
                return false;
            }
            if (groupId == null) {
                if (other.groupId != null) {
                    return false;
                }
            } else if (!groupId.equals(other.groupId)) {
                return false;
            }
            if (root != other.root) {
                return false;
            }
            if (scope == null) {
                if (other.scope != null) {
                    return false;
                }
            } else if (!scope.equals(other.scope)) {
                return false;
            }
            if (version == null) {
                if (other.version != null) {
                    return false;
                }
            } else if (!version.equals(other.version)) {
                return false;
            }
            return true;
        }

        private String extractScope(String scope) {
            int lparen = scope.indexOf("(");
            int rparen = scope.indexOf(")");
            int space = scope.indexOf(" ");

            if (lparen == -1 && rparen == -1 && space == -1) {
                return scope;
            } else if (lparen != -1 && rparen != -1 && space != -1) {
                return scope.substring(0, space);
            }

            throw new IllegalArgumentException("Invalid format of the dependency coordinates for artifact scope: "
                + scope);

        }

    }

}
