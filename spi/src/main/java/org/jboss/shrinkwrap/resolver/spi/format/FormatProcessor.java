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
 * <p>
 * Any format processor can be registered via SPI. See {@link ServiceLoader} for further details.
 *
 * @param <RESOLVEDTYPE> The type to be processed
 * @param <RETURNTYPE> The type to be returned
 * Desired format to be returned from the {@link ResolvedArtifact} input in {@link FormatProcessor#process(ResolvedArtifact, Class)}
 *
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 */
public interface FormatProcessor<RESOLVEDTYPE extends ResolvedArtifact<RESOLVEDTYPE>, RETURNTYPE> {

    /**
     * Checks if the processor is able to process {@code resolvedTypeClass}
     *
     * @param resolvedTypeClass The type to be processed
     * @return whether the processor is able to process {@code resolvedTypeClass} or not
     */
    boolean handles(Class<?> resolvedTypeClass);

    /**
     * Checks if the processor is able to return {@code returnTypeClass}.
     *
     * @param returnTypeClass The type to be returned
     * @return whether the processor is able to return {@code returnTypeClass} or not
     */
    boolean returns(Class<?> returnTypeClass);

    /**
     * Processes the specified {@code RESOLVEDTYPE} and returns as the typed return value.
     *
     * @param input The type to be processed
     * @param returnType The type to be returned
     * @return The typed return value.
     * @throws IllegalArgumentException
     * If the {@link RESOLVEDTYPE} argument is not specified or null
     */
    RETURNTYPE process(RESOLVEDTYPE input, Class<RETURNTYPE> returnType) throws IllegalArgumentException;

}
