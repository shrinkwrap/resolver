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
package org.jboss.shrinkwrap.resolver.impl.maven.exclusion;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.jboss.shrinkwrap.resolver.api.CoordinateBuildException;
import org.jboss.shrinkwrap.resolver.api.maven.dependency.ConfigurableDependencyDeclarationBuilder;
import org.jboss.shrinkwrap.resolver.api.maven.dependency.DependencyDeclaration;
import org.jboss.shrinkwrap.resolver.api.maven.dependency.exclusion.DependencyExclusionBuilderToConfigurableDependencyDeclarationBuilderBridge;
import org.jboss.shrinkwrap.resolver.impl.maven.MavenWorkingSession;
import org.jboss.shrinkwrap.resolver.impl.maven.dependency.ConfigurableDependencyDeclarationBuilderImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

/**
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class DependencyExclusionBuilderTestCase {

    @Mock
    MavenWorkingSession session;

    @Before
    public void initializeSession() {
        List<DependencyDeclaration> stack = new ArrayList<DependencyDeclaration>();
        Mockito.when(session.getDependencies()).thenReturn(stack);
    }

    @Test
    public void addSingleExclusion() {
        ConfigurableDependencyDeclarationBuilder depBuilder = new ConfigurableDependencyDeclarationBuilderImpl(session);
        DependencyExclusionBuilderToConfigurableDependencyDeclarationBuilderBridge exclBuilder = new DependencyExclusionBuilderToConfigurableDependencyDeclarationBuilderBridgeImpl(
            depBuilder);

        depBuilder = exclBuilder.groupId("foo").artifactId("barbar").endExclusion();
        depBuilder.groupId("foo").artifactId("bar").version("42").and();

        Assert.assertEquals("Session contains exactly 1 dependency", 1, session.getDependencies().size());
        Assert.assertEquals("Dependency in the session contains one exclusion", 1, session.getDependencies().iterator()
            .next().getExclusions().size());
    }

    @Test
    public void addThreeExclusions() {
        ConfigurableDependencyDeclarationBuilder depBuilder = new ConfigurableDependencyDeclarationBuilderImpl(session);
        DependencyExclusionBuilderToConfigurableDependencyDeclarationBuilderBridge exclBuilder = new DependencyExclusionBuilderToConfigurableDependencyDeclarationBuilderBridgeImpl(
            depBuilder);

        depBuilder = exclBuilder.groupId("foo").artifactId("barbar").endExclusion();
        depBuilder.groupId("foo").artifactId("bar").version("42").addExclusion().groupId("foo").artifactId("barbarbar")
            .endExclusion().addExclusion("foo:foobar").and();

        Assert.assertEquals("Session contains exactly 1 dependency", 1, session.getDependencies().size());
        Assert.assertEquals("Dependency in the session contains three exclusions", 3, session.getDependencies()
            .iterator().next().getExclusions().size());
    }

    @Test
    public void addFourExclusions() {
        ConfigurableDependencyDeclarationBuilder depBuilder = new ConfigurableDependencyDeclarationBuilderImpl(session);
        DependencyExclusionBuilderToConfigurableDependencyDeclarationBuilderBridge exclBuilder = new DependencyExclusionBuilderToConfigurableDependencyDeclarationBuilderBridgeImpl(
            depBuilder);

        depBuilder = exclBuilder.groupId("foo").artifactId("barbar").endExclusion();
        depBuilder.groupId("foo").artifactId("bar").version("42").addExclusion().groupId("foo").artifactId("barbarbar")
            .endExclusion().addExclusions("foo:foobar", "foo:barfoo").and();

        Assert.assertEquals("Session contains exactly 1 dependency", 1, session.getDependencies().size());
        Assert.assertEquals("Dependency in the session contains four exclusions", 4, session.getDependencies()
            .iterator().next().getExclusions().size());
    }

    @Test(expected = CoordinateBuildException.class)
    public void addExclusionMissingG() {
        ConfigurableDependencyDeclarationBuilder depBuilder = new ConfigurableDependencyDeclarationBuilderImpl(session);
        DependencyExclusionBuilderToConfigurableDependencyDeclarationBuilderBridge exclBuilder = new DependencyExclusionBuilderToConfigurableDependencyDeclarationBuilderBridgeImpl(
            depBuilder);

        depBuilder = exclBuilder.artifactId("barbar").endExclusion();
    }

    @Test(expected = CoordinateBuildException.class)
    public void addExclusionMissingA() {
        ConfigurableDependencyDeclarationBuilder depBuilder = new ConfigurableDependencyDeclarationBuilderImpl(session);
        DependencyExclusionBuilderToConfigurableDependencyDeclarationBuilderBridge exclBuilder = new DependencyExclusionBuilderToConfigurableDependencyDeclarationBuilderBridgeImpl(
            depBuilder);

        depBuilder = exclBuilder.groupId("barbar").endExclusion();
    }

}
