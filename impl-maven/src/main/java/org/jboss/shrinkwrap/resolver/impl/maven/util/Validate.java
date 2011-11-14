/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
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

import java.io.File;
import java.util.Collection;

/**
 * Validate
 *
 * Validation utility
 *
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @auther <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 */
public final class Validate {
    private Validate() {
    }

    /**
     * Checks that object is not null, throws exception if it is.
     *
     * @param obj The object to check
     * @param message The exception message
     * @throws IllegalArgumentException Thrown if obj is null
     */
    public static void notNull(final Object obj, final String message) throws IllegalArgumentException {
        if (obj == null) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Checks that collection is not {@code null} or empty, throws exception if it is.
     *
     * @param collection The collection to be checked
     * @param message The exception message
     * @throws IllegalArgumentException Thrown if {@code collection} is {@code null} or empty
     */
    public static void notEmpty(final Collection<?> collection, final String message) throws IllegalArgumentException {
        if (collection == null || collection.size() == 0) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Checks that the specified String is not null or empty, throws exception if it is.
     *
     * @param string The object to check
     * @param message The exception message
     * @throws IllegalArgumentException Thrown if string is null
     */
    public static void notNullOrEmpty(final String string, final String message) throws IllegalArgumentException {
        if (string == null || string.length() == 0) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Checks that the specified String is not null or empty and represents a readable file, throws exception if it is empty or
     * null and does not represent a path to a file.
     *
     * @param path The path to check
     * @param message The exception message
     * @throws IllegalArgumentException Thrown if path is empty, null or invalid
     */
    public static void isReadable(final String path, String message) throws IllegalArgumentException {
        notNullOrEmpty(path, message);
        File file = new File(path);
        if (!file.exists() || !file.canRead()) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Checks that the specified String is not null or empty and represents a writeable directory, throws exception if it is empty or
     * null and does not represent a path to a directory.
     *
     * @param path The path to check
     * @param message The exception message
     * @throws IllegalArgumentException Thrown if path is empty, null or invalid
     */
    public static void isWriteableDirectory(final String path, String message) throws IllegalArgumentException {
        notNullOrEmpty(path, message);
        File file = new File(path);
        if (!file.exists() || !file.isDirectory() || !file.canWrite() || !file.canExecute()) {
            throw new IllegalArgumentException(message);
        }

    }

    /**
     * Checks that the specified array is not null or contain any null values.
     *
     * @param objects The object to check
     * @param message The exception message
     */
    public static void notNullAndNoNullValues(final Object[] objects, final String message) {
        notNull(objects, message);
        for (Object object : objects) {
            notNull(object, message);
        }
    }
}
