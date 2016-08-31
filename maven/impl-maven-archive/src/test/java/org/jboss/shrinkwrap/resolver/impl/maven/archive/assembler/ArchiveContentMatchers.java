/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.shrinkwrap.resolver.impl.maven.archive.assembler;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.Node;
import org.jboss.shrinkwrap.api.asset.Asset;

/**
 * Represents Hamcrest matchers for validating content of an Archive
 *
 * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
 * @author <a href="mailto:mjobanek@redhat.com">Matous Jobanek</a>
 *
 */
public final class ArchiveContentMatchers {

    public static ArchiveContentMatcher contains(String path) {
        return new ArchiveContentMatcher(path);
    }

    public static ArchiveSizeMatcher size(int filesTotal) {
        return new ArchiveSizeMatcher(filesTotal);
    }

    public static ManifestAssetMatcher hasManifestEntry(String section, String entry, String value) {
        return new ManifestAssetMatcher(section, entry, value);
    }

    public static ManifestAssetMatcher hasManifestEntry(String entry) {
        return new ManifestAssetMatcher(null, entry, null);
    }

    public static ManifestAssetMatcher hasManifestEntry(String entry, String value) {
        return new ManifestAssetMatcher(null, entry, value);
    }

    /**
     * Checks that archive {@link Asset}, that represents Manifest contains a manifest entry with specified value or at least
     * entry itself
     *
     * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
     *
     */
    public static class ManifestAssetMatcher extends BaseMatcher<Asset> implements Matcher<Asset> {

        private final String section;
        private final String name;
        private final String value;

        private ManifestAssetMatcher(String section, String entry, String value) {
            this.section = section;
            this.name = entry;
            this.value = value;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("Archive manifest contains entry named: ").appendText(name);
            if (value != null) {
                description.appendText(" with value: ").appendText(value);
            }
            if (section != null) {
                description.appendText("(in section ").appendText(section).appendText(")");
            }
        }

        @Override
        public boolean matches(Object item) {
            if (item == null) {
                return false;
            } else if (item instanceof Asset) {
                Manifest mf = getManifest((Asset) item);

                String manifestValue = null;
                if (section == null) {
                    manifestValue = mf.getMainAttributes().getValue(name);
                } else {
                    Attributes attrs = mf.getAttributes(section);
                    if (attrs != null) {
                        manifestValue = attrs.getValue(name);
                    }
                }
                return value == null ? manifestValue != null : value.equals(manifestValue);
            }
            return false;
        }

        private Manifest getManifest(Asset asset) {
            InputStream is = null;
            try {
                is = asset.openStream();
                return new Manifest(is);
            } catch (IOException e) {

            } finally {
                try {
                    if (is != null) {
                        is.close();
                    }
                } catch (IOException e) {
                }
            }
            return new Manifest();
        }

    }

    /**
     * Checks that an archive contains specific number of assets (directories are excluded)
     *
     * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
     *
     */
    public static class ArchiveSizeMatcher extends BaseMatcher<Map<ArchivePath, Node>> implements
            Matcher<Map<ArchivePath, Node>> {

        private final int assetsTotal;

        private ArchiveSizeMatcher(int assetsTotal) {
            this.assetsTotal = assetsTotal;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("Archive contains ").appendText(String.valueOf(assetsTotal)).appendText(" assets.");
        }

        @Override
        public boolean matches(Object item) {
            if (item == null) {
                return false;
            } else if (item instanceof Map<?, ?>) {
                @SuppressWarnings("unchecked")
                Map<ArchivePath, Node> content = (Map<ArchivePath, Node>) item;
                int assets = 0;
                for (Map.Entry<ArchivePath, Node> entry : content.entrySet()) {
                    if (entry.getValue().getAsset() != null) {
                        assets++;
                    }
                }
                return assets == assetsTotal;
            }
            return false;
        }
    }

    /**
     * Checks that archive contains a node under given path
     *
     * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
     *
     */
    public static class ArchiveContentMatcher extends BaseMatcher<Map<ArchivePath, Node>> implements
            Matcher<Map<ArchivePath, Node>> {

        private final String path;

        private ArchiveContentMatcher(String path) {
            this.path = path;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("Archive contains path ").appendText(path);
        }

        @Override
        public boolean matches(Object item) {
            if (item == null) {
                return false;
            } else if (item instanceof Map<?, ?>) {
                @SuppressWarnings("unchecked")
                Map<ArchivePath, Node> content = (Map<ArchivePath, Node>) item;
                return content.containsKey(ArchivePaths.create(path));
            }

            else if (item instanceof Archive<?>) {
                Archive<?> archive = (Archive<?>) item;
                return archive.contains(path);
            }
            return false;
        }

    }
}
