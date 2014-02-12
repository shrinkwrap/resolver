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

import org.jboss.shrinkwrap.resolver.api.ResolutionStrategy;
import org.jboss.shrinkwrap.resolver.api.TransitiveStrategyStage;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependency;
import org.jboss.shrinkwrap.resolver.api.maven.filter.MavenResolutionFilter;
import org.jboss.shrinkwrap.resolver.api.maven.strategy.MavenResolutionStrategy;

/**
 * Provides support for Maven-based {@link ResolutionStrategy}s in artifact resolution
 *
 * @param <FORMATSTAGETYPE>
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 */
public interface MavenStrategyStageBase<STRATEGYSTAGETYPE extends MavenStrategyStageBase<STRATEGYSTAGETYPE, FORMATSTAGETYPE>, FORMATSTAGETYPE extends MavenFormatStage>
    extends TransitiveStrategyStage<MavenDependency, MavenResolutionFilter, MavenResolvedArtifact, FORMATSTAGETYPE, MavenResolutionStrategy> {

    /**
     * Sets that resolution from the ClassPath should be permitted in addition to configured repositories - defaults to
     * "true"
     *
     * @param useClassPathResolution
     * @return
     */
    STRATEGYSTAGETYPE withClassPathResolution(boolean useClassPathResolution);

    /**
     * Sets whether to consult the Maven Central Repository in resolution; defaults to true.
     *
     * @param useMavenCentral
     * @return
     */
    STRATEGYSTAGETYPE withMavenCentralRepo(boolean useMavenCentral);

    /**
     * Add a remote repository to use in resolution.
     *
     * @param name a unique arbitrary ID such as "codehaus"
     * @param url the repository URL, such as "http://snapshots.maven.codehaus.org/maven2"
     * @param layout the repository layout. Most of the time "default"
     */
    STRATEGYSTAGETYPE withMavenRemoteRepo(String name, String url, String layout);
}
