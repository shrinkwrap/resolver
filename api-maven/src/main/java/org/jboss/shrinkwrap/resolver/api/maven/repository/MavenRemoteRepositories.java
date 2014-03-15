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
     * @param id the unique ID of the repository to create (arbitrary name)
     * @param url the base URL of the Maven repository
     * @return
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
     * @param id
     * @param url
     * @param layout
     * @return
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
