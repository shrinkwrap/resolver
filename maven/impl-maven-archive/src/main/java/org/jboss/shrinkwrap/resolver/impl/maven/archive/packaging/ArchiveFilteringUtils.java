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
package org.jboss.shrinkwrap.resolver.impl.maven.archive.packaging;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.codehaus.plexus.util.SelectorUtils;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.Filter;
import org.jboss.shrinkwrap.api.Node;
import org.jboss.shrinkwrap.api.ShrinkWrap;

/**
 * Utils related to filtering of archive content
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 *
 */
public class ArchiveFilteringUtils {

    public static <T extends Archive<?>> T filterArchiveContent(T archive, Class<T> archiveType, final String[] includes,
            final String[] excludes) {
        return filterArchiveContent(archive, archiveType, Arrays.asList(includes), Arrays.asList(excludes));
    }

    public static <T extends Archive<?>> T filterArchiveContent(T archive, Class<T> archiveType, final List<String> includes,
            final List<String> excludes) {

        // get all files that should be included in archive
        Map<ArchivePath, Node> includePart = archive.getContent(new Filter<ArchivePath>() {
            @Override
            public boolean include(ArchivePath path) {

                // trim first slash
                String pathAsString = path.get();
                pathAsString = pathAsString.startsWith("/") ? pathAsString.substring(1) : pathAsString;

                boolean include = false;
                // include all files that should be included
                includesLoop: for (String i : includes) {
                    // paths in ShrinkWrap archives are always "/" separated
                    if (SelectorUtils.matchPath(i, pathAsString, "/", true)) {
                        // if file should be included, check also for excludes
                        for (String e : excludes) {
                            if (SelectorUtils.matchPath(e, pathAsString, "/", true)) {
                                break includesLoop;
                            }
                        }
                        include = true;
                        break;
                    }
                }

                return include;
            }
        });

        // create new archive and merge content together
        T newArchive = ShrinkWrap.create(archiveType, archive.getName());

        for (Map.Entry<ArchivePath, Node> entry : includePart.entrySet()) {
            if (entry.getValue() != null && entry.getValue().getAsset() != null) {
                newArchive.add(entry.getValue().getAsset(), entry.getKey());
            }
        }

        return newArchive;
    }
}
