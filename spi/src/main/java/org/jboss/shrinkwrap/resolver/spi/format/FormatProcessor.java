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

import org.jboss.shrinkwrap.resolver.api.ResolvedArtifact;
import org.jboss.shrinkwrap.resolver.spi.loader.ServiceLoader;

/**
 * Processes an input {@link ResolvedArtifact} and returns as a typed format.
 *
 * Any format processor can be registered via SPI. See {@link ServiceLoader} for further details.
 *
 * @param <RESOLVEDTYPE>
 * @param <RETURNTYPE>
 * Desired format to be returned from the {@link ResolvedArtifact} input in {@link FormatProcessor#process(File, Class))}
 *
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 */
public interface FormatProcessor<RESOLVEDTYPE extends ResolvedArtifact<RESOLVEDTYPE>, RETURNTYPE> {

    /**
     * Checks if the processor is able to process {@code RESOLVEDTYPE}
     *
     * @param resolvedTypeClass
     * @return
     */
    boolean handles(Class<?> resolvedTypeClass);

    /**
     * Checks if the processor is able to return {@code returnTypeClass}.
     *
     * @param returnTypeClass
     * @return
     */
    boolean returns(Class<?> returnTypeClass);

    /**
     * Processes the specified {@code RESOLVEDTYPE} and returns as the typed return value.
     *
     * @param input
     * @param returnType
     * @param <RESOLVEDTYPE>
     * @return
     * @throws IllegalArgumentException
     * If the {@link RESOLVEDTYPE} argument is not specified or null
     */
    RETURNTYPE process(RESOLVEDTYPE input, Class<RETURNTYPE> returnType) throws IllegalArgumentException;

}
