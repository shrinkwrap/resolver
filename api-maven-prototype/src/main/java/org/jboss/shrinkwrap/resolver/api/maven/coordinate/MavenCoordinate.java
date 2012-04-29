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
public interface MavenCoordinate extends MavenCoordinateBase {

}
