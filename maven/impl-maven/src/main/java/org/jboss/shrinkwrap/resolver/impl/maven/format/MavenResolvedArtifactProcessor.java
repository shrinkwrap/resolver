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
package org.jboss.shrinkwrap.resolver.impl.maven.format;

import org.jboss.shrinkwrap.resolver.api.maven.MavenResolvedArtifact;
import org.jboss.shrinkwrap.resolver.spi.format.FormatProcessor;

/**
 * A format processor which returns {@link MavenResolvedArtifact}. As {@link MavenResolvedArtifact} is the default format for
 * Maven resolution, this is a no-op format processor.
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 */
public class MavenResolvedArtifactProcessor implements FormatProcessor<MavenResolvedArtifact, MavenResolvedArtifact> {

    @Override
    public boolean handles(final Class<?> resolvedTypeClass) {
        return MavenResolvedArtifact.class.isAssignableFrom(resolvedTypeClass);
    }

    @Override
    public boolean returns(final Class<?> returnTypeClass) {
        return MavenResolvedArtifact.class.equals(returnTypeClass);
    }

    @Override
    public MavenResolvedArtifact process(final MavenResolvedArtifact input, final Class<MavenResolvedArtifact> returnType)
            throws IllegalArgumentException {
        // no-op
        return input;
    }

}
