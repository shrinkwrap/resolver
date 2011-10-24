package org.jboss.shrinkwrap.resolver.impl.maven;

import org.jboss.shrinkwrap.resolver.api.maven.MavenRepository;

class MavenRepositoryImpl implements MavenRepository {

    private static final String DEFAULT_LAYOUT = "default";

    private String url;
    private String id;
    private String layout;

    public MavenRepositoryImpl(String url) {
        this.id = url;
        this.url = url;
        this.layout = DEFAULT_LAYOUT;
    }

    @Override
    public MavenRepository id(String id) {
        this.id = id;
        return this;
    }

    @Override
    public MavenRepository url(String url) {
        this.url = url;
        return this;
    }

    @Override
    public MavenRepository layout(String layout) {
        this.layout = layout;
        return this;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public String url() {
        return url;
    }

    @Override
    public String layout() {
        return layout;
    }

}
