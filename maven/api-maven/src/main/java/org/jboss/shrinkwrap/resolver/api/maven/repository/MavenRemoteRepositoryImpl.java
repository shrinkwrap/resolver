package org.jboss.shrinkwrap.resolver.api.maven.repository;

import java.net.MalformedURLException;
import java.net.URL;

class MavenRemoteRepositoryImpl implements MavenRemoteRepository {

    String id, layout;
    URL url;
    MavenUpdatePolicy upPolicy = MavenUpdatePolicy.UPDATE_POLICY_NEVER;
    MavenChecksumPolicy ckPolicy = MavenChecksumPolicy.CHECKSUM_POLICY_WARN;

    /**
     * Builds a new remote repository.
     *
     * @param id
     * @param url the url is supposed to be correct at this step.
     * @throws MalformedURLException
     */
    MavenRemoteRepositoryImpl(String id, String url, String layout) throws MalformedURLException {
        this(id, new URL(url), layout);
    }

    MavenRemoteRepositoryImpl(String id, URL url, String layout) {
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

        this.id = id;
        this.layout = layout;
        this.url = url;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getType() {
        return layout;
    }

    @Override
    public String getUrl() {
        return url.toString();
    }

    @Override
    public MavenUpdatePolicy getUpdatePolicy() {
        return upPolicy;
    }

    @Override
    public MavenChecksumPolicy getChecksumPolicy() {
        return ckPolicy;
    }

    @Override
    public MavenRemoteRepository setUpdatePolicy(MavenUpdatePolicy policy) {
        this.upPolicy = policy;
        return this;
    }

    @Override
    public MavenRemoteRepository setChecksumPolicy(MavenChecksumPolicy policy) {
        this.ckPolicy = policy;
        return this;
    }
}
