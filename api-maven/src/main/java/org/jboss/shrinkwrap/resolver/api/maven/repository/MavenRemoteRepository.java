package org.jboss.shrinkwrap.resolver.api.maven.repository;

public interface MavenRemoteRepository {

    /**
     * Returns the unique ID of this {@link MavenRemoteRepository}. The ID is arbitrary. There is no default, and this is
     * never <code>null</code>
     *
     * @return
     */
    String getId();

    /**
     * Returns the type, i.e. the layout of this {@link MavenRemoteRepository}. It is always "default".
     *
     * @return
     */
    String getType();

    /**
     * Returns the URL of this {@link MavenRemoteRepository}. There is no default, and this is never <code>null</code>
     *
     * @return
     */
    String getUrl();

    /**
     * Returns the policy chosen for refreshing locally stored/cached artifacts (both for release and snapshot)
     *
     * @return
     */
    MavenUpdatePolicy getUpdatePolicy();

    /**
     * Returns the policy chosen for checking locally stored/cached artifacts (both for release and snapshot)
     *
     * @return
     */
    MavenChecksumPolicy getChecksumPolicy();

    /**
     * Sets the policy chosen for refreshing locally stored/cached artifacts (both for release and snapshot) in a fluent way.
     * This is null by default, which means Maven's default will be used
     *
     * @param policy
     * @return the same object
     */
    MavenRemoteRepository setUpdatePolicy(MavenUpdatePolicy policy);

    /**
     * Sets the policy chosen for checking locally stored/cached artifacts (both for release and snapshot) in a fluent way.
     * This is null by default, which means Maven's default will be used
     *
     * @param policy
     * @return the same object
     */
    MavenRemoteRepository setChecksumPolicy(MavenChecksumPolicy policy);
}
