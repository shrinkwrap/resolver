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

import org.jboss.shrinkwrap.resolver.api.Coordinate;

/**
 * Represents base properties common to both {@link MavenCoordinate} or {@link MavenDependencyExclusion}
 *
 * @see <a href="http://maven.apache.org/pom.html#Maven_Coordinates">Maven Coordinates</a>
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
interface MavenGABase extends Coordinate {

    /**
     * Returns the "groupId" portion of this artifact's coordinates
     *
     * @return The "groupId" portion of this artifact's coordinates
     */
    String getGroupId();

    /**
     * Returns the "artifactId" portion of this artifact's coordinates
     *
     * @return The "artifactId" portion of this artifact's coordinates
     */
    String getArtifactId();

}
