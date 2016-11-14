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

public interface MavenRemoteRepository {

    /**
     * Returns the unique ID of this {@link MavenRemoteRepository}. The ID is arbitrary. There is no default, and this is
     * never <code>null</code>
     *
     * @return The unique ID of this {@link MavenRemoteRepository}.
     */
    String getId();

    /**
     * Returns the type, i.e. the layout of this {@link MavenRemoteRepository}. It is always "default".
     *
     * @return The type, i.e. the layout of this {@link MavenRemoteRepository}. It is always "default".
     */
    String getType();

    /**
     * Returns the URL of this {@link MavenRemoteRepository}. There is no default, and this is never <code>null</code>
     *
     * @return The URL of this {@link MavenRemoteRepository}. There is no default, and this is never <code>null</code>
     */
    String getUrl();

    /**
     * Returns the policy chosen for refreshing locally stored/cached artifacts (both for release and snapshot)
     *
     * @return The policy chosen for refreshing locally stored/cached artifacts (both for release and snapshot)
     */
    MavenUpdatePolicy getUpdatePolicy();

    /**
     * Returns the policy chosen for checking locally stored/cached artifacts (both for release and snapshot)
     *
     * @return The policy chosen for checking locally stored/cached artifacts (both for release and snapshot)
     */
    MavenChecksumPolicy getChecksumPolicy();

    /**
     * Sets the policy chosen for refreshing locally stored/cached artifacts (both for release and snapshot) in a fluent way.
     * This is null by default, which means Maven's default will be used
     *
     * @param policy The policy chosen for refreshing locally stored/cached artifacts (both for release and snapshot)
     *               in a fluent way. This is null by default.
     * @return the same object
     */
    MavenRemoteRepository setUpdatePolicy(MavenUpdatePolicy policy);

    /**
     * Sets the policy chosen for checking locally stored/cached artifacts (both for release and snapshot) in a fluent way.
     * This is null by default, which means Maven's default will be used
     *
     * @param policy the policy chosen for checking locally stored/cached artifacts (both for release and snapshot) in
     *               a fluent way. This is null by default.
     * @return the same object
     */
    MavenRemoteRepository setChecksumPolicy(MavenChecksumPolicy policy);
}
