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
package org.jboss.shrinkwrap.resolver.impl.maven;

import java.util.Set;
import java.util.Stack;

import org.jboss.shrinkwrap.resolver.api.maven.MavenDependency;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;

/**
 * Internal SPI to expose out required elements of the {@link MavenBuilderImpl} to inner classes for use as delegate methods
 *
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
interface MavenDependencyResolverInternal extends MavenDependencyResolver {

    /**
     * Gets all the dependencies marked by Resolver to be resolved
     *
     * @return the stack which represents content of MavenDependencyResolver
     */
    Stack<MavenDependency> getDependencies();

    /**
     * Gets all the dependencies retrieved from metadata parsing.
     *
     * @return the set which represents content of {@link MavenDependencyResolver} version metadata
     */
    Set<MavenDependency> getVersionManagement();
}
