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

import java.util.HashMap;
import java.util.Map;

import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenCoordinateBase;

/**
 * Represents the valid values for the "packaging" portion of a {@link MavenCoordinateBase}
 *
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
public enum PackagingType {
    POM("pom"), JAR("jar"), MAVEN_PLUGIN("maven-plugin"), EJB("ejb"), WAR("war"), EAR("ear"), RAR("rar"), PAR("par");

    private final String value;

    PackagingType(final String value) {
        this.value = value;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return this.value;
    }

    /**
     * Maps a string to PackagingType
     *
     * @param typeName String name of the packaging type
     * @return Corresponding PackagingType object
     * @throws IllegalArgumentException Thrown if typeName is {@code null}, empty or does not represent a valid packaging type
     */
    public static PackagingType fromPackagingType(String typeName) throws IllegalArgumentException {

        if (typeName == null || typeName.length() == 0) {
            throw new IllegalArgumentException("Packaging type must not be null nor empty.");
        }

        PackagingType pt = PACKAGING_NAME_CACHE.get(typeName);
        if (pt == null) {
            throw new IllegalArgumentException("Packaging type " + typeName + " is not supported.");
        }
        return pt;
    }

    private static final Map<String, PackagingType> PACKAGING_NAME_CACHE = new HashMap<String, PackagingType>() {
        private static final long serialVersionUID = 1L;
        {
            for (PackagingType packaging : PackagingType.values()) {
                this.put(packaging.value, packaging);
            }
        }
    };

}
