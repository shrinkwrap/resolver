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
package org.jboss.shrinkwrap.resolver.impl.maven.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Generic input/output utilities
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @version $Revision: $
 */
public final class IOUtil {

    // -------------------------------------------------------------------------------------||
    // Class Members ----------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Logger
     */
    private static final Logger log = Logger.getLogger(IOUtil.class.getName());

    // -------------------------------------------------------------------------------------||
    // Constructor ------------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Internal constructor; should not be called
     */
    private IOUtil() {
        throw new UnsupportedOperationException("No instances should be created; stateless class");
    }

    // -------------------------------------------------------------------------------------||
    // Required Implementations ------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Copies the contents from an InputStream to an OutputStream. It is the responsibility of the caller to close the
     * streams passed in when done, though the {@link OutputStream} will be fully flushed.
     *
     * @param input The {@link InputStream}
     * @param output The {@link OutputStream}
     * @throws IOException
     *             If a problem occurred during any I/O operations
     */
    public static void copy(final InputStream input, final OutputStream output) throws IOException {
        final byte[] buffer = new byte[4096];
        int read = 0;
        while ((read = input.read(buffer)) != -1) {
            output.write(buffer, 0, read);
        }

        output.flush();
    }

    /**
     * Copies the contents from an InputStream to an OutputStream and closes both streams.
     *
     * @param input The {@link InputStream}
     * @param output The {@link OutputStream}
     * @throws IOException
     *             If a problem occurred during any I/O operations during the copy, but on closing the streams these
     *             will be ignored and logged at {@link Level#FINER}
     */
    public static void copyWithClose(InputStream input, OutputStream output) throws IOException {
        try {
            copy(input, output);
        } finally {
            try {
                input.close();
            } catch (final IOException ignore) {
                if (log.isLoggable(Level.FINER)) {
                    log.finer("Could not close stream due to: " + ignore.getMessage() + "; ignoring");
                }
            }
            try {
                output.close();
            } catch (final IOException ignore) {
                if (log.isLoggable(Level.FINER)) {
                    log.finer("Could not close stream due to: " + ignore.getMessage() + "; ignoring");
                }
            }
        }
    }

}