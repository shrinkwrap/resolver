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
package org.jboss.shrinkwrap.resolver.impl.maven.bootstrap;

import org.apache.maven.wagon.Wagon;
import org.apache.maven.wagon.providers.file.FileWagon;
import org.apache.maven.wagon.providers.http.LightweightHttpWagon;
import org.apache.maven.wagon.providers.http.LightweightHttpsWagon;
import org.sonatype.aether.connector.wagon.WagonProvider;

/**
 * {@link WagonProvider} implementation using an appropriate {@link Wagon} given the provided roleHint in
 * {@link WagonProvider#lookup(String)}
 *
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
class ManualWagonProvider implements WagonProvider {

    private static final String HTTP = "http";
    private static final String HTTPS = "https";
    private static final String FILE = "file";

    /**
     * {@inheritDoc}
     *
     * @see org.sonatype.aether.connector.wagon.WagonProvider#lookup(java.lang.String)
     */
    @Override
    public Wagon lookup(final String roleHint) throws Exception {
        if (roleHint.equals(HTTP)) {
            return new LightweightHttpWagon();
        } else if (roleHint.equals(HTTPS)) {
            return new LightweightHttpsWagon();
        } else if (roleHint.equals(FILE)) {
            return new FileWagon();
        }

        throw new RuntimeException("Role hint not supported: " + roleHint);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.sonatype.aether.connector.wagon.WagonProvider#release(org.apache.maven.wagon.Wagon)
     */
    @Override
    public void release(final Wagon wagon) {
        // NO-OP
    }

}