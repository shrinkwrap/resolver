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
package org.jboss.shrinkwrap.resolver.spi.format;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.jboss.shrinkwrap.resolver.api.ResolvedArtifact;

/**
 * {@link FormatProcessor} implementation to return an {@link URL} from the provided {@link ResolvedArtifact} argument.
 *
 * Implementation note: This format processor does not use type parameters to be able to process any type inherited from
 * {@link ResolvedAritifact}.
 *
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 *
 */
@SuppressWarnings("rawtypes")
public enum URLFormatProcessor implements FormatProcessor {
    INSTANCE;

    @Override
    public URL process(ResolvedArtifact artifact, Class returnType) throws IllegalArgumentException {
        if (returnType.getClass() == null || URL.class.equals(returnType.getClass())) {
            throw new IllegalArgumentException("URL processor must be called to return URL, not "
                    + (returnType == null ? "null" : returnType.getClass()));
        }
        if (artifact == null) {
            throw new IllegalArgumentException("Resolution artifact must be specified");
        }
        File file = artifact.asFile();
        if (file == null) {
            throw new IllegalArgumentException("Artifact was not resolved");
        }

        if (!file.exists()) {
            throw new IllegalArgumentException("input file does not exist: " + file.getAbsolutePath());
        }
        if (file.isDirectory()) {
            throw new IllegalArgumentException("input file is a directory: " + file.getAbsolutePath());
        }
        try {
            return file.toURI().toURL();
        } catch(MalformedURLException e) { // Should not happen
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public boolean handles(Class resolvedTypeClass) {
        return ResolvedArtifact.class.isAssignableFrom(resolvedTypeClass);
    }

    @Override
    public boolean returns(Class returnTypeClass) {
        return URL.class.equals(returnTypeClass);
    }
}
