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
package org.jboss.shrinkwrap.resolver.impl.maven;

import org.jboss.shrinkwrap.resolver.api.maven.ConfigurableMavenResolverSystem;
import org.jboss.shrinkwrap.resolver.api.maven.MavenFormatStage;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolverSystem;
import org.jboss.shrinkwrap.resolver.api.maven.MavenStrategyStage;
import org.jboss.shrinkwrap.resolver.api.maven.MavenWorkingSession;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;
import org.jboss.shrinkwrap.resolver.api.maven.PomlessResolveStage;

/**
 * Implementation of {@link MavenResolverSystem}
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
public class MavenResolverSystemImpl
    extends
    MavenResolverSystemBaseImpl<MavenResolverSystem, ConfigurableMavenResolverSystem, PomEquippedResolveStage, PomlessResolveStage, MavenStrategyStage, MavenFormatStage>
    implements MavenResolverSystem, MavenWorkingSessionContainer {

    /**
     * {@inheritDoc}
     *
     */
    public MavenResolverSystemImpl() throws IllegalArgumentException {
        this(new PomlessResolveStageImpl(new MavenWorkingSessionImpl()));
    }

    /**
     * {@inheritDoc}
     *
     * @param delegate A delegate
     * @throws IllegalArgumentException
     * If the {@code delegate} is either null or doesn't implement the {@link MavenWorkingSessionContainer}
     */
    public MavenResolverSystemImpl(final PomlessResolveStage delegate) throws IllegalArgumentException {
        super(delegate);
    }

    /**
     * {@inheritDoc}
     *
     * @return The {@link MavenWorkingSession} associated with this {@link MavenResolverSystem}
     */
    @Override
    public MavenWorkingSession getMavenWorkingSession() {
        return super.getSession();
    }

}
