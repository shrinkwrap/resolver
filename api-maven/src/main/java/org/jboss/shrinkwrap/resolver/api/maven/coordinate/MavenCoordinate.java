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

import org.jboss.shrinkwrap.resolver.api.maven.PackagingType;
import org.jboss.shrinkwrap.resolver.api.maven.ResolvedArtifactInfo;

/**
 * Represents a single Maven coordinate (an address in canonical form
 * <code>"groupId:artifactId:packaging:classifier:version"</code>) which is capable of resolving to an artifact.
 *
 * Also note that since "packaging" and "classifier" are optional, the following canonical forms are also valid:
 *
 * <ul>
 * <li><code>groupId:artifactId:packaging:version</code></li></li><code>groupId:artifactId:version</code></li>
 * </ul>
 *
 * When comparing equality by value, all fields except for "version" are considered.
 *
 * To match the <code><dependency /><code> sections in POM metadata, the <code>packaging</code> field is also aliased as
 * <code>"type"</code> operations.
 *
 * @see http://maven.apache.org/pom.html#Maven_Coordinates
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
public interface MavenCoordinate extends MavenGABase {
    /**
     * Returns the "packaging" portion of this artifact's coordinates; always returns a value. Defaults to
     * {@link PackagingType#JAR}.
     *
     * @return
     */
    PackagingType getPackaging();

    /**
     * Alias to {@link MavenCoordinate#getPackaging()}.
     *
     * @return
     */
    PackagingType getType();

    /**
     * Returns the "classifier" portion of this artifact's coordinates.
     *
     * @return
     */
    String getClassifier();

    /**
     * Returns the declared "version" portion of this artifact's coordinates, for instance "1.2.0-alpha-2" or
     * "1.2.0-SNAPSHOT". This is the value of the "version" field as declared in the POM. During artifact resolution,
     * SNAPSHOT versions may be set to a fixed SNAPSHOT as represented by
     * {@link ResolvedArtifactInfo#getResolvedVersion()}.
     *
     * @return The base version, never {@code null}.
     */
    String getVersion();

    /**
     * {@inheritDoc}
     *
     * @return
     */
    @Override
    int hashCode();

    /**
     * Determines whether two {@link MavenCoordinate} instances are equal by value; all fields are considered exception
     * for <code>version</code>
     *
     * @param other
     * @return
     */
    @Override
    boolean equals(Object other);
}
