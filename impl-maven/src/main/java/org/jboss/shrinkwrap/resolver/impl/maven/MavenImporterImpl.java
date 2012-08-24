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
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;
import org.jboss.shrinkwrap.resolver.api.maven.MavenImporter;

/**
 * Basic implementation of MavenImporter. It is able to load settings.xml (optional) and a effective pom.
 *
 * @author <a href="kpiwko@redhat.com>Karel Piwko</a>
 *
 */
public class MavenImporterImpl implements MavenImporter {

    private Archive<?> archive;

    private MavenDependencyResolver delegate;

    /**
     * Constructs a MavenImporter based on underlying archive
     *
     * @param archive
     *            the content to be enriched by importer
     */
    public MavenImporterImpl(Archive<?> archive) {
        this.archive = archive;
    }

    @Override
    public <TYPE extends Assignable> TYPE as(Class<TYPE> archiveType) {
        return archive.as(archiveType);
    }

    @Override
    public MavenImporter loadSettings(String userSettings) {
        this.delegate = new MavenDependencyResolverImpl().loadSettings(userSettings);
        return this;
    }

    @Override
    public EffectivePomMavenImporter loadEffectivePom(String path, String... profiles) {
        if (delegate == null) {
            this.delegate = new MavenDependencyResolverImpl();
        }

        EffectivePomMavenDependencyResolver epmdr = delegate.loadEffectivePom(path, profiles);
        return new EffectivePomMavenImporterImpl(archive, epmdr);
    }
}
