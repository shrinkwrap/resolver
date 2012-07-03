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
package org.jboss.shrinkwrap.resolver.impl.maven.exclusion;

/**
 * Basic validation utility
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 *
 */
class Validate {

    /**
     * Checks that the specified String is not null or empty.
     *
     * @param string The object to check
     * @param message The exception message
     * @return {@code true} if specified String is null or empty, {@code false} otherwise
     */
    static boolean isNullOrEmpty(final String string) {
        if (string == null || string.length() == 0) {
            return true;
        }
        return false;
    }

    /**
     * Checks that the specified String is not null or empty, throws exception if it is.
     *
     * @param string The object to check
     * @param message The exception message
     * @throws IllegalArgumentException Thrown if string is null
     */
    static void notNullOrEmpty(final String string, final String message) throws IllegalArgumentException {
        if (isNullOrEmpty(string)) {
            throw new IllegalArgumentException(message);
        }
    }
}
