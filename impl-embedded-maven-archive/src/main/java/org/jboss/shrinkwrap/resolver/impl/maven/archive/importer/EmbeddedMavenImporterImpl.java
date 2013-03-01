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
package org.jboss.shrinkwrap.resolver.impl.maven.archive.importer;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.Assignable;
import org.jboss.shrinkwrap.resolver.api.maven.archive.importer.EmbeddedMavenImporter;
import org.jboss.shrinkwrap.resolver.api.maven.archive.importer.PomEquippedEmbeddedMavenImporter;

public class EmbeddedMavenImporterImpl implements EmbeddedMavenImporter {

    private Archive<?> archive;

    public EmbeddedMavenImporterImpl(Archive<?> archive) {
        this.archive = archive;
    }

    @Override
    public PomEquippedEmbeddedMavenImporter loadPomFromFile(String pomFilePath) {
        return new PomEquippedEmbeddedMavenImporterImpl(archive, pomFilePath);
    }


    @Override
    public <TYPE extends Assignable> TYPE as(Class<TYPE> clazz) {
        throw new UnsupportedOperationException(
                "There were no data imported yet. Please load a pom file first using any of the loadPomFrom*() methods.");
    }
}
