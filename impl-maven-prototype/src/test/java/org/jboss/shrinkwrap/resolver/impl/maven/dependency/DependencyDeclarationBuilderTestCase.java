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
package org.jboss.shrinkwrap.resolver.impl.maven.dependency;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.jboss.shrinkwrap.resolver.api.CoordinateBuildException;
import org.jboss.shrinkwrap.resolver.api.maven.PackagingType;
import org.jboss.shrinkwrap.resolver.api.maven.ScopeType;
import org.jboss.shrinkwrap.resolver.api.maven.dependency.ConfigurableDependencyDeclarationBuilder;
import org.jboss.shrinkwrap.resolver.api.maven.dependency.ConfiguredDependencyDeclarationBuilder;
import org.jboss.shrinkwrap.resolver.api.maven.dependency.DependencyDeclaration;
import org.jboss.shrinkwrap.resolver.api.maven.dependency.exclusion.DependencyExclusion;
import org.jboss.shrinkwrap.resolver.impl.maven.MavenWorkingSession;
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
public class DependencyDeclarationBuilderTestCase {

    @Mock
    MavenWorkingSession session;

    @Mock
    MavenWorkingSession sessionWithDepMngmt;

    @Before
    public void initializeSession() {
        List<DependencyDeclaration> stack = new ArrayList<DependencyDeclaration>();
        Set<DependencyDeclaration> set = new LinkedHashSet<DependencyDeclaration>();
        set.add(new DependencyDeclarationImpl("foo", "bar", PackagingType.JAR, "", "2", ScopeType.TEST, false,
            Collections.<DependencyExclusion> emptySet()));

        Mockito.when(session.getDependencies()).thenReturn(stack);
        Mockito.when(sessionWithDepMngmt.getDependencies()).thenReturn(stack);
        Mockito.when(sessionWithDepMngmt.getDependencyManagement()).thenReturn(set);
    }

    @Test
    public void addSingleDependencyOnConfigured() {
        ConfiguredDependencyDeclarationBuilder builder = new ConfiguredDependencyDeclarationBuilderImpl(
            sessionWithDepMngmt);
        builder.groupId("foo").artifactId("bar").version("1").and();
        Assert.assertEquals("Exactly one dependency is in session", 1, session.getDependencies().size());
        Assert.assertEquals("Version of the dependency is still 1", "1", session.getDependencies().iterator().next()
            .getVersion());
    }

    @Test
    public void addSingleDependency() {
        ConfigurableDependencyDeclarationBuilder builder = new ConfigurableDependencyDeclarationBuilderImpl(session);
        builder.groupId("foo").artifactId("bar").version("1").and();
        Assert.assertEquals("Exactly one dependency is in session", 1, session.getDependencies().size());
    }

    @Test
    public void addTwoDependencies() {
        ConfigurableDependencyDeclarationBuilder builder = new ConfigurableDependencyDeclarationBuilderImpl(session);
        builder.groupId("foo").artifactId("bar").version("1").and().groupId("foo").artifactId("barbar").version("1")
            .and();
        Assert.assertEquals("Exactly two dependencies in session", 2, session.getDependencies().size());
    }

    @Test
    public void addTwoDependenciesShortcut() {
        ConfigurableDependencyDeclarationBuilder builder = new ConfigurableDependencyDeclarationBuilderImpl(session);
        builder.groupId("foo").artifactId("bar").version("1").and("foo:barbar:1").and();
        Assert.assertEquals("Exactly two dependencies in session", 2, session.getDependencies().size());
    }

    @Test
    public void oneDependencyIsStillIntermediate() {
        ConfigurableDependencyDeclarationBuilder builder = new ConfigurableDependencyDeclarationBuilderImpl(session);
        // only first dependency will be included, we haven't pushed anything via and(...) nor resolve()
        builder.groupId("foo").artifactId("bar").version("1").and("foo:barbar:1");
        Assert.assertEquals("Exactly one dependency in session", 1, session.getDependencies().size());
    }

    @Test
    public void dependencyEqualityOnGA() {
        ConfigurableDependencyDeclarationBuilder builder = new ConfigurableDependencyDeclarationBuilderImpl(session);
        builder.groupId("foo").artifactId("bar").version("1").and("foo:bar:2").and();
        Assert.assertEquals("Exactly one dependency in session", 2, session.getDependencies().size());
        final Iterator<DependencyDeclaration> it = session.getDependencies().iterator();
        Assert.assertTrue("Both dependencies are the same", it.next().equals(it.next()));
    }

    @Test
    public void dependencyEqualityOnGAP() {
        ConfigurableDependencyDeclarationBuilder builder = new ConfigurableDependencyDeclarationBuilderImpl(session);
        builder.groupId("foo").artifactId("bar").packaging(PackagingType.EJB).version("1").and("foo:bar:2").and();
        Assert.assertEquals("Exactly one dependency in session", 2, session.getDependencies().size());
        final Iterator<DependencyDeclaration> it = session.getDependencies().iterator();
        Assert.assertFalse("Both dependencies are not the same", it.next().equals(it.next()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void addSingleDependencyWithoutArtifactId() {
        ConfigurableDependencyDeclarationBuilder builder = new ConfigurableDependencyDeclarationBuilderImpl(session);
        builder.groupId("foo").version("42").and();
    }

    @Test(expected = IllegalArgumentException.class)
    public void addSingleDependencyWithoutGroupId() {
        ConfigurableDependencyDeclarationBuilder builder = new ConfigurableDependencyDeclarationBuilderImpl(session);
        builder.artifactId("foo").version("42").and();
    }

    @Test(expected = CoordinateBuildException.class)
    public void addSingleDependencyWithoutVersion() {
        ConfigurableDependencyDeclarationBuilder builder = new ConfigurableDependencyDeclarationBuilderImpl(session);
        builder.groupId("foo").artifactId("bar").and();
        // we have not set a version and there is no dependendency management set, so it will fail due to missing
        // version string
    }

    @Test
    public void addSingleDependencyWithoutVersionOnConfigured() {
        ConfiguredDependencyDeclarationBuilder builder = new ConfiguredDependencyDeclarationBuilderImpl(
            sessionWithDepMngmt);
        builder.groupId("foo").artifactId("bar").and();
        Assert.assertEquals("Exactly one dependency in session", 1, session.getDependencies().size());
        Assert.assertEquals("Version of the dependency is inferred to 2", "2", session.getDependencies().iterator()
            .next().getVersion());
    }
}
