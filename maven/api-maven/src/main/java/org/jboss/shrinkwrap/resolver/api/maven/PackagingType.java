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
package org.jboss.shrinkwrap.resolver.api.maven;

import java.util.concurrent.ConcurrentHashMap;

import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenCoordinate;

/**
 * Represents the valid values for the "packaging" portion of a {@link MavenCoordinate}
 *
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class PackagingType {

    private static final ConcurrentHashMap<String, PackagingType> cache = new ConcurrentHashMap<String, PackagingType>();

    public static final PackagingType POM = new PackagingType("pom");
    public static final PackagingType JAR = new PackagingType("jar");
    public static final PackagingType TEST_JAR = new PackagingType("test-jar", "jar", "tests");
    public static final PackagingType MAVEN_PLUGIN = new PackagingType("maven-plugin", "jar", "");
    public static final PackagingType EJB_CLIENT = new PackagingType("ejb-client", "jar", "client");
    public static final PackagingType EJB = new PackagingType("ejb", "jar", "");
    public static final PackagingType WAR = new PackagingType("war");
    public static final PackagingType EAR = new PackagingType("ear");
    public static final PackagingType RAR = new PackagingType("rar");
    public static final PackagingType PAR = new PackagingType("par");
    public static final PackagingType JAVADOC = new PackagingType("javadoc", "jar", "javadoc");
    public static final PackagingType JAVA_SOURCE = new PackagingType("java-source", "jar", "sources");

    private final String id;
    private final String extension;
    private final String classifier;

    private PackagingType(final String id, final String extension, final String classifier) {
        this.id = id;
        this.extension = extension;
        this.classifier = classifier;
        cache.putIfAbsent(id, this);
    }

    private PackagingType(final String id) {
        this(id, id, "");
    }

    /**
     * Returns type of the packaging. Might be the same as extension.
     *
     * @return Type of the packaging.
     */
    public String getId() {
        return id;
    }

    /**
     * Returns extension for packaging. Might be the same as id;
     *
     * @return Extension for packaging.
     */
    public String getExtension() {
        return extension;
    }

    /**
     * Returns classifier for packaging. Might be empty string.
     *
     * @return Classifier for packaging.
     */
    public String getClassifier() {
        return classifier;
    }

    /**
     * Returns the canonical {@link String} value of this {@link PackagingType}
     *
     */
    @Override
    public String toString() {
        return this.id;
    }

    /**
     * Builds a {@link PackagingType} object
     *
     * @param typeName
     * String name of the packaging type
     * @return Corresponding PackagingType object
     * @throws IllegalArgumentException
     * Thrown if typeName is {@code null} or empty
     */
    public static PackagingType of(String typeName) throws IllegalArgumentException {

        if (typeName == null || typeName.length() == 0) {
            throw new IllegalArgumentException("Packaging type must not be null nor empty.");
        }
        // return from cache if available
        if(cache.containsKey(typeName)) {
            return cache.get(typeName);
        }
        // this will cause packaging object to register into cache
        return new PackagingType(typeName);
    }

    // we are using only id for hashCode() and equals(Object)
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    // we are using only id for hashCode() and equals(Object)
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PackagingType other = (PackagingType) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }



}
