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
package org.jboss.shrinkwrap.resolver.impl.maven.archive.builder;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.Assignable;
import org.jboss.shrinkwrap.resolver.api.maven.ScopeType;
import org.jboss.shrinkwrap.resolver.api.maven.archive.builder.PomEquippedMavenBuilder;
import org.jboss.shrinkwrap.resolver.api.maven.strategy.AcceptScopesStrategy;
import org.jboss.shrinkwrap.resolver.api.maven.strategy.MavenResolutionStrategy;
import org.jboss.shrinkwrap.resolver.spi.maven.archive.packaging.PackagingProcessor;

public class PomEquippedMavenBuilderImpl implements PomEquippedMavenBuilder {

    private final PackagingProcessor<? extends Archive<?>> processor;

    public PomEquippedMavenBuilderImpl(PackagingProcessor<? extends Archive<?>> processor) {
        this.processor = processor;
    }

    @Override
    public <TYPE extends Assignable> TYPE as(Class<TYPE> type) {
        return processor.getResultingArchive().as(type);
    }

    @Override
    public PomEquippedMavenBuilder importBuildOutput() {
        MavenResolutionStrategy strategy = new AcceptScopesStrategy(ScopeType.COMPILE, ScopeType.IMPORT, ScopeType.RUNTIME,
                ScopeType.SYSTEM);
        return importBuildOutput(strategy);
    }

    @Override
    public PomEquippedMavenBuilder importBuildOutput(MavenResolutionStrategy strategy) throws IllegalArgumentException {
        processor.importBuildOutput(strategy);
        return this;
    }

}
