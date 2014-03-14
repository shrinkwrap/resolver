package org.jboss.shrinkwrap.resolver.api.maven.repository;

import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;

public final class MavenRemoteRepositories {
    private static final String NAME_IMPL_CLASS = "org.jboss.shrinkwrap.resolver.impl.maven.repository.MavenRemoteRepositoryImpl";
    private static final Constructor<MavenRemoteRepository> ctor;

    static {
        try {
            @SuppressWarnings("unchecked")
            final Class<MavenRemoteRepository> clazz = (Class<MavenRemoteRepository>) MavenRemoteRepository.class.getClassLoader()
                    .loadClass(NAME_IMPL_CLASS);
            ctor = clazz.getConstructor(String.class, URL.class, String.class);
        } catch (final Exception e) {
            throw new RuntimeException("Could not obtain constructor for " + MavenRemoteRepository.class.getSimpleName(), e);
        }
    }

    /**
     * No instances
     */
    private MavenRemoteRepositories() {
        throw new UnsupportedOperationException("No instances permitted");
    }

    /**
     * Creates a new <code>MavenRemoteRepository</code> with ID and URL. Please note that the repository layout is always set to default.
     *
     * @param id the unique ID of the repository to create (arbitrary name)
     * @param url the base URL of the Maven repository
     * @return
     * @throws IllegalArgumentException for null or empty id
     * @throws RuntimeException if an error occurred during <code>MavenRemoteRepository</code> instance creation
     */
    public static MavenRemoteRepository createRemoteRepository(final String id, final URL url, final String layout) {
        // Duplication of argument tests from MavenRemoteRepositoryImpl - as it
        // would be hidden behind reflection exceptions
        if (id == null) {
            throw new IllegalArgumentException("name cannot be null");
        }
        if (url == null) {
            throw new IllegalArgumentException("url cannot be null");
        }
        if (layout == null) {
            throw new IllegalArgumentException("layout cannot be null");
        }
        if (!layout.equals("default")) {
            throw new IllegalArgumentException("layout must be default. Parameter reserved for later use");
        }

        try {
            return ctor.newInstance(id, url, layout);
        } catch (final Exception e) {
            throw new RuntimeException("Could not create new " + MavenRemoteRepository.class.getSimpleName() + " instance", e);
        }
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
