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
package org.jboss.shrinkwrap.resolver.spi.maven.archive.packaging;

import java.util.Collection;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.resolver.api.maven.PackagingType;
import org.jboss.shrinkwrap.resolver.spi.loader.ServiceRegistry;

public class PackagingProcessors {

    /**
     * Finds the first packaging processor on the classpath that supports give {@code packageType}
     *
     * @param packagingType Package type
     * @return
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static PackagingProcessor<? extends Archive<?>> find(
            final PackagingType packagingType) {

        ServiceRegistry registry = ServiceRegistry.getInstance();
        Collection<PackagingProcessor> processors = registry.all(PackagingProcessor.class);

        StringBuilder unsupportedFormatMessage = new StringBuilder("No packaging processor for ")
                .append(packagingType.toString()).append(
                        " packaging was found. Supported processors are: ");

        for (PackagingProcessor processor : processors) {
            if (processor.handles(packagingType)) {
                // unchecked cast
                return (PackagingProcessor<? extends Archive<?>>) processor;
            }
            unsupportedFormatMessage.append(processor.getClass()).append(", ");
        }
        // trim
        if (unsupportedFormatMessage.indexOf(", ") != -1) {
            unsupportedFormatMessage.delete(unsupportedFormatMessage.length() - 2, unsupportedFormatMessage.length());
        }

        throw new UnsupportedOperationException(unsupportedFormatMessage.toString());
    }

}
