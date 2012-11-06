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

import java.util.Collection;

import org.jboss.shrinkwrap.resolver.api.ResolvedArtifact;
import org.jboss.shrinkwrap.resolver.spi.loader.ServiceRegistry;

/**
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 *
 */
public final class FormatProcessors {

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static <RESOLVEDTYPE extends ResolvedArtifact<RESOLVEDTYPE>, RETURNTYPE> FormatProcessor<? super RESOLVEDTYPE, RETURNTYPE> find(
            final Class<RESOLVEDTYPE> resolvedTypeClass, final Class<RETURNTYPE> returnTypeClass) {

        ServiceRegistry registry = ServiceRegistry.getInstance();
        Collection<FormatProcessor> processors = registry.all(FormatProcessor.class);

        StringBuilder unsupportedFormatMessage = new StringBuilder("No format processor for ")
                .append(returnTypeClass.getName()).append(
                        " was found. Supported processors are: ");

        for (FormatProcessor processor : processors) {
            if (processor.handles(resolvedTypeClass) && processor.returns(returnTypeClass)) {
                return processor;
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
