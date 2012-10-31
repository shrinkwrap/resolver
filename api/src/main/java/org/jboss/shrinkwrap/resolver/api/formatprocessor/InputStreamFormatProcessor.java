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
package org.jboss.shrinkwrap.resolver.api.formatprocessor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.jboss.shrinkwrap.resolver.api.ResolvedArtifact;

/**
 * {@link FormatProcessor} implementation to return an {@link InputStream} from the provided {@link ResolvedArtifact} argument.
 *
 * Implementation note: This format processor does not use type parameters to be able to process any type inherited from
 * {@link ResolvedAritifact}.
 *
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
@SuppressWarnings("rawtypes")
public enum InputStreamFormatProcessor implements FormatProcessor {
    INSTANCE;

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.resolver.api.formatprocessor.FormatProcessor#process(File, Class)
     */
    @Override
    public InputStream process(final ResolvedArtifact artifact, final Class returnType)
            throws IllegalArgumentException {
        if (returnType.getClass() == null || InputStream.class.equals(returnType.getClass())) {
            throw new IllegalArgumentException("InputStream processor must be called to return InputStream, not "
                    + (returnType == null ? "null" : returnType.getClass()));
        }
        if (artifact == null) {
            throw new IllegalArgumentException("Resolution artifact must be specified");
        }
        File file = artifact.asFile();
        if (file == null) {
            throw new IllegalArgumentException("Artifact was not resolved");
        }

        if (!file.exists()) {
            throw new IllegalArgumentException("input file does not exist: " + file.getAbsolutePath());
        }
        if (file.isDirectory()) {
            throw new IllegalArgumentException("input file is a directory: " + file.getAbsolutePath());
        }

        try {
            // Return
            return new FileInputStream(file);
        } catch (final FileNotFoundException fnfe) {
            // Wrap to make the compiler happy, even though we have the precondition checks above
            throw new IllegalArgumentException(fnfe);
        }
    }

    @Override
    public boolean handles(Class resolvedTypeClass) {
        return ResolvedArtifact.class.isAssignableFrom(resolvedTypeClass);
    }

    @Override
    public boolean returns(Class returnTypeClass) {
        return InputStream.class.equals(returnTypeClass);
    }

}
