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

import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenCoordinate;

/**
 * Represents the valid values for the "packaging" portion of a {@link MavenCoordinate}
 *
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public final class PackagingType {

    public static final PackagingType POM = PackagingType.of("pom");
    public static final PackagingType JAR = PackagingType.of("jar");
    public static final PackagingType MAVEN_PLUGIN = PackagingType.of("maven-plugin");
    public static final PackagingType EJB = PackagingType.of("ejb");
    public static final PackagingType WAR = PackagingType.of("war");
    public static final PackagingType EAR = PackagingType.of("ear");
    public static final PackagingType RAR = PackagingType.of("rar");
    public static final PackagingType PAR = PackagingType.of("par");

    private final String value;

    private PackagingType(final String value) {
        this.value = value;
    }

    /**
     * Returns the canonical {@link String} value of this {@link PackagingType}
     *
     */
    @Override
    public String toString() {
        return this.value;
    }

    /**
     * Builds a {@link PackagingType} object
     *
     * @param typeName
     *            String name of the packaging type
     * @return Corresponding PackagingType object
     * @throws IllegalArgumentException
     *             Thrown if typeName is {@code null} or empty
     */
    public static PackagingType of(String typeName) throws IllegalArgumentException {

        if (typeName == null || typeName.length() == 0) {
            throw new IllegalArgumentException("Packaging type must not be null nor empty.");
        }
        return new PackagingType(typeName);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PackagingType other = (PackagingType) obj;
        if (value == null) {
            if (other.value != null)
                return false;
        } else if (!value.equals(other.value))
            return false;
        return true;
    }

}
