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

/**
 * {@link FormatProcessor} implementation to return a {@link File} as-is, assuming it exists and does not point to a
 * directory.
 *
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
public enum FileFormatProcessor implements FormatProcessor<File> {
    INSTANCE;

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.resolver.api.formatprocessor.FormatProcessor#process(java.io.File)
     */
    @Override
    public File process(final File input) throws IllegalArgumentException {
        if (input == null) {
            throw new IllegalArgumentException("input file must be specified");
        }
        if (!input.exists()) {
            throw new IllegalArgumentException("input file does not exist: " + input.getAbsolutePath());
        }
        if (input.isDirectory()) {
            throw new IllegalArgumentException("input file is a directory: " + input.getAbsolutePath());
        }
        return input;
    }

}
