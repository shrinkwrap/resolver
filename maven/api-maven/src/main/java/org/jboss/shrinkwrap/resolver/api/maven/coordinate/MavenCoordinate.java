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

import org.jboss.shrinkwrap.resolver.api.maven.MavenResolvedArtifact;
import org.jboss.shrinkwrap.resolver.api.maven.PackagingType;

/**
 * Represents a single Maven coordinate (an address in canonical form
 * <code>"groupId:artifactId:packaging:classifier:version"</code>) which is capable of resolving to an artifact.
 * <p>
 * Also note that since "packaging" and "classifier" are optional, the following canonical forms are also valid:
 *
 * <ul>
 * <li>{@code groupId:artifactId:packaging:version}</li><li>{@code groupId:artifactId:version}</li>
 * </ul>
 *
 * When comparing equality by value, all fields except for "version" are considered.
 * <p>
 * To match the {@code <dependency />} sections in POM metadata, the {@code packaging} field is also aliased as
 * <code>"type"</code> operations.
 * <p>
 * Type can represent both packaging and classifier for some of the use cases, like {@code <type>test-jar</type>}. In such
 * cases,
 * type will act as specifier for both packaging and classifier transparently to user.
 *
 * @see <a href="http://maven.apache.org/pom.html#Maven_Coordinates">Maven Coordinates</a>
 * @see <a href="http://docs.codehaus.org/display/MAVEN/Packaging+vs+Type+-+Derived+and+Attached+Artifacts">
 *     Packaging vs Type - Derived and Attached Artifacts</a>
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
public interface MavenCoordinate extends MavenGABase {
    /**
     * Returns the "packaging" portion of this artifact's coordinates; always returns a value. Defaults to
     * {@link PackagingType#JAR}.
     *
     * @return The "packaging" portion of this artifact's coordinates
     */
    PackagingType getPackaging();

    /**
     * Alias to {@link MavenCoordinate#getPackaging()}.
     *
     * @return The "packaging" portion of this artifact's coordinates
     */
    PackagingType getType();

    /**
     * Returns the "classifier" portion of this artifact's coordinates.
     *
     * @return The "classifier" portion of this artifact's coordinates.
     */
    String getClassifier();

    /**
     * Returns the declared "version" portion of this artifact's coordinates, for instance "1.2.0-alpha-2" or
     * "1.2.0-SNAPSHOT". This is the value of the "version" field as declared in the POM. During artifact resolution,
     * SNAPSHOT versions may be set to a fixed SNAPSHOT as represented by {@link MavenResolvedArtifact#getResolvedVersion()}.
     *
     * @return The base version, never {@code null}.
     */
    String getVersion();

    /**
     * {@inheritDoc}
     *
     */
    @Override
    int hashCode();

    /**
     * Determines whether two {@link MavenCoordinate} instances are equal by value; all fields are considered except
     * for <code>version</code>
     *
     */
    @Override
    boolean equals(Object other);
}
