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
package org.jboss.shrinkwrap.resolver.impl.maven.coordinate;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jboss.shrinkwrap.resolver.api.CoordinateParseException;
import org.jboss.shrinkwrap.resolver.api.maven.PackagingType;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenCoordinate;

public class MavenCoordinateParser implements MavenCoordinate {

    public static final String UNKNOWN_VERSION = "?";

    private static final Pattern DEPENDENCY_PATTERN = Pattern.compile("([^: ]+):([^: ]+)(:([^: ]*)(:([^: ]+))?)?(:([^: ]+))?");

    private static final int DEPENDENCY_GROUP_ID = 1;
    private static final int DEPENDENCY_ARTIFACT_ID = 2;
    private static final int DEPENDENCY_TYPE_ID = 4;
    private static final int DEPENDENCY_CLASSIFIER_ID = 6;
    private static final int DEPENDENCY_VERSION_ID = 8;

    private String groupId;
    private String artifactId;
    private String type;
    private String classifier;
    private String version;

    // provides default values
    private MavenCoordinateParser() {
        this.classifier = "";
        this.type = PackagingType.JAR.toString();
        this.version = UNKNOWN_VERSION;
    }

    public static MavenCoordinateParser parse(String coordinates) throws CoordinateParseException {

        Matcher m = DEPENDENCY_PATTERN.matcher(coordinates);
        if (!m.matches()) {
            throw new CoordinateParseException("Bad artifact coordinates"
                    + ", expected format is <groupId>:<artifactId>[:<extension>[:<classifier>]][:<version>]");
        }

        MavenCoordinateParser parser = new MavenCoordinateParser();

        parser.groupId = m.group(DEPENDENCY_GROUP_ID);
        parser.artifactId = m.group(DEPENDENCY_ARTIFACT_ID);

        String type = m.group(DEPENDENCY_TYPE_ID);
        String classifier = m.group(DEPENDENCY_CLASSIFIER_ID);
        String version = m.group(DEPENDENCY_VERSION_ID);

        // some logic with numbers of provided groups
        int noOfColons = StringUtil.numberOfOccurences(coordinates, ':');

        if (noOfColons == 1) {
            parser.version = UNKNOWN_VERSION;
        } else if (noOfColons == 2) {
            parser.version = (type == null || type.length() == 0) ? UNKNOWN_VERSION : type;
        } else if (noOfColons == 3) {
            parser.type = (type == null || type.length() == 0) ? PackagingType.JAR.toString() : type;
            parser.version = (classifier == null || classifier.length() == 0) ? UNKNOWN_VERSION : classifier;
        } else {
            parser.type = (type == null || type.length() == 0) ? PackagingType.JAR.toString() : type;
            parser.classifier = classifier;
            parser.version = (version == null || version.length() == 0) ? UNKNOWN_VERSION : version;
        }

        return parser;
    }

    @Override
    public PackagingType getPackaging() {
        return PackagingType.fromPackagingType(type);
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getClassifier() {
        return classifier;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public String getGroupId() {
        return groupId;
    }

    @Override
    public String getArtifactId() {
        return artifactId;
    }

    @Override
    public String getAddress() {
        return groupId + ":" + artifactId + ":" + type + ":" + classifier + ":" + version;
    }
}
