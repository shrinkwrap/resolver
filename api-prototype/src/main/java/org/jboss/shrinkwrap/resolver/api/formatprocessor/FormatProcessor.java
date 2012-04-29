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
package org.jboss.shrinkwrap.resolver.api.formatprocessor;

import java.io.File;

/**
 * Processes an input {@link File} and returns as a typed format
 *
 * @param <RETURNTYPE>
 *            Desired format to be returned from the {@link File} input in {@link FormatProcessor#process(File)}
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
public interface FormatProcessor<RETURNTYPE> {

    /**
     * Processes the specified {@link File} and returns as the typed return value.
     *
     * @param input
     * @return
     * @throws IllegalArgumentException
     *             If the {@link File} argument is not specified, does not exist, or points to a directory
     */
    RETURNTYPE process(File input) throws IllegalArgumentException;

}
