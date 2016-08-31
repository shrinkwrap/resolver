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
package org.jboss.shrinkwrap.resolver.impl.maven.archive.assembler;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.Assignable;
import org.jboss.shrinkwrap.resolver.api.maven.ScopeType;
import org.jboss.shrinkwrap.resolver.api.maven.archive.assembler.PomEquippedArchiveMavenAssembler;
import org.jboss.shrinkwrap.resolver.api.maven.filter.AcceptAllFilter;
import org.jboss.shrinkwrap.resolver.api.maven.filter.MavenResolutionFilter;
import org.jboss.shrinkwrap.resolver.spi.maven.archive.packaging.PackagingProcessor;

/**
 * @author <a href="mailto:mjobanek@redhat.com">Matous Jobanek</a>
 */
public class PomEquippedArchiveMavenAssemblerImpl implements PomEquippedArchiveMavenAssembler {

    private final PackagingProcessor<? extends Archive<?>> processor;


    public PomEquippedArchiveMavenAssemblerImpl(PackagingProcessor<? extends Archive<?>> processor) {
        this.processor = processor;
    }

    @Override
    public PomEquippedArchiveMavenAssembler withBuildOutput() {
        return withBuildOutput(ScopeType.values());
    }

    @Override public PomEquippedArchiveMavenAssembler withBuildOutput(ScopeType... scopes) {
        processor.addBuildOutput(scopes);
        return this;
    }

    @Override public PomEquippedArchiveMavenAssembler withTestBuildOutput() {
        return withTestBuildOutput(ScopeType.values());
    }

    @Override public PomEquippedArchiveMavenAssembler withTestBuildOutput(ScopeType... scopes) {
        processor.addTestOutput(scopes);
        return this;
    }

    @Override
    public PomEquippedArchiveMavenAssembler withDependencies(MavenResolutionFilter filter, ScopeType... scopes)
        throws IllegalArgumentException {
        processor.addDependencies(filter, scopes.length == 0 ? ScopeType.values() : scopes);
        return this;
    }

    @Override public PomEquippedArchiveMavenAssembler withDependencies(MavenResolutionFilter filter) {
        return withDependencies(filter, ScopeType.values());
    }

    @Override public PomEquippedArchiveMavenAssembler withDependencies() throws IllegalArgumentException {
        return withDependencies(ScopeType.values());
    }

    @Override
    public PomEquippedArchiveMavenAssembler withDependencies(ScopeType... scopes) throws IllegalArgumentException {
        return withDependencies(AcceptAllFilter.INSTANCE, scopes);
    }

    @Override
    public <TYPE extends Assignable> TYPE as(Class<TYPE> type) {
        return processor.getResultingArchive().as(type);
    }

    @Override public <TYPE extends Assignable> TYPE as(Class<TYPE> type, String name) {
        return processor.getResultingArchive(name).as(type);
    }

}
