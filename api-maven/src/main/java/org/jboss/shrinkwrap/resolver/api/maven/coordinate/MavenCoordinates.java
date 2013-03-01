/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.shrinkwrap.resolver.api.maven.coordinate;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jboss.shrinkwrap.resolver.api.CoordinateParseException;
import org.jboss.shrinkwrap.resolver.api.maven.PackagingType;

/**
 * Factory class for creating new {@link MavenCoordinate} instances
 *
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
public final class MavenCoordinates {

    /**
     * No instances
     */
    private MavenCoordinates() {
        throw new UnsupportedOperationException("No instances permitted");
    }

    /**
     * Creates a new {@link MavenCoordinate} instance from the specified, required canonical form in format
     * <code><groupId>:<artifactId>[:<packagingType>[:<classifier>]]:(<version>|'?')</code>
     *
     * @param canonicalForm
     * @return
     * @throws IllegalArgumentException
     *             If the canonical form is not supplied
     * @throws CoordinateParseException
     *             If the specified canonical form is not valid
     */
    public static MavenCoordinate createCoordinate(final String canonicalForm) throws IllegalArgumentException,
        CoordinateParseException {
        if (canonicalForm == null || canonicalForm.length() == 0) {
            throw new IllegalArgumentException("canonical form is required");
        }
        final MavenCoordinateParser parser = MavenCoordinateParser.parse(canonicalForm);
        return createCoordinate(parser.getGroupId(), parser.getArtifactId(), parser.getVersion(),
            parser.getPackaging(), parser.getClassifier());
    }

    /**
     * Creates a new {@link MavenCoordinate} instance from the specified arguments
     *
     * @param groupId
     * @param artifactId
     * @param version
     * @param packaging
     * @param classifier
     * @return
     * @throws IllegalArgumentException
     *             If <code>groupId</code> or <code>artifactId</code> is not specified
     */
    public static MavenCoordinate createCoordinate(final String groupId, final String artifactId, final String version,
        final PackagingType packaging, final String classifier) throws IllegalArgumentException {
        if (groupId == null || groupId.length() == 0) {
            throw new IllegalArgumentException("groupId is required");
        }
        if (artifactId == null || artifactId.length() == 0) {
            throw new IllegalArgumentException("artifactId is required");
        }
        final MavenCoordinateImpl coordinate = new MavenCoordinateImpl(groupId, artifactId, version, packaging,
            classifier);
        return coordinate;
    }

    /**
     * Parser to obtain {@link MavenCoordinate} instances from the canonical {@link String} form
     * <code><groupId>:<artifactId>[:<packagingType>[:<classifier>]]:(<version>|'?')</code>
     *
     * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
     * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
     */
    static class MavenCoordinateParser {

        private static final Pattern DEPENDENCY_PATTERN = Pattern
            .compile("([^: ]+):([^: ]+)(:([^: ]*)(:([^: ]+))?)?(:([^: ]+))?");

        private static final String EMPTY_STRING = "";

        private static final int IS_POS_1 = 1;
        private static final int ID_POS_2 = 2;
        private static final int ID_POS_3 = 4;
        private static final int ID_POS_4 = 6;
        private static final int ID_POS_5 = 8;

        private String groupId;
        private String artifactId;
        private PackagingType type;
        private String classifier;
        private String version;

        // provides default values
        private MavenCoordinateParser() {
            this.type = PackagingType.JAR;
            // To be compliant with the Maven/Aether parsers, classifiers that aren't specified go to empty Strings, not
            // null. Go figure.
            this.classifier = EMPTY_STRING;
        }

        static MavenCoordinateParser parse(final String coordinates) throws CoordinateParseException {

            final Matcher m = DEPENDENCY_PATTERN.matcher(coordinates);
            if (!m.matches()) {
                throw new CoordinateParseException("Bad artifact coordinates"
                    + ", expected format is <groupId>:<artifactId>[:<packagingType>[:<classifier>]]:(<version>|'?'), got: "
                    + coordinates);
            }

            final MavenCoordinateParser parser = new MavenCoordinateParser();

            parser.groupId = m.group(IS_POS_1);
            parser.artifactId = m.group(ID_POS_2);

            final String position3 = m.group(ID_POS_3);
            final String position4 = m.group(ID_POS_4);
            final String position5 = m.group(ID_POS_5);

            // some logic with numbers of provided groups
            final int noOfColons = numberOfOccurrences(coordinates, MavenGABaseImpl.SEPARATOR_COORDINATE);

            // Parsing is segment-dependent
            switch (noOfColons) {
                case 2:
                    parser.version = position3;
                    break;
                case 3:
                    parser.type = (position3 == null || position3.length() == 0) ? PackagingType.JAR
                        : toPackagingType(position3);
                    parser.version = position4;
                    break;
                default:
                    parser.type = (position3 == null || position3.length() == 0) ? PackagingType.JAR
                        : toPackagingType(position3);
                    parser.classifier = position4;
                    parser.version = position5;
            }

            return parser;
        }

        public PackagingType getPackaging() {
            return type;
        }

        public String getClassifier() {
            return classifier;
        }

        public String getVersion() {
            return version;
        }

        public String getGroupId() {
            return groupId;
        }

        public String getArtifactId() {
            return artifactId;
        }

        private static int numberOfOccurrences(final CharSequence haystack, char needle) {
            int counter = 0;
            for (int i = 0; i < haystack.length(); i++) {
                if (haystack.charAt(i) == needle) {
                    counter++;
                }
            }
            return counter;
        }

        private static PackagingType toPackagingType(final String type) {
            assert type != null : "Should not be fed a null type via internals (regardless of user input)";
            PackagingType parsedPackagingType = null;
            try {
                parsedPackagingType = PackagingType.of(type);
            } catch (final IllegalArgumentException iae) {
                // Exception translate
                throw new CoordinateParseException(iae.getMessage());
            }
            return parsedPackagingType;
        }

    }
}
