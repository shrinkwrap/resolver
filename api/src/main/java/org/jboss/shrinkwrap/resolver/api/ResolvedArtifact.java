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

/**
 * Representation of resolved artifact
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 *
 * @param <RESOLVEDTYPE>
 */
public interface ResolvedArtifact<RESOLVEDTYPE extends ResolvedArtifact<RESOLVEDTYPE>> {

    /**
     * Returns resolved artifact, optionally casted to the sub type of {@link ResolvedArtifact}
     *
     * @return
     */
    RESOLVEDTYPE asResolvedArtifact();

    /**
     * Returns resolved artifact as a {@link File}
     *
     * @return
     */
    File asFile();

    /**
     * Returns resolved artifact as an @{link {@link InputStream}. It is a responsibility of the caller to close stream
     * afterwards.
     *
     * @return
     */
    InputStream asInputStream();

    /**
     * Returns resolved artifact formatted to {@code returnTypeClass}.
     *
     * See {@link FormatProcessor} to register additional format.
     *
     * @param returnTypeClass
     * @return
     * @throws {@link IllegalArgumentException} If the type is not specified
     * @throws {@link UnsupportedOperationException} If the type is not supported
     */
    <RETURNTYPE> RETURNTYPE as(Class<RETURNTYPE> returnTypeClass) throws IllegalArgumentException,
            UnsupportedOperationException;

}
