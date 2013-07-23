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
package org.jboss.shrinkwrap.resolver.api.maven;

/**
 * Defines the contract for operations denoting a {@link ResolverStage} has been configured via POM (Project Object
 * Model) metadata
 *
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 */
public interface PomEquippedResolveStageBase<RESOLVESTAGETYPE extends MavenResolveStageBase<RESOLVESTAGETYPE, STRATEGYSTAGETYPE, FORMATSTAGETYPE>, STRATEGYSTAGETYPE extends MavenStrategyStageBase<STRATEGYSTAGETYPE, FORMATSTAGETYPE>, FORMATSTAGETYPE extends MavenFormatStage>
    extends MavenResolveStageBase<RESOLVESTAGETYPE, STRATEGYSTAGETYPE, FORMATSTAGETYPE>, MavenResolveWithRangeSupportStageBase {
    /**
     * Adds all dependencies defined in imported POM file included in selected scopes for resolution
     *
     * @param scopes Scopes
     * @return Modified instance to allow chaining
     * @throws IllegalArgumentException If no scopes were provided
     */
    RESOLVESTAGETYPE importDependencies(ScopeType... scopes) throws IllegalArgumentException;

    /**
     * Adds all dependencies defined in imported POM file included in test scope for resolution
     *
     * @return Modified instance to allow chaining
     */
    RESOLVESTAGETYPE importTestDependencies();

    /**
     * Adds all dependencies defined in imported POM file included in test, compile(default), system, and import scopes for resolution
     *
     * @return Modified instance to allow chaining
     */
    RESOLVESTAGETYPE importRuntimeAndTestDependencies();

    /**
     * Adds all dependencies defined in imported POM file included in compile(default), system, import and runtime scopes for
     * resolution
     *
     * @return Modified instance to allow chaining
     */
    RESOLVESTAGETYPE importRuntimeDependencies();

    /**
     * More explicit alias equivalent to {@link org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStageBase#importRuntimeDependencies()}
     *
     * @return Modified instance to allow chaining
     */
    RESOLVESTAGETYPE importCompileAndRuntimeDependencies();
}
