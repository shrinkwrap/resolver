/*
 * JBoss, Home of Professional Open Source
 * Copyright 2016, Red Hat Middleware LLC, and individual contributors
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


import org.jboss.shrinkwrap.resolver.api.ResolutionException;
import org.jboss.shrinkwrap.resolver.api.Resolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolverSystem;
import org.jboss.shrinkwrap.resolver.api.maven.MavenStrategyStage;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 *
 */
public class WorkingSessionChainingTestCase {

    @Test
    public void checkResolutionOfSingleArtifact() {

        MavenStrategyStage stage = Resolvers.use(MavenResolverSystem.class).resolve("foo:bar:2");

        Assert.assertNotNull("Resolving an artifact is possible via API", stage);
        Assert.assertEquals("Resolver contains 1 dependency to be resolved", 1, ((MavenWorkingSessionContainer) stage)
            .getMavenWorkingSession().getDependenciesForResolution().size());
    }

    @Test(expected = ResolutionException.class)
    public void checkResolutionOfSingleArtifactFailFast() {
        // there is no version
        Resolvers.use(MavenResolverSystem.class).resolve("foo:bar");
    }

}
