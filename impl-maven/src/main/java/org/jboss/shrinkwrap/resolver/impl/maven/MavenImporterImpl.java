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

import java.io.File;

import org.apache.maven.model.Model;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.Assignable;
import org.jboss.shrinkwrap.resolver.api.maven.MavenImporter;
import org.jboss.shrinkwrap.resolver.impl.maven.util.ResourceUtil;
import org.jboss.shrinkwrap.resolver.impl.maven.util.Validate;
import org.sonatype.aether.RepositorySystemSession;

/**
 * Basic implementation of MavenImporter. It is able to load settings.xml (optional) and a effective pom.
 *
 * @author <a href="kpiwko@redhat.com>Karel Piwko</a>
 *
 */
public class MavenImporterImpl implements MavenImporter {

    private Archive<?> archive;

    private final MavenRepositorySystem system;
    private final MavenDependencyResolverSettings settings;

    private RepositorySystemSession session;

    /**
     * Constructs a MavenImporter based on underlying archive
     *
     * @param archive the content to be enriched by importer
     */
    public MavenImporterImpl(Archive<?> archive) {
        this.archive = archive;

        this.system = new MavenRepositorySystem();
        this.settings = new MavenDependencyResolverSettings();

        // get session to spare time
        this.session = system.getSession(settings);
    }

    @Override
    public <TYPE extends Assignable> TYPE as(Class<TYPE> archiveType) {
        return archive.as(archiveType);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.shrinkwrap.resolver.api.maven.MavenImporter#configureFrom(java.lang.String)
     */
    @Override
    public MavenImporter configureFrom(String path) {
        Validate.notNullOrEmpty(path, "Path to a settings.xml file must be specified");
        String resolvedPath = ResourceUtil.resolvePathByQualifier(path);
        Validate.isReadable(resolvedPath, "Path to the settings.xml ('" + path + "') must be defined and accessible");
        system.loadSettings(new File(resolvedPath), settings);
        // regenerate session
        this.session = system.getSession(settings);
        return this;
    }

    @Override
    public EffectivePomMavenImporter loadEffectivePom(String path, String... profiles) {
        Validate.notNullOrEmpty(path, "Path to a POM file must be specified");
        String resolvedPath = ResourceUtil.resolvePathByQualifier(path);
        Validate.isReadable(resolvedPath, "Path to the pom.xml ('" + path + "')file must be defined and accessible");

        File pom = new File(resolvedPath);
        Model model = system.loadPom(pom, settings, session);

        MavenPackagingType mpt = MavenPackagingType.from(model.getPackaging());

        return new EffectivePomMavenImporterImpl(archive, mpt, model, system, settings, session);
    }

}
