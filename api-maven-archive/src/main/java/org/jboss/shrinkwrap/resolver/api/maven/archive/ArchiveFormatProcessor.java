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
package org.jboss.shrinkwrap.resolver.api.maven.archive;

import java.io.File;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.resolver.api.formatprocessor.FormatProcessor;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolvedArtifact;

/**
 * {@link FormatProcessor} implementation to return an artifact as a ShrinkWrap {@link Archive}
 *
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 */
public final class ArchiveFormatProcessor<ARCHIVETYPE extends Archive<ARCHIVETYPE>> implements
        FormatProcessor<MavenResolvedArtifact, ARCHIVETYPE> {

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.resolver.api.formatprocessor.FormatProcessor#process(java.io.File)
     */
    @Override
    public ARCHIVETYPE process(final MavenResolvedArtifact artifact, final Class<ARCHIVETYPE> returnType)
            throws IllegalArgumentException {

        if (artifact == null) {
            throw new IllegalArgumentException("Resolution artifact must be specified");
        }
        File file = artifact.asFile();
        if (file == null) {
            throw new IllegalArgumentException("Artifact was not resolved");
        }

        return ShrinkWrap.create(ZipImporter.class, file.getName()).importFrom(file).as(returnType);
    }

    @Override
    public boolean handles(Class<?> resolvedTypeClass) {
        return MavenResolvedArtifact.class.isAssignableFrom(resolvedTypeClass);
    }

    @Override
    public boolean returns(Class<?> returnTypeClass) {
        return Archive.class.isAssignableFrom(returnTypeClass);
    }

}
