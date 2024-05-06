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
package org.jboss.shrinkwrap.resolver.api.maven.coordinate;

/**
 * Immutable, thread-safe implementation of {@link MavenDependencyExclusion}
 *
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
final class MavenDependencyExclusionImpl extends MavenGABaseImpl implements MavenDependencyExclusion {

    /**
     * Creates a new instance with the specified properties. <code>groupId</code> and <code>artifactId</code> are
     * required.
     *
     * @param groupId The group ID of the dependency exclusion.
     * @param artifactId The artifact ID of the dependency exclusion.
     */
    MavenDependencyExclusionImpl(final String groupId, final String artifactId) {

        // Precondition checks covered by superclass
        super(groupId, artifactId);

    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return MavenDependencyExclusion.class.getSimpleName() + " [" + toCanonicalForm() + "]";
    }
}
