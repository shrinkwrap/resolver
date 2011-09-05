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
package org.jboss.shrinkwrap.resolver.api;

/**
 * Encapsulates access to a backing store or repository; Accepts coordinates intended to resolve to a set of dependencies.
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
public interface DependencyBuilder<T extends DependencyBuilder<T>> {
    /**
     * Creates an artifact builder. You can define additional parameters for the artifact later. Additional parameters will be
     * changed for all artifacts defined by this call.
     *
     * @param coordinates Coordinates specified to a created artifact, specified in an implementation-specific format.
     *
     * @return This {@link DependencyBuilder}
     * @throws ResolutionException If artifact coordinates are wrong or if version cannot be determined.
     */
    T artifact(String coordinates) throws ResolutionException;

    /**
     * Creates an artifact builder. You can define additional parameters for the artifacts later. Additional parameters will be
     * changed for all artifacts defined by this call.
     *
     * @param coordinates A list of coordinates specified to the created artifacts, specified in an implementation-specific
     *        format.
     * @return This {@link DependencyBuilder}
     * @throws ResolutionException If artifact coordinates are wrong or if version cannot be determined.
     */
    T artifacts(String... coordinates) throws ResolutionException;
}
