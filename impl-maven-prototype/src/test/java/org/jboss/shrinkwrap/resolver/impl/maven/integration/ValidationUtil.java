/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat Middleware LLC, and individual contributors
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.jboss.shrinkwrap.resolver.api.CoordinateParseException;
import org.jboss.shrinkwrap.resolver.api.maven.ScopeType;
import org.junit.Assert;

/**
 * Sets a set of files or archives and checks that returned files start with the same names.
 *
 * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
 *
 */
public class ValidationUtil {
    private String[] files;

    private Map<String, Boolean> flags;

    public ValidationUtil(String... allowedFiles) {
        this.files = allowedFiles;
        this.flags = new HashMap<String, Boolean>(files.length);
        for (String file : allowedFiles) {
            flags.put(file, Boolean.FALSE);
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

    public static ValidationUtil fromDependencyTree(File dependencyTree, List<String> allowedScopes)
        throws IllegalArgumentException {

        List<String> files = new ArrayList<String>();

        try {
            BufferedReader input = new BufferedReader(new FileReader(dependencyTree));

            String line = null;
            while ((line = input.readLine()) != null) {
                ArtifactHolder holder = new ArtifactHolder(line);
                if (holder.root == true && !"jar".equals(holder.extension)) {
                    // skip non-jar from dependency tree
                    continue;
                }
                // add artifact if in allowed scope
                else if (allowedScopes.isEmpty() || (!allowedScopes.isEmpty() && allowedScopes.contains(holder.scope))) {
                    files.add(holder.filename());
                }
            }
        } catch (IOException e) {
            throw new CoordinateParseException(MessageFormat.format(
                "Unable to load dependency tree from {0} to verify", dependencyTree));
        }

        return new ValidationUtil(files.toArray(new String[0]));
    }

    public void validate(File single) throws AssertionError {
        validate(new File[] { single });
    }

    public void validate(File[] array) throws AssertionError {
        Assert.assertNotNull("There must be some files passed for validation, but the array was null", array);

        StringBuilder passedFiles = new StringBuilder();
        for (File f : array) {
            passedFiles.append(f.getName()).append(",");
        }
        if (passedFiles.length() > 0) {
            passedFiles.deleteCharAt(passedFiles.length() - 1);
        }

        for (File f : array) {
            for (String fname : files) {
                if (f.getName().startsWith(fname)) {
                    flags.put(fname, Boolean.TRUE);
                }
            }
        }

        StringBuilder sb = new StringBuilder("There must be ").append(files.length).append(" files resolved, however ")
            .append(array.length).append(" files were resolved. ").append("Resolution contains: \n")
            .append(passedFiles).append("\n").append("Expected, but missing files were: \n");
        boolean success = true;

        for (Map.Entry<String, Boolean> entry : flags.entrySet()) {
            if (!entry.getValue()) {
                success = false;
                sb.append(entry.getKey()).append("\n");
            }
        }

        if (!success) {
            throw new AssertionError(sb.toString());
        }
    }
}

/**
 * A holder for a line generated from Maven dependency tree plugin
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 *
 */
class ArtifactHolder {

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
    ArtifactHolder(String dependencyCoords) {
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

            this.scope = "";
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
        ArtifactHolder other = (ArtifactHolder) obj;
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

        throw new IllegalArgumentException("Invalid format of the dependency coordinates for artifact scope: " + scope);

    }

}