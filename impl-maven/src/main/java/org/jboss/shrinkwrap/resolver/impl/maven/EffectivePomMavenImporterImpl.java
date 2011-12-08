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

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.Assignable;
import org.jboss.shrinkwrap.resolver.api.maven.EffectivePomMavenDependencyResolver;
import org.jboss.shrinkwrap.resolver.api.maven.MavenImporter;
import org.jboss.shrinkwrap.resolver.api.maven.MavenImporter.EffectivePomMavenImporter;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolutionFilter;
import org.jboss.shrinkwrap.resolver.api.maven.filter.CombinedFilter;
import org.jboss.shrinkwrap.resolver.api.maven.filter.ScopeFilter;

/**
 * Implementation of EffectivePomMavenImporter. This class is hidden to be instantiated by user, and it contains all the
 * functionality required to handle Maven packages.
 *
 * @author <a href="kpiwko@redhat.com>Karel Piwko</a>
 * @see MavenPackagingType
 */
class EffectivePomMavenImporterImpl implements MavenImporter.EffectivePomMavenImporter, MavenEnvironmentRetrieval {

    private Archive<?> archive;

    private EffectivePomMavenDependencyResolver effectivePomResolver;

    private final MavenPackagingType mpt;

    /**
     * Creates a EffectivePomMavenImporter based on information from POM model
     *
     * @param archive The archive to be modified
     * @param effectivePomResolver Effective pom in resolved state
     */
    public EffectivePomMavenImporterImpl(Archive<?> archive, EffectivePomMavenDependencyResolver effectivePomResolver) {

        this.archive = archive;
        this.effectivePomResolver = effectivePomResolver;
        this.mpt = MavenPackagingType.from(getMavenEnvironment().getModel().getPackaging());
    }

    @Override
    public <TYPE extends Assignable> TYPE as(Class<TYPE> archiveType) {
        return archive.as(archiveType);
    }

    @Override
    public EffectivePomMavenImporter importBuildOutput() {
        this.archive = mpt.enrichArchiveWithBuildOutput(archive, getMavenEnvironment().getModel());
        return this;
    }

    @Override
    public EffectivePomMavenImporter importTestBuildOutput() {
        this.archive = mpt.enrichArchiveWithTestOutput(archive, getMavenEnvironment().getModel());
        return this;
    }

    @Override
    public EffectivePomMavenImporter importTestDependencies() {
        return importAnyDependencies(new ScopeFilter("test"));
    }

    @Override
    public EffectivePomMavenImporter importTestDependencies(final MavenResolutionFilter filter) {
        Validate.notNull(filter, "A filter must be supplied");

        return this.importAnyDependencies(new CombinedFilter(new ScopeFilter("test"), filter));
    }

    @Override
    public EffectivePomMavenImporter importAnyDependencies(MavenResolutionFilter filter) {
        Validate.notNull(filter, "A filter must be supplied");

        this.effectivePomResolver = effectivePomResolver.importAnyDependencies(filter);
        this.archive = mpt.enrichArchiveWithTestArtifacts(archive, effectivePomResolver, filter);
        return this;
    }

    @Override
    public MavenEnvironment getMavenEnvironment() {
        if (!(effectivePomResolver instanceof MavenEnvironmentRetrieval)) {
            throw new UnsupportedOperationException(
                    "Incompatible instance of EffectivePomDependencyResolver, unable to get MavenEnvironment object");
        }

        return ((MavenEnvironmentRetrieval) effectivePomResolver).getMavenEnvironment();
    }
}