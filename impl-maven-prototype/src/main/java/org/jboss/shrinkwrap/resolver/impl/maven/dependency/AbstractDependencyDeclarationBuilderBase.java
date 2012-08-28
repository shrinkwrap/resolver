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
package org.jboss.shrinkwrap.resolver.impl.maven.dependency;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.shrinkwrap.resolver.api.CoordinateBuildException;
import org.jboss.shrinkwrap.resolver.api.CoordinateParseException;
import org.jboss.shrinkwrap.resolver.api.maven.MavenFormatStage;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolutionStrategyBase;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolveStageBase;
import org.jboss.shrinkwrap.resolver.api.maven.MavenStrategyStage;
import org.jboss.shrinkwrap.resolver.api.maven.MavenStrategyStageBase;
import org.jboss.shrinkwrap.resolver.api.maven.PackagingType;
import org.jboss.shrinkwrap.resolver.api.maven.ScopeType;
import org.jboss.shrinkwrap.resolver.api.maven.dependency.DependencyDeclaration;
import org.jboss.shrinkwrap.resolver.api.maven.dependency.DependencyDeclarationBuilderBase;
import org.jboss.shrinkwrap.resolver.api.maven.dependency.exclusion.DependencyExclusion;
import org.jboss.shrinkwrap.resolver.api.maven.dependency.exclusion.DependencyExclusionBuilderBase;
import org.jboss.shrinkwrap.resolver.impl.maven.MavenStrategyStageImpl;
import org.jboss.shrinkwrap.resolver.impl.maven.MavenWorkingSession;
import org.jboss.shrinkwrap.resolver.impl.maven.MavenWorkingSessionRetrieval;
import org.jboss.shrinkwrap.resolver.impl.maven.coordinate.MavenCoordinateParser;
import org.jboss.shrinkwrap.resolver.impl.maven.exclusion.DependencyExclusionBuilderImpl;

/**
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 *
 * @param <COORDINATETYPE>
 * @param <COORDINATEBUILDERTYPE>
 * @param <EXCLUSIONBUILDERTYPE>
 * @param <RESOLVESTAGETYPE>
 * @param <MavenStrategyStage>
 * @param <FORMATSTAGETYPE>
 */
abstract class AbstractDependencyDeclarationBuilderBase<COORDINATEBUILDERTYPE extends DependencyDeclarationBuilderBase<COORDINATEBUILDERTYPE, EXCLUSIONBUILDERTYPE, RESOLVESTAGETYPE, STRATEGYSTAGETYPE, FORMATSTAGETYPE, RESOLUTIONSTRATEGYTYPE>, EXCLUSIONBUILDERTYPE extends DependencyExclusionBuilderBase<EXCLUSIONBUILDERTYPE>, RESOLVESTAGETYPE extends MavenResolveStageBase<COORDINATEBUILDERTYPE, EXCLUSIONBUILDERTYPE, RESOLVESTAGETYPE, STRATEGYSTAGETYPE, FORMATSTAGETYPE, RESOLUTIONSTRATEGYTYPE>, STRATEGYSTAGETYPE extends MavenStrategyStageBase<STRATEGYSTAGETYPE, FORMATSTAGETYPE, RESOLUTIONSTRATEGYTYPE>, FORMATSTAGETYPE extends MavenFormatStage, RESOLUTIONSTRATEGYTYPE extends MavenResolutionStrategyBase<RESOLUTIONSTRATEGYTYPE>>
    implements
    DependencyDeclarationBuilderBase<COORDINATEBUILDERTYPE, EXCLUSIONBUILDERTYPE, RESOLVESTAGETYPE, STRATEGYSTAGETYPE, FORMATSTAGETYPE, RESOLUTIONSTRATEGYTYPE>,
    MavenWorkingSessionRetrieval {

    private static final Logger log = Logger.getLogger(AbstractDependencyDeclarationBuilderBase.class.getName());

    protected MavenWorkingSession session;

    protected String groupId;
    protected String artifactId;
    protected ScopeType scope;
    protected PackagingType type;
    protected boolean optional;
    protected String classifier;
    protected String version;

    protected Set<DependencyExclusion> exclusions;

    public AbstractDependencyDeclarationBuilderBase(MavenWorkingSession session) {
        this.session = session;
        this.exclusions = new LinkedHashSet<DependencyExclusion>();
    }

    @Override
    public MavenWorkingSession getMavenWorkingSession() {
        return session;
    }

    @SuppressWarnings("unchecked")
    @Override
    public COORDINATEBUILDERTYPE version(String version) throws IllegalArgumentException {
        this.version = version;
        return (COORDINATEBUILDERTYPE) this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public COORDINATEBUILDERTYPE packaging(PackagingType packagingType) {
        this.type = packagingType;
        return (COORDINATEBUILDERTYPE) this;

    }

    @SuppressWarnings("unchecked")
    @Override
    public COORDINATEBUILDERTYPE type(PackagingType packagingType) {
        this.type = packagingType;
        return (COORDINATEBUILDERTYPE) this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public COORDINATEBUILDERTYPE classifier(String classifier) throws IllegalArgumentException {
        this.classifier = classifier;
        return (COORDINATEBUILDERTYPE) this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public COORDINATEBUILDERTYPE groupId(String groupId) throws IllegalArgumentException {
        Validate.notNullOrEmpty(groupId, "GroupId must not be null nor empty.");
        this.groupId = groupId;
        return (COORDINATEBUILDERTYPE) this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public COORDINATEBUILDERTYPE artifactId(String artifactId) throws IllegalArgumentException {
        Validate.notNullOrEmpty(artifactId, "ArtifactId must not be null nor empty.");
        this.artifactId = artifactId;
        return (COORDINATEBUILDERTYPE) this;
    }

    @Override
    public String getGroupId() {
        return groupId;
    }

    @Override
    public String getArtifactId() {
        return artifactId;
    }

    @Override
    public String getAddress() {
        return groupId + ":" + artifactId;
    }

    @Override
    public PackagingType getPackaging() {
        return type;
    }

    @Override
    public String getType() {
        Validate.stateNotNull(type, "Packaging type must be defined.");
        return type.toString();
    }

    @Override
    public String getClassifier() {
        return classifier;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public COORDINATEBUILDERTYPE and() {
        if (!isFresh()) {
            session.getDependencies().push(build());
        }
        return refreshBuilder();
    }

    @Override
    public COORDINATEBUILDERTYPE and(String coordinate) throws CoordinateParseException, IllegalArgumentException {

        Validate.notNullOrEmpty(coordinate, "Coordinates for a dependency must not be null nor empty.");
        if (!isFresh()) {
            session.getDependencies().push(build());
        }

        // prepare next dependency
        COORDINATEBUILDERTYPE builder = refreshBuilder();
        MavenCoordinateParser parser = MavenCoordinateParser.parse(coordinate);

        builder.groupId(parser.getGroupId());
        builder.artifactId(parser.getArtifactId());
        builder.type(parser.getPackaging());
        builder.classifier(parser.getClassifier());
        builder.version(parser.getVersion());

        return builder;
    }

    @SuppressWarnings("unchecked")
    @Override
    public COORDINATEBUILDERTYPE scope(ScopeType scope) {
        this.scope = scope;
        return (COORDINATEBUILDERTYPE) this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public COORDINATEBUILDERTYPE optional(boolean optional) {
        this.optional = optional;
        return (COORDINATEBUILDERTYPE) this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public COORDINATEBUILDERTYPE addExclusion(String coordinates) throws IllegalArgumentException,
        CoordinateParseException {
        Validate.notNullOrEmpty(coordinates, "Exclusion coordinates must not be null nor empty.");

        exclusions.add(DependencyExclusionBuilderImpl.fromExclusionCoordinateAddress(coordinates).build());
        return (COORDINATEBUILDERTYPE) this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public COORDINATEBUILDERTYPE addExclusions(String... coordinates) throws IllegalArgumentException,
        CoordinateParseException {
        Validate.notNullAndNoNullValues(coordinates, "Exclusions coordinates must not be null nor empty.");
        for (String coords : coordinates) {
            exclusions.add(DependencyExclusionBuilderImpl.fromExclusionCoordinateAddress(coords).build());
        }
        return (COORDINATEBUILDERTYPE) this;
    }

    protected DependencyDeclaration build() throws IllegalArgumentException, CoordinateBuildException {

        Validate.stateNotNull(exclusions,
            "Exclusions set must not be null. This should not happen with fluent API, please file a bug.");

        Validate.notNullOrEmpty(groupId, "GroupId must not be null or empty defined when specifying a <dependency>.");
        Validate.notNullOrEmpty(artifactId,
            "ArtifactId must not be null or empty defined when specifying a <dependency>.");
        // set default packaging
        if (type == null) {
            log.log(Level.FINEST, "Setting packaging type to {0} for dependency {1}:{2}", new Object[] {
                PackagingType.JAR, groupId, artifactId });
            this.type = PackagingType.JAR;
        }
        // set default classifier
        if (classifier == null) {
            log.log(Level.FINEST, "Setting classifier to empty string for dependency {0}:{1}", new Object[] { groupId,
                artifactId });
            this.classifier = "";
        }
        // set default scope
        if (scope == null) {
            log.log(Level.FINEST, "Setting scope to 'compile' for dependency {0}:{1}", new Object[] { groupId,
                artifactId });
            this.scope = ScopeType.COMPILE;
        }

        // resolve version from dependencyManagement
        if (Validate.isNullOrEmpty(version) || MavenCoordinateParser.UNKNOWN_VERSION.equals(version)) {
            version = inferDependencyVersion();
        }
        // create dependency
        return new DependencyDeclarationImpl(groupId, artifactId, type, classifier, version, scope, optional,
            exclusions);
    }

    protected abstract String inferDependencyVersion() throws CoordinateBuildException;

    /**
     * Cleans data stored in the builder, so next round of dependency adding starts with clean environment
     *
     * @return Regenerated builder
     */
    @SuppressWarnings("unchecked")
    protected COORDINATEBUILDERTYPE refreshBuilder() {
        this.groupId = null;
        this.artifactId = null;
        this.scope = null;
        this.type = null;
        this.optional = false;
        this.classifier = null;
        this.version = null;
        this.exclusions = new LinkedHashSet<DependencyExclusion>();
        return (COORDINATEBUILDERTYPE) this;
    }

    // FIXME Java seems not to be able to infer this correctly
    protected MavenStrategyStage resolveInternally() throws CoordinateBuildException {
        if (!isFresh()) {
            session.getDependencies().push(build());
        }
        return new MavenStrategyStageImpl(session);
    }

    /**
     * Checks if a builder is fresh, that is nothing was yet set by user. This allows implementation to distinguish
     * between valid and invalid usage of and(...) call.
     *
     * @return Returns {@code true} if user had not yet provide any input for next dependency, {@code false} otherwise
     */
    protected boolean isFresh() {
        return this.groupId == null && this.artifactId == null && this.scope == null && this.type == null
            && this.optional == false && this.classifier == null && this.version == null && this.exclusions.size() == 0;

    }
}
