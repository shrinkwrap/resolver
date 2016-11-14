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

class MavenRemoteRepositoryImpl implements MavenRemoteRepository {

    String id, layout;
    URL url;
    MavenUpdatePolicy upPolicy = null;
    MavenChecksumPolicy ckPolicy = null;

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
