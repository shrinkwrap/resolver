/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014, Red Hat Middleware LLC, and individual contributors
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
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenCoordinate;
import org.jboss.shrinkwrap.resolver.spi.format.FormatProcessor;

/**
 * A format processor which returns {@link MavenCoordinate}. {@link MavenCoordinate} can be used for instance
 * to compare resolved artifacts of two ShrinkWrap Resolver runs
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 */
public class MavenCoordinateProcessor implements FormatProcessor<MavenResolvedArtifact, MavenCoordinate> {

    @Override
    public boolean handles(Class<?> resolvedTypeClass) {
        return MavenResolvedArtifact.class.isAssignableFrom(resolvedTypeClass);
    }

    @Override
    public boolean returns(Class<?> returnTypeClass) {
        return MavenCoordinate.class.equals(returnTypeClass);
    }

    @Override
    public MavenCoordinate process(MavenResolvedArtifact input, Class<MavenCoordinate> returnType)
            throws IllegalArgumentException {
        if (input == null) {
            throw new IllegalArgumentException("Resolved artifact must not be null");
        }
        return input.getCoordinate();
    }

}
