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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.shrinkwrap.resolver.api.ResolutionException;
import org.jboss.shrinkwrap.resolver.api.maven.MavenFormatStage;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolutionFilter;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolutionStrategy;
import org.jboss.shrinkwrap.resolver.api.maven.MavenStrategyStageBase;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStageBase;
import org.jboss.shrinkwrap.resolver.api.maven.ScopeType;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependency;
import org.jboss.shrinkwrap.resolver.impl.maven.convert.MavenConverter;
import org.jboss.shrinkwrap.resolver.impl.maven.filter.ScopeFilter;
import org.jboss.shrinkwrap.resolver.impl.maven.strategy.AcceptAllStrategy;
import org.jboss.shrinkwrap.resolver.impl.maven.strategy.AcceptScopesStrategy;
import org.jboss.shrinkwrap.resolver.impl.maven.strategy.CombinedStrategy;
import org.jboss.shrinkwrap.resolver.impl.maven.util.Validate;
import org.sonatype.aether.artifact.ArtifactTypeRegistry;

/**
 * Base support for implementations of a {@link PomEquippedResolveStage}
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
public abstract class PomEquippedResolveStageBaseImpl<EQUIPPEDRESOLVESTAGETYPE extends PomEquippedResolveStageBase<EQUIPPEDRESOLVESTAGETYPE, STRATEGYSTAGETYPE, FORMATSTAGETYPE>, STRATEGYSTAGETYPE extends MavenStrategyStageBase<STRATEGYSTAGETYPE, FORMATSTAGETYPE>, FORMATSTAGETYPE extends MavenFormatStage>
    extends ResolveStageBaseImpl<EQUIPPEDRESOLVESTAGETYPE, STRATEGYSTAGETYPE, FORMATSTAGETYPE> implements
    PomEquippedResolveStageBase<EQUIPPEDRESOLVESTAGETYPE, STRATEGYSTAGETYPE, FORMATSTAGETYPE> {

    private static final Logger log = Logger.getLogger(PomEquippedResolveStageBaseImpl.class.getName());

    public PomEquippedResolveStageBaseImpl(final MavenWorkingSession session) {
        super(session);

        ArtifactTypeRegistry stereotypes = session.getArtifactTypeRegistry();

        Validate.stateNotNull(session.getModel(),
            "Could not spawn ConfiguredResolveStage. An effective POM must be resolved first.");

        // store all dependency information to be able to retrieve versions later
        if (session.getModel().getDependencyManagement() != null) {
            Set<MavenDependency> pomDependencyMngmt = MavenConverter.fromDependencies(session.getModel()
                .getDependencyManagement().getDependencies(), stereotypes);
            session.getDependencyManagement().addAll(pomDependencyMngmt);
        }

        // store all of the <dependencies> into depMgmt and explicitly-declared dependencies
        final Set<MavenDependency> pomDefinedDependencies = MavenConverter.fromDependencies(session.getModel()
            .getDependencies(), stereotypes);
        session.getDeclaredDependencies().addAll(pomDefinedDependencies);
        session.getDependencyManagement().addAll(pomDefinedDependencies);

    }

    @Override
    public final FORMATSTAGETYPE importRuntimeAndTestDependencies() {
        addScopedDependencies(ScopeType.values());
        return importAnyDependencies(AcceptAllStrategy.INSTANCE);
    }

    @Override
    public final FORMATSTAGETYPE importRuntimeAndTestDependencies(final MavenResolutionStrategy strategy)
        throws IllegalArgumentException {

        Validate.notNull(strategy, "Specified strategy for importing test dependencies must not be null");

        addScopedDependencies(ScopeType.values());
        return importAnyDependencies(strategy);
    }

    @Override
    public final FORMATSTAGETYPE importRuntimeDependencies() {

        ScopeType[] scopes = new ScopeType[] { ScopeType.COMPILE, ScopeType.IMPORT, ScopeType.RUNTIME, ScopeType.SYSTEM };

        addScopedDependencies(scopes);
        return importAnyDependencies(new AcceptScopesStrategy(scopes));
    }

    @Override
    public final FORMATSTAGETYPE importRuntimeDependencies(final MavenResolutionStrategy strategy)
        throws IllegalArgumentException {

        Validate.notNull(strategy, "Specified strategy for importing test dependencies must not be null");

        ScopeType[] scopes = new ScopeType[] { ScopeType.COMPILE, ScopeType.IMPORT, ScopeType.RUNTIME, ScopeType.SYSTEM };

        addScopedDependencies(scopes);
        return importAnyDependencies(new CombinedStrategy(new AcceptScopesStrategy(scopes), strategy));
    }

    private FORMATSTAGETYPE importAnyDependencies(final MavenResolutionStrategy strategy) {
        // resolve

        return this.createStrategyStage().using(strategy);

    }

    private void addScopedDependencies(final ScopeType... scopes) {

        // Get all declared dependencies
        final MavenWorkingSession session = this.getMavenWorkingSession();
        final List<MavenDependency> dependencies = new ArrayList<MavenDependency>(session.getDeclaredDependencies());

        // Filter by scope
        final MavenResolutionFilter preResolutionFilter = new ScopeFilter(scopes);

        // For all declared dependencies which pass the filter, add 'em to the Set of dependencies to be resolved for
        // this request
        for (final MavenDependency candidate : dependencies) {
            if (preResolutionFilter.accepts(candidate)) {
                session.getDependencies().add(candidate);
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.resolver.impl.maven.ResolveStageBaseImpl#resolveVersion(org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenCoordinate)
     */
    @Override
    protected String resolveVersion(final MavenDependency dependency) throws IllegalArgumentException {

        final String declaredVersion = dependency.getVersion();
        String resolvedVersion = declaredVersion;
        final MavenWorkingSession session = this.getMavenWorkingSession();
        // is not able to infer anything, it was not configured
        if (Validate.isNullOrEmpty(resolvedVersion)) {

            // version is ignore here, so we have to iterate to get the dependency we are looking for
            if (session.getDependencyManagement().contains(dependency)) {

                // get the dependency from internal dependencyManagement
                MavenDependency resolved = null;
                Iterator<MavenDependency> it = session.getDependencyManagement().iterator();
                while (it.hasNext()) {
                    resolved = it.next();
                    if (resolved.equals(dependency)) {
                        break;
                    }
                }
                // we have resolved a version from dependency management
                resolvedVersion = resolved.getVersion();
                log.log(Level.FINE, "Resolved version {} from the POM file for the artifact {}", new Object[] {
                    resolved.getVersion(), dependency.toCanonicalForm() });

            }
        }

        // Still unresolved?
        if (Validate.isNullOrEmpty(resolvedVersion)) {

            // log available version management
            if (log.isLoggable(Level.FINER)) {
                StringBuilder sb = new StringBuilder("Available version management: \n");
                for (final MavenDependency depmgmt : session.getDependencyManagement()) {
                    sb.append(depmgmt).append("\n");
                }
                log.log(Level.FINER, sb.toString());
            }

            throw new ResolutionException(
                MessageFormat
                    .format(
                        "Unable to get version for dependency specified by {0}, it was not provided in <dependencyManagement> section.",
                        dependency.toCanonicalForm()));
        }

        // Return
        return resolvedVersion;
    }

}
