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
package org.jboss.shrinkwrap.resolver.api.maven.archive.assembler;

import org.jboss.shrinkwrap.api.Assignable;
import org.jboss.shrinkwrap.resolver.api.maven.ScopeType;
import org.jboss.shrinkwrap.resolver.api.maven.filter.MavenResolutionFilter;

/**
 * Instance of {@link ArchiveMavenAssembler} that has configuration from a POM file loaded
 *
 * @author <a href="mailto:mjobanek@redhat.com">Matous Jobanek</a>
 * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
 */
public interface PomEquippedArchiveMavenAssembler extends Assignable {

    /**
     * Based on metadata previously loaded from a Project Object Model file, adds content made during package phase to
     * the archive. The content is compiled using dependencies with following scopes:
     * {@link ScopeType#COMPILE}, {@link ScopeType#IMPORT}, {@link ScopeType#PROVIDED}, {@link ScopeType#RUNTIME},
     * {@link ScopeType#SYSTEM} - there isn't used the scope {@link ScopeType#TEST} as it simulates Maven package build.
     * <p>
     * If you want to use all scopes, then use this method: {@code withBuildOutput(ScopeType.values())}
     * </p>
     *
     * @return This modified {@link ArchiveMavenAssembler} instance with added content based on metadata previously loaded
     * from a Project Object Model file.
     */
    PomEquippedArchiveMavenAssembler withBuildOutput();

    /**
     * Based on metadata previously loaded from a Project Object Model file, adds content (classes and resources) made
     * during package phase to the archive.
     * The content is compiled using dependencies with the set of scopes given by the parameter
     * <p>
     * If you want to use all scopes, then use: {@code withBuildOutput(ScopeType.values())}
     * </p>
     *
     * @return This modified {@link ArchiveMavenAssembler} instance with added content based on metadata previously loaded
     * from a Project Object Model file.
     */
    PomEquippedArchiveMavenAssembler withBuildOutput(ScopeType... scopes);

    /**
     * Based on metadata previously loaded from a Project Object Model file, adds test content (test classes and test resources)
     * made during package phase to the archive.
     * The content is compiled using dependencies with all scopes:
     * {@link ScopeType#COMPILE}, {@link ScopeType#IMPORT}, {@link ScopeType#PROVIDED}, {@link ScopeType#RUNTIME},
     * {@link ScopeType#SYSTEM}, {@link ScopeType#TEST}
     * <p>
     * If you want to limit the set of used scopes, then use this method: {@link #withTestBuildOutput(ScopeType...)}
     * </p>
     *
     * @return This modified {@link ArchiveMavenAssembler} instance with added test content based on metadata previously loaded
     * from a Project Object Model file.
     */
    PomEquippedArchiveMavenAssembler withTestBuildOutput();

    /**
     * Based on metadata previously loaded from a Project Object Model file, adds test content (test classes and test resources)
     * made during package phase to the archive.
     * The content is compiled using dependencies with the set of scopes given by the parameter
     * <p>
     * If you want to use all scopes, then use: {@code withTestBuildOutput()}
     * </p>
     *
     * @return This modified {@link ArchiveMavenAssembler} instance with added test content based on metadata previously loaded
     * from a Project Object Model file.
     */
    PomEquippedArchiveMavenAssembler withTestBuildOutput(ScopeType... scopes);

    /**
     * Ads dependencies defined in  previously loaded Project Object Model file.
     * The set of dependencies can be limited by {@link MavenResolutionFilter} and set of {@link ScopeType}s.
     * <p>
     * If you want to use all possible scopes, then use this method: {@link #withDependencies(MavenResolutionFilter)}
     * </p>
     * <p>
     * If you don't want to specify {@link MavenResolutionFilter} and limit it only by set of {@link ScopeType}s,
     * then use this method: {@link #withDependencies(ScopeType...)}
     * </p>
     * <p>
     * If you don't want to limit anything and use all defined dependencies, then use this method: {@link #withDependencies()}
     * </p>
     *
     * @return This modified {@link ArchiveMavenAssembler} instance with added dependencies limited by {@link MavenResolutionFilter} and set of {@link ScopeType}s
     * @param filter A {@link MavenResolutionFilter} the added dependencies should be limited by
     * @param scopes A set of {@link ScopeType}s the added dependencies should be limited by
     */
    PomEquippedArchiveMavenAssembler withDependencies(MavenResolutionFilter filter, ScopeType... scopes);

    /**
     * Ads dependencies defined in  previously loaded Project Object Model file. All scopes will be used.
     * The set of dependencies can be limited by {@link MavenResolutionFilter}.
     * <p>
     * If you want to limit the set of dependencies also by scopes, then use this method: {@link #withDependencies(MavenResolutionFilter, ScopeType...)}
     * </p>
     * <p>
     * If you don't want to specify {@link MavenResolutionFilter} and limit it only by set of scopes,
     * then use this method: {@link #withDependencies(ScopeType...)}
     * </p>
     * <p>
     * If you don't want to limit anything and use all defined dependencies, then use this method: {@link #withDependencies()}
     * </p>
     *
     * @return This modified {@link ArchiveMavenAssembler} instance with added dependencies limited by {@link MavenResolutionFilter}
     * @param filter A {@link MavenResolutionFilter} the added dependencies should be limited by
     */
    PomEquippedArchiveMavenAssembler withDependencies(MavenResolutionFilter filter);

    /**
     * Ads dependencies defined in  previously loaded Project Object Model file.
     * The set of dependencies can be limited by set of {@link ScopeType}s.
     * <p>
     * If you want to limit the set of dependencies also by scopes, then use this method: {@link #withDependencies(MavenResolutionFilter, ScopeType...)}
     * </p>
     * <p>
     * If you don't want to specify {@link MavenResolutionFilter} and limit it only by set of scopes,
     * then use this method: {@link #withDependencies(ScopeType...)}
     * </p>
     * <p>
     * If you don't want to limit anything and use all defined dependencies, then use this method: {@link #withDependencies()}
     * </p>
     *
     * @return This modified {@link ArchiveMavenAssembler} instance with added dependencies limited by a set of {@link ScopeType}s
     * @param scopes A set of {@link ScopeType}s the added dependencies should be limited by
     */
    PomEquippedArchiveMavenAssembler withDependencies(ScopeType... scopes);

    /**
     * Ads all dependencies defined in  previously loaded Project Object Model file.
     * The set of dependencies can be limited by {@link MavenResolutionFilter} and set of {@link ScopeType}s.
     * <p>
     * If you want to limit the set of added dependencies, use any of these methods: <br>
     * {@link #withDependencies(ScopeType...)}<br>
     * {@link #withDependencies(MavenResolutionFilter, ScopeType...)}<br>
     * {@link #withDependencies(MavenResolutionFilter)}
     * </p>
     *
     * @return This modified {@link ArchiveMavenAssembler} instance with added dependencies

     */
    PomEquippedArchiveMavenAssembler withDependencies();

    <TYPE extends Assignable> TYPE as(Class<TYPE> var1, String name);

}
