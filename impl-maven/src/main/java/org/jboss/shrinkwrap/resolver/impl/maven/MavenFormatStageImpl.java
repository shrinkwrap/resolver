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
package org.jboss.shrinkwrap.resolver.impl.maven;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.jboss.shrinkwrap.resolver.api.NoResolvedResultException;
import org.jboss.shrinkwrap.resolver.api.NonUniqueResultException;
import org.jboss.shrinkwrap.resolver.api.maven.MavenFormatStage;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolvedArtifact;
import org.jboss.shrinkwrap.resolver.impl.maven.util.Validate;
import org.jboss.shrinkwrap.resolver.spi.format.FormatProcessor;
import org.jboss.shrinkwrap.resolver.spi.format.FormatProcessors;

/**
 * Implementation of {@link MavenFormatStage}
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 */
public class MavenFormatStageImpl implements MavenFormatStage {

    private final Collection<MavenResolvedArtifact> artifacts;

    public MavenFormatStageImpl(final Collection<MavenResolvedArtifact> artifacts) {
        assert artifacts != null : "Artifacts are required";
        this.artifacts = artifacts;
    }

    @Override
    public File[] asFile() {
        return as(File.class);
    }

    @Override
    public File asSingleFile() throws NonUniqueResultException, NoResolvedResultException {
        return asSingle(File.class);
    }

    @Override
    public InputStream[] asInputStream() {
        return as(InputStream.class);
    }

    @Override
    public InputStream asSingleInputStream() throws NonUniqueResultException, NoResolvedResultException {
        return asSingle(InputStream.class);
    }

    @Override
    public MavenResolvedArtifact[] asResolvedArtifact() {
        return as(MavenResolvedArtifact.class);
    }

    @Override
    public MavenResolvedArtifact asSingleResolvedArtifact() throws NonUniqueResultException, NoResolvedResultException {
        return asSingle(MavenResolvedArtifact.class);
    }

    @Override
    public <RETURNTYPE> RETURNTYPE[] as(Class<RETURNTYPE> returnTypeClass) throws IllegalArgumentException,
            UnsupportedOperationException {
        Validate.notNull(returnTypeClass, "Return type class must not be null");

        final FormatProcessor<? super MavenResolvedArtifact, RETURNTYPE> processor = FormatProcessors.find(
                MavenResolvedArtifact.class, returnTypeClass);

        @SuppressWarnings("unchecked")
        final RETURNTYPE[] array = (RETURNTYPE[]) Array.newInstance(returnTypeClass, artifacts.size());

        int i = 0;
        for (final MavenResolvedArtifact artifact : artifacts) {
            array[i++] = processor.process(artifact, returnTypeClass);
        }
        return array;
    }

    @Override
    public <RETURNTYPE> List<RETURNTYPE> asList(Class<RETURNTYPE> returnTypeClass) throws IllegalArgumentException, UnsupportedOperationException {
        return Arrays.asList(as(returnTypeClass));
    }

    @Override
    public <RETURNTYPE> RETURNTYPE asSingle(Class<RETURNTYPE> type) throws IllegalArgumentException,
            UnsupportedOperationException, NonUniqueResultException,
            NoResolvedResultException {

        return getSingle(as(type));
    }

    private <RETURNTYPE> RETURNTYPE getSingle(RETURNTYPE[] array) throws IllegalArgumentException,
            NoResolvedResultException, NonUniqueResultException {
        Validate.notNull(array, "Array must not be null");

        if (array.length == 0) {
            throw new NoResolvedResultException("Unable to resolve dependencies, none of them were found.");
        }
        if (array.length != 1) {

            StringBuilder sb = new StringBuilder();
            for (RETURNTYPE artifact : array) {
                sb.append(artifact).append("\n");
            }
            // delete last two characters
            if (sb.lastIndexOf("\n") != -1) {
                sb.deleteCharAt(sb.length() - 1);
            }

            throw new NonUniqueResultException(
                    MessageFormat
                            .format(
                                    "Resolution resolved more than a single artifact ({0} artifact(s)), unable to determine which one should used.\nComplete list of resolved artifacts:\n{1}"
                                    ,
                                    array.length, sb));
        }

        return array[0];
    }

}