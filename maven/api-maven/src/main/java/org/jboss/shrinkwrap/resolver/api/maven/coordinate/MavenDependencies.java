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

import java.lang.reflect.Constructor;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import org.jboss.shrinkwrap.resolver.api.CoordinateParseException;
import org.jboss.shrinkwrap.resolver.api.maven.ScopeType;

/**
 * Factory class for creating new {@link MavenDependency} instances
 *
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
public final class MavenDependencies {

    private static final String NAME_IMPL_CLASS = "org.jboss.shrinkwrap.resolver.impl.maven.coordinate.MavenDependencyImpl";
    private static final Constructor<MavenDependency> ctor;
    static {
        try {
            @SuppressWarnings("unchecked")
            final Class<MavenDependency> clazz = (Class<MavenDependency>) MavenDependencies.class.getClassLoader()
                .loadClass(NAME_IMPL_CLASS);
            ctor = clazz.getConstructor(MavenCoordinate.class, ScopeType.class, boolean.class,
                MavenDependencyExclusion[].class);
        } catch (final Exception e) {
            throw new RuntimeException("Could not obtain constructor for " + MavenDependency.class.getSimpleName(), e);
        }
    }

    /**
     * No instances
     */
    private MavenDependencies() {
        throw new UnsupportedOperationException("No instances permitted");
    }

    /**
     * Creates a new {@link MavenDependency} instance from the specified, required canonical form in format
     * {@code <groupId>:<artifactId>[:<packagingType>[:<classifier>]][:<version>]}, with the additional, optional
     * properties. If no {@link ScopeType} is specified, default will be {@link ScopeType#COMPILE}.
     *
     * @param canonicalForm A canonical form in format {@code <groupId>:<artifactId>[:<packagingType>[:<classifier>]][:<version>]}
     *                      of the new {@link MavenDependency} instance.
     * @param scope A scope of the new {@link MavenDependency} instance. Default will be {@link ScopeType#COMPILE}.
     * @param optional Whether or not this {@link MavenDependency} has been marked as optional; defaults to <code>false</code>.
     * @param exclusions Exclusions of the new {@link MavenDependency} instance.
     * @return The new {@link MavenDependency} instance.
     * @throws IllegalArgumentException
     *             If the canonical form is not supplied
     * @throws CoordinateParseException
     *             If the specified canonical form is not valid
     */
    public static MavenDependency createDependency(final String canonicalForm, final ScopeType scope,
        final boolean optional, final MavenDependencyExclusion... exclusions) throws IllegalArgumentException,
        CoordinateParseException {
        if (canonicalForm == null || canonicalForm.isEmpty()) {
            throw new IllegalArgumentException("canonical form is required");
        }
        final MavenCoordinate delegate = MavenCoordinates.createCoordinate(canonicalForm);
        return createDependency(delegate, scope, optional, exclusions);
    }

    /**
     * Creates a new {@link MavenDependency} instance from the specified properties. If no {@link ScopeType} is
     * specified, default will be {@link ScopeType#COMPILE}.
     *
     * @param coordinate A coordinate of the new {@link MavenDependency} instance.
     * @param scope A scope of the new {@link MavenDependency} instance. Default will be {@link ScopeType#COMPILE}.
     * @param optional Whether or not this {@link MavenDependency} has been marked as optional; defaults to <code>false</code>.
     * @param exclusions Exclusions of the new {@link MavenDependency} instance.
     * @return The new {@link MavenDependency} instance.
     * @throws IllegalArgumentException
     *             If the coordinate is not supplied
     * @throws CoordinateParseException
     *             If the specified canonical form is not valid
     */
    public static MavenDependency createDependency(final MavenCoordinate coordinate, final ScopeType scope,
        final boolean optional, final MavenDependencyExclusion... exclusions) throws IllegalArgumentException,
        CoordinateParseException {
        if (coordinate == null) {
            throw new IllegalArgumentException("coordinate form is required");
        }
        final MavenDependency dep = newInstance(coordinate, scope, optional, exclusions);
        return dep;
    }

    /**
     * Creates a new {@link MavenDependency} instance
     *
     * @param coordinate A coordinate of the new {@link MavenDependency} instance.
     * @param scope A scope of the new {@link MavenDependency} instance. Default will be {@link ScopeType#COMPILE}.
     * @param optional Whether or not this {@link MavenDependency} has been marked as optional; defaults to <code>false</code>.
     * @param exclusions Exclusions of the new {@link MavenDependency} instance.
     * @return The new {@link MavenDependency} instance.
     */
    private static MavenDependency newInstance(final MavenCoordinate coordinate, final ScopeType scope,
        final boolean optional, final MavenDependencyExclusion... exclusions) {
        assert coordinate != null : "coordinate must be specified";
        assert exclusions != null : "exclusions must be specified";
        try {
            return ctor.newInstance(coordinate, scope, optional, exclusions);
        } catch (final Exception e) {
            throw new RuntimeException("Could not create new " + MavenDependency.class.getSimpleName() + "instance", e);
        }
    }

    /**
     * Creates a new {@link MavenDependencyExclusion} instance from the specified, required canonical form in format
     * {@code <groupId>:<artifactId>}
     *
     * @param canonicalForm A canonical form in format {@code <groupId>:<artifactId>}
     * @return The new {@link MavenDependencyExclusion} instance.
     * @throws IllegalArgumentException
     *             If the canonical form is not supplied
     * @throws CoordinateParseException
     *             If the canonical form is not in the correct format
     */
    public static MavenDependencyExclusion createExclusion(final String canonicalForm) throws IllegalArgumentException,
        CoordinateParseException {
        if (canonicalForm == null || canonicalForm.isEmpty()) {
            throw new IllegalArgumentException("canonical form is required");
        }
        final StringTokenizer tokenizer = new StringTokenizer(canonicalForm,
            String.valueOf(MavenGABaseImpl.SEPARATOR_COORDINATE));
        final String groupId;
        final String artifactId;
        try {
            groupId = tokenizer.nextToken();
            artifactId = tokenizer.nextToken();
        } catch (final NoSuchElementException nsee) {
            // Exception translate
            throw new CoordinateParseException("Canonical form must be \"groupId:artifactId\"; got: " + canonicalForm);
        }
        // Ensure there isn't *more* defined than we need
        if (tokenizer.hasMoreTokens()) {
            throw new CoordinateParseException("Canonical form must be \"groupId:artifactId\"; got: " + canonicalForm);
        }
        final MavenDependencyExclusion exclusion = createExclusion(groupId, artifactId);
        return exclusion;
    }

    /**
     * Creates a new {@link MavenDependencyExclusion} instance from the specified, required arguments
     *
     * @param groupId A groupId of the new {@link MavenDependencyExclusion} instance.
     * @param artifactId An artifactId of the new {@link MavenDependencyExclusion} instance.
     * @return  The new {@link MavenDependencyExclusion} instance.
     * @throws IllegalArgumentException
     *             If either argument is not specified
     */
    public static MavenDependencyExclusion createExclusion(final String groupId, final String artifactId)
        throws IllegalArgumentException {
        if (groupId == null || groupId.isEmpty()) {
            throw new IllegalArgumentException("groupId must be specified");
        }
        if (artifactId == null || artifactId.isEmpty()) {
            throw new IllegalArgumentException("groupId must be specified");
        }
        final MavenDependencyExclusion exclusion = new MavenDependencyExclusionImpl(groupId, artifactId);
        return exclusion;
    }

}
