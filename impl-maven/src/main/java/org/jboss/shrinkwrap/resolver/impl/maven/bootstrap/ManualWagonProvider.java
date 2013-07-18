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

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

import org.apache.maven.wagon.Wagon;
import org.apache.maven.wagon.providers.file.FileWagon;
import org.apache.maven.wagon.providers.http.LightweightHttpWagon;
import org.apache.maven.wagon.providers.http.LightweightHttpWagonAuthenticator;
import org.apache.maven.wagon.providers.http.LightweightHttpsWagon;
import org.jboss.shrinkwrap.resolver.api.ResolutionException;
import org.eclipse.aether.connector.wagon.WagonProvider;

/**
 * {@link WagonProvider} implementation using an appropriate {@link Wagon} given the provided roleHint in
 * {@link WagonProvider#lookup(String)}
 *
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 */
class ManualWagonProvider implements WagonProvider {

    private static final String HTTP = "http";
    private static final String HTTPS = "https";
    private static final String FILE = "file";

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.aether.connector.wagon.WagonProvider#lookup(java.lang.String)
     */
    @Override
    public Wagon lookup(final String roleHint) throws Exception {
        if (roleHint.equals(HTTP)) {
            return setAuthenticator(new LightweightHttpWagon());
        } else if (roleHint.equals(HTTPS)) {
            return setAuthenticator(new LightweightHttpsWagon());
        } else if (roleHint.equals(FILE)) {
            return new FileWagon();
        }

        throw new RuntimeException("Role hint not supported: " + roleHint);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.aether.connector.wagon.WagonProvider#release(org.apache.maven.wagon.Wagon)
     */
    @Override
    public void release(final Wagon wagon) {
        // NO-OP
    }

    // SHRINKRES-68
    // Wagon noes not correctly fill Authenticator field if Plexus is not used
    // we need to use reflexion in order to get fix this behavior
    // http://dev.eclipse.org/mhonarc/lists/aether-users/msg00113.html
    private LightweightHttpWagon setAuthenticator(final LightweightHttpWagon wagon) {
        final Field authenticator;
        try {
            authenticator = AccessController.doPrivileged(new PrivilegedExceptionAction<Field>() {
                @Override
                public Field run() throws Exception {
                    final Field field = LightweightHttpWagon.class.getDeclaredField("authenticator");
                    field.setAccessible(true);
                    return field;
                }
            });
        } catch (final PrivilegedActionException pae) {
            throw new ResolutionException("Could not manually set authenticator to accessible on "
                + LightweightHttpWagon.class.getName(), pae);
        }
        try {
            authenticator.set(wagon, new LightweightHttpWagonAuthenticator());
        } catch (final Exception e) {
            throw new ResolutionException("Could not manually set authenticator on "
                + LightweightHttpWagon.class.getName(), e);
        }

        // SHRINKRES-69
        // Needed to ensure that we do not cache BASIC Auth values
        wagon.setPreemptiveAuthentication(true);

        return wagon;
    }
}