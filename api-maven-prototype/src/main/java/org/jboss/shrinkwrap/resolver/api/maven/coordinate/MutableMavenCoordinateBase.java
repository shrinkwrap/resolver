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

/**
 * Defines base operations for the mutable view of a single Maven coordinate (also referred to as a "GAV" or
 * "groupId:artifactId:version") which is capable of resolving to an artifact. For concrete usage refer to
 * {@link MutableMavenCoordinate}.
 *
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
public interface MutableMavenCoordinateBase<COORDINATETYPE extends MutableMavenCoordinateBase<COORDINATETYPE>> extends
    MutableMavenGABase<COORDINATETYPE>, MavenCoordinateBase {

    /**
     * Sets the specified version, returning this coordinate. <code>null</code> value permitted.
     *
     * @param version
     * @return
     */
    COORDINATETYPE version(String version) throws IllegalArgumentException;

    /**
     * Sets the specified packaging, returning this coordinate. <code>null</code> value permitted; will default to
     * {@link PackagingType#JAR}.
     *
     * @param packagingType
     * @return
     */
    COORDINATETYPE packaging(PackagingType packagingType);

    /**
     * Sets the specified type, alias to "packaging" as defined in a POM, returning this coordinate. <code>null</code>
     * value permitted; will default to {@link PackagingType#JAR}.
     *
     * @param packagingType
     * @return
     */
    COORDINATETYPE type(PackagingType packagingType);

    /**
     * Sets the classifier, returning this coordinate. <code>null</code> value permitted.
     *
     * @param classifier
     * @return
     * @throws IllegalArgumentException
     */
    COORDINATETYPE classifier(String classifier) throws IllegalArgumentException;

}
