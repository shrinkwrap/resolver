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
package org.jboss.shrinkwrap.resolver.impl.maven.archive;

import java.util.Collection;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.resolver.api.NoResolvedResultException;
import org.jboss.shrinkwrap.resolver.api.NonUniqueResultException;
import org.jboss.shrinkwrap.resolver.api.maven.archive.ArchiveFormatProcessor;
import org.jboss.shrinkwrap.resolver.api.maven.archive.MavenArchiveFormatStage;
import org.jboss.shrinkwrap.resolver.impl.maven.MavenFormatStageImpl;
import org.sonatype.aether.artifact.Artifact;

/**
 * Implementation of {@link MavenArchiveFormatStage}
 *
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
class MavenArchiveFormatStageImpl extends MavenFormatStageImpl implements MavenArchiveFormatStage {

    MavenArchiveFormatStageImpl(final Collection<Artifact> artifacts) {
        super(artifacts);
    }

    @Override
    public <ARCHIVETYPE extends Archive<ARCHIVETYPE>> ARCHIVETYPE[] as(final Class<ARCHIVETYPE> type)
        throws IllegalArgumentException {
        return this.as(type, new ArchiveFormatProcessor<ARCHIVETYPE>(type));
    }

    @Override
    public <ARCHIVETYPE extends Archive<ARCHIVETYPE>> ARCHIVETYPE asSingle(final Class<ARCHIVETYPE> type)
        throws IllegalArgumentException, NonUniqueResultException, NoResolvedResultException {
        return this.asSingle(type, new ArchiveFormatProcessor<ARCHIVETYPE>(type));
    }
}
