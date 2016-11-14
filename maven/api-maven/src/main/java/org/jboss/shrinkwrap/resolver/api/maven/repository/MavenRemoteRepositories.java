/*
 * JBoss, Home of Professional Open Source
 * Copyright 2016, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.shrinkwrap.resolver.api.maven.repository;

import java.net.MalformedURLException;
import java.net.URL;

public final class MavenRemoteRepositories {

    /**
     * No instances
     */
    private MavenRemoteRepositories() {
        throw new UnsupportedOperationException("No instances permitted");
    }

    /**
     * Creates a new <code>MavenRemoteRepository</code> with ID and URL. Please note that the repository layout should always be set to default.
     *
     * @param id The unique ID of the repository to create (arbitrary name)
     * @param url The base URL of the Maven repository
     * @param layout he repository layout. Should always be "default"
     * @return A new <code>MavenRemoteRepository</code> with the given ID and URL.
     * @throws IllegalArgumentException for null or empty id
     * @throws RuntimeException if an error occurred during <code>MavenRemoteRepository</code> instance creation
     */
    public static MavenRemoteRepository createRemoteRepository(final String id, final URL url, final String layout) {
        // Argument tests are inside the impl constructor
        return new MavenRemoteRepositoryImpl(id, url, layout);
    }

    /**
     * Overload of {@link #createRemoteRepository(String, URL, String)} that thrown an exception if URL is wrong.
     *
     * @param id The unique ID of the repository to create (arbitrary name)
     * @param url The base URL of the Maven repository
     * @param layout he repository layout. Should always be "default"
     * @return A new <code>MavenRemoteRepository</code> with the given ID and URL.
     * @throws IllegalArgumentException for null or empty id or if the URL is technically wrong or null
     */
    public static MavenRemoteRepository createRemoteRepository(final String id, final String url, final String layout)
            throws IllegalArgumentException {
        try {
            return createRemoteRepository(id, new URL(url), layout);
        }
        catch (MalformedURLException e) {
            throw new IllegalArgumentException("invalid URL", e);
        }
    }
}
