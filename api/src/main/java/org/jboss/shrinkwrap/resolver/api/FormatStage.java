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
package org.jboss.shrinkwrap.resolver.api;

import java.io.File;
import java.io.InputStream;
import java.util.List;


/**
 * Represents the formatting stage of resolution in which the {@code RESOLVEDTYPE} is returned in the desired format.
 * Supports extensible formats by registering a FormatProcessor with the SPI.
 *
 * @param <RESOLVEDTYPE> The resolved type
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 */
public interface FormatStage<RESOLVEDTYPE extends ResolvedArtifact<RESOLVEDTYPE>> {

    /**
     * Formats the resultant artifacts as an array of {@link File}s
     *
     * @return The formatted resultant artifacts as an array of {@link File}s
     */
    File[] asFile();

    /**
     * Formats the resultant artifact as a {@link File}; assumes a single artifact is returned from resolution.
     *
     * @return The formatted resultant artifacts as a {@link File}
     * @throws NonUniqueResultException If the resolution resulted in more than one result
     * @throws NoResolvedResultException If the resolution did not yield any result
     */
    File asSingleFile() throws NonUniqueResultException, NoResolvedResultException;

    /**
     * Formats the resultant artifacts as an array of {@link InputStream}s. It is a caller responsibility to close the streams
     * afterwards.
     *
     * @return The formatted resultant artifacts as an array of {@link InputStream}s.
     */
    InputStream[] asInputStream();

    /**
     * Formats the resultant artifact as an {@link InputStream}; assumes a single artifact is returned from resolution. It is a
     * caller responsibility to close the stream afterwards.
     *
     * @return The formatted resultant artifacts as an {@link InputStream}.
     * @throws NonUniqueResultException If the resolution resulted in more than one result
     * @throws NoResolvedResultException If the resolution did not yield any result
     */
    InputStream asSingleInputStream() throws NonUniqueResultException, NoResolvedResultException;

    /**
     * Formats the resultant artifacts as an array of {@code RESOLVEDTYPE}.
     *
     * @return The formatted resultant artifacts as an array of {@code RESOLVEDTYPE}.
     */
    RESOLVEDTYPE[] asResolvedArtifact();

    /**
     * Formats the resultant artifact as {@code RESOLVEDTYPE}; assumes a single artifact is returned from resolution.
     *
     * @return The formatted resultant artifacts as {@code RESOLVEDTYPE}.
     * @throws NonUniqueResultException If the resolution resulted in more than one result
     * @throws NoResolvedResultException If the resolution did not yield any result
     */
    RESOLVEDTYPE asSingleResolvedArtifact() throws NonUniqueResultException, NoResolvedResultException;

    /**
     * Formats the resultant artifacts as an array of {@code type}s. If nothing matches resolution, an empty array will
     * be returned. Supports extensible formats by registering a {@code FormatProcessor} for given {@code returnTypeClass}.
     *
     * @param <RETURNTYPE> The return type
     * @param returnTypeClass The return type class
     * @return The formatted resultant artifacts as an array of {@code type}s.
     * @throws IllegalArgumentException If the type is not specified
     * @throws UnsupportedOperationException If the type is not supported *
     */
    <RETURNTYPE> RETURNTYPE[] as(Class<RETURNTYPE> returnTypeClass) throws IllegalArgumentException,
            UnsupportedOperationException;

    /**
     * Formats the resultant artifacts as a list of {@code type}s. If nothing matches resolution, an empty list will
     * be returned. Supports extensible formats by registering a {@code FormatProcessor} for given {@code returnTypeClass}.
     *
     * @param <RETURNTYPE> The return type
     * @param returnTypeClass The return type class
     * @return The formatted resultant artifacts as a list of {@code type}s.
     * @throws IllegalArgumentException If the type is not specified
     * @throws UnsupportedOperationException If the type is not supported *
     */
    <RETURNTYPE> List<RETURNTYPE> asList(Class<RETURNTYPE> returnTypeClass) throws IllegalArgumentException,
            UnsupportedOperationException;

    /**
     * Formats the resultant artifact as a {@code type}; assumes a single artifact is returned from resolution.
     * Supports extensible formats by registering a {@code FormatProcessor} for given {@code returnTypeClass}.
     *
     * @param <RETURNTYPE> The return type
     * @param returnTypeClass The return type class
     * @return The formatted resultant artifacts as a {@code type}.
     * @throws NonUniqueResultException If the resolution resulted in more than one result
     * @throws NoResolvedResultException If the resolution did not yield any result
     * @throws IllegalArgumentException If the type is not specified
     * @throws UnsupportedOperationException If the type is not supported
     */
    <RETURNTYPE> RETURNTYPE asSingle(Class<RETURNTYPE> returnTypeClass) throws IllegalArgumentException,
            UnsupportedOperationException,
            NonUniqueResultException,
            NoResolvedResultException;
}
