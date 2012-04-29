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
package org.jboss.shrinkwrap.resolver.api.archive;

import java.io.File;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.resolver.api.formatprocessor.FormatProcessor;

/**
 * {@link FormatProcessor} implementation to return an artifact as a ShrinkWrap {@link Archive}
 *
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
public final class ArchiveFormatProcessor<ARCHIVETYPE extends Archive<ARCHIVETYPE>> implements
    FormatProcessor<ARCHIVETYPE> {

    private final Class<ARCHIVETYPE> clazz;

    /**
     * Creates a new instance capable of processing the input {@link File} as the specified {@link Class} type
     *
     * @param clazz
     * @throws IllegalArgumentException
     *             If the class type is not specified
     */
    public ArchiveFormatProcessor(final Class<ARCHIVETYPE> clazz) throws IllegalArgumentException {
        if (clazz == null) {
            throw new IllegalArgumentException("clazz must be specified");
        }
        this.clazz = clazz;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.resolver.api.formatprocessor.FormatProcessor#process(java.io.File)
     */
    @Override
    public ARCHIVETYPE process(final File input) throws IllegalArgumentException {
        if (input == null) {
            throw new IllegalArgumentException("input file must be specified");
        }
        return ShrinkWrap.create(ZipImporter.class, input.getName()).as(clazz);
    }

}
