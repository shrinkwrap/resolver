/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat Middleware LLC, and individual contributors
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

package org.jboss.shrinkwrap.resolver.impl.maven.integration;

import java.io.File;

import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.MavenArtifactInfo;
import org.jboss.shrinkwrap.resolver.api.maven.MavenFormatStage;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolvedArtifact;
import org.jboss.shrinkwrap.resolver.api.maven.MavenStrategyStage;
import org.jboss.shrinkwrap.resolver.api.maven.ScopeType;
import org.jboss.shrinkwrap.resolver.api.maven.filter.MavenResolutionFilter;
import org.jboss.shrinkwrap.resolver.api.maven.filter.NonTransitiveFilter;
import org.jboss.shrinkwrap.resolver.api.maven.strategy.MavenResolutionStrategy;
import org.jboss.shrinkwrap.resolver.api.maven.strategy.TransitiveExclusionPolicy;
import org.jboss.shrinkwrap.resolver.impl.maven.util.ValidationUtil;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
public class DependencyScopesTestCase {
    private static final MavenResolutionStrategy STRATEGY = ProvidedScopeNonTransitiveStrategy.INSTANCE;
    private static final String SETTINGS_XML = "target/settings/profiles/settings.xml";

    @Test
    public void wrongScopeDefined() {
        File[] jars = Maven.configureResolver().fromFile(SETTINGS_XML).loadPomFromFile("target/poms/test-wrong-scope.xml")
                .importRuntimeAndTestDependencies().resolve().withTransitivity().asFile();

        new ValidationUtil("test-deps-a").validate(jars);
    }

    @Test
    public void wrongScopeRetrieved() {
        File[] jars = Maven.configureResolver().fromFile(SETTINGS_XML)
                .resolve("org.jboss.shrinkwrap.test:test-wrong-scope:1.0.0").withTransitivity().asFile();

        new ValidationUtil("test-wrong-scope", "test-deps-a").validate(jars);
    }

    @Test
    public void resolveProvidedDependency() {
        final String coordinates = "org.jboss.xnio:xnio-api:jar:3.1.0.Beta7";

        final MavenStrategyStage mss = Maven.configureResolver().fromFile(SETTINGS_XML).resolve(coordinates);
        final MavenFormatStage mfs = mss.using(STRATEGY);
        final MavenResolvedArtifact info = mfs.asSingleResolvedArtifact();
        Assert.assertNotNull(info);
        final MavenArtifactInfo[] dependencies = info.getDependencies();
        Assert.assertNotNull(dependencies);
        // http://search.maven.org/remotecontent?filepath=org/jboss/xnio/xnio-api/3.1.0.Beta7/xnio-api-3.1.0.Beta7.pom
        // there should be org.jboss.logging:jboss-logging and org.jboss.logmanager:jboss-logmanager as provided
        Assert.assertTrue(dependencies.length == 2);
    }

    /**
     * {@link MavenResolutionStrategy} to pull in dependency information for transitive dependencies in "provided"
     * scope, yet does not include these dependencies in resolution.
     *
     * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
     */
    private enum ProvidedScopeNonTransitiveStrategy implements MavenResolutionStrategy {
        INSTANCE;

        @Override
        public TransitiveExclusionPolicy getTransitiveExclusionPolicy() {
            return new TransitiveExclusionPolicy() {

                @Override
                public ScopeType[] getFilteredScopes() {
                    // We want "provided", so just prohibit "test"
                    return new ScopeType[] { ScopeType.TEST };
                }

                @Override
                public boolean allowOptional() {
                    return false;
                }
            };
        }

        @Override
        public MavenResolutionFilter[] getResolutionFilters() {
            return new MavenResolutionFilter[] { NonTransitiveFilter.INSTANCE };
        }
    }
}
