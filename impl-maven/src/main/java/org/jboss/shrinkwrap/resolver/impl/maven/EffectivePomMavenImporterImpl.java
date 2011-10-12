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

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import org.apache.maven.model.Model;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.Assignable;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependency;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;
import org.jboss.shrinkwrap.resolver.api.maven.MavenImporter;
import org.jboss.shrinkwrap.resolver.api.maven.MavenImporter.EffectivePomMavenImporter;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolutionFilter;
import org.jboss.shrinkwrap.resolver.api.maven.filter.ScopeFilter;
import org.sonatype.aether.RepositorySystemSession;

/**
 * Implementation of EffectivePomMavenImporter. This class is hidden to be instantiated by user, and it contains all the
 * functionality required to handle Maven packages.
 *
 * @author <a href="kpiwko@redhat.com>Karel Piwko</a>
 * @see MavenPackagingType
 */
class EffectivePomMavenImporterImpl implements MavenImporter.EffectivePomMavenImporter {

    private Archive<?> archive;

    private final MavenPackagingType mpt;
    private final Model model;
    private Stack<MavenDependency> dependencies;
    private final MavenRepositorySystem system;
    private final MavenDependencyResolverSettings settings;

    private RepositorySystemSession session;

    /**
     * Creates a EffectivePomMavenImporter based on information from POM model
     *
     * @param archive The archive to be modified
     * @param mpt The type of Maven packaging
     * @param model The Maven model
     * @param system The Maven-Aether system
     * @param settings The Maven-Aether-Resolver settings
     * @param session The repository session to be reused
     */
    public EffectivePomMavenImporterImpl(Archive<?> archive, MavenPackagingType mpt, Model model, MavenRepositorySystem system,
            MavenDependencyResolverSettings settings, RepositorySystemSession session) {

        this.archive = archive;
        this.mpt = mpt;
        this.model = model;
        this.system = system;
        this.settings = settings;
        this.session = session;

        // cache parsed model dependencies
        this.dependencies = MavenConverter.fromDependencies(model.getDependencies(), system.getArtifactTypeRegistry(session));

    }

    @Override
    public <TYPE extends Assignable> TYPE as(Class<TYPE> archiveType) {
        return archive.as(archiveType);
    }

    @Override
    public EffectivePomMavenImporter importBuildOutput() {
        this.archive = mpt.enrichArchiveWithBuildOutput(archive, model);
        return this;
    }

    @Override
    public EffectivePomMavenImporter importTestBuildOutput() {
        this.archive = mpt.enrichArchiveWithTestOutput(archive, model);
        return this;
    }

    @Override
    public EffectivePomMavenImporter importTestDependencies() {
        return importAnyDependencies(new ScopeFilter("test"));
    }

    @Override
    public EffectivePomMavenImporter importAnyDependencies(MavenResolutionFilter filter) {

        MavenDependencyResolver resolver = getMavenDependencyResolver(dependencies, new HashSet<MavenDependency>());

        this.archive = mpt.enrichArchiveWithTestArtifacts(archive, resolver, filter);
        return this;
    }

    private MavenDependencyResolver getMavenDependencyResolver(Stack<MavenDependency> dependencies,
            Set<MavenDependency> versionManagement) {

        return new MavenBuilderImpl(system, session, settings, dependencies, versionManagement);
    }

}
