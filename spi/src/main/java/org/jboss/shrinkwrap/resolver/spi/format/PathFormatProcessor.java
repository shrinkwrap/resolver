/*
 * JBoss, Home of Professional Open Source
 * Copyright 2017, Red Hat, Inc., and individual contributors
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
import java.nio.file.Path;

import org.jboss.shrinkwrap.resolver.api.ResolvedArtifact;

/**
 * {@link FormatProcessor} implementation to return a {@link Path} from the provided {@link ResolvedArtifact} argument.
 * <p>
 * Implementation note: This format processor does not use type parameters to be able to process any type inherited from
 * {@link ResolvedArtifact}.
 *
 * @author Gunnar Morling
 */
@SuppressWarnings("rawtypes")
public enum PathFormatProcessor implements FormatProcessor {

    INSTANCE;

    @Override
    public boolean handles(Class resolvedTypeClass) {
        return FileFormatProcessor.INSTANCE.handles(resolvedTypeClass);
    }

    @Override
    public boolean returns(Class returnTypeClass) {
        return Path.class.equals(returnTypeClass);
    }

    @Override
    public Path process(ResolvedArtifact input, Class returnType) throws IllegalArgumentException {
        if (returnType != Path.class) {
            throw new IllegalArgumentException("Path processor must be called to return Path, not " + returnType);
        }

        return FileFormatProcessor.INSTANCE.process( input, File.class ).toPath();
    }
}
