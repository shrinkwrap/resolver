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
package org.jboss.shrinkwrap.resolver.impl.maven.archive.packaging;

import java.io.File;
import java.util.Collection;
import java.util.Map;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ExplodedImporter;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolvedArtifact;
import org.jboss.shrinkwrap.resolver.api.maven.MavenWorkingSession;
import org.jboss.shrinkwrap.resolver.api.maven.PackagingType;
import org.jboss.shrinkwrap.resolver.api.maven.ScopeType;
import org.jboss.shrinkwrap.resolver.api.maven.pom.ParsedPomFile;
import org.jboss.shrinkwrap.resolver.api.maven.strategy.MavenResolutionStrategy;
import org.jboss.shrinkwrap.resolver.impl.maven.task.AddAllDeclaredDependenciesTask;
import org.jboss.shrinkwrap.resolver.impl.maven.util.Validate;
import org.jboss.shrinkwrap.resolver.spi.maven.archive.packaging.PackagingProcessor;

/**
 * Packaging processor for Maven projects of WAR packaging type
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 *
 */
public class WarPackagingProcessor extends AbstractCompilingProcessor<WebArchive> implements PackagingProcessor<WebArchive> {
    // private static final Logger log = Logger.getLogger(WarPackagingProcessor.class.getName());

    public static final String MAVEN_WAR_PLUGIN_KEY = "org.apache.maven.plugins:maven-war-plugin";

    private WebArchive archive;

    @Override
    public boolean handles(PackagingType packagingType) {
        return PackagingType.WAR.equals(packagingType);
    }

    @Override
    public WarPackagingProcessor configure(Archive<?> archive, MavenWorkingSession session) {
        super.configure(session);

        // archive is ignored so far
        this.archive = ShrinkWrap.create(WebArchive.class, session.getParsedPomFile().getFinalName());
        return this;
    }

    @Override
    public WebArchive getResultingArchive() {
        return archive;
    }

    @Override
    public WarPackagingProcessor importBuildOutput(MavenResolutionStrategy strategy) throws IllegalArgumentException,
            UnsupportedOperationException {

        final ParsedPomFile pomFile = session.getParsedPomFile();

        // add source filed if any
        if (Validate.isReadable(pomFile.getSourceDirectory())) {
            compile(pomFile.getSourceDirectory(), pomFile.getBuildOutputDirectory(),
                    ScopeType.COMPILE, ScopeType.IMPORT, ScopeType.PROVIDED, ScopeType.RUNTIME,
                    ScopeType.SYSTEM);

            JavaArchive classes = ShrinkWrap.create(ExplodedImporter.class, "sources.jar")
                    .importDirectory(pomFile.getBuildOutputDirectory())
                    .as(JavaArchive.class);

            archive = archive.merge(classes, "WEB-INF/classes");
        }

        // add resources
        for (File resource : pomFile.getProjectResources()) {
            archive.addAsResource(resource);
        }

        // add war content
        Map<String, Object> warConfiguration = pomFile.getPluginConfiguration(MAVEN_WAR_PLUGIN_KEY);
        File warSourceDirectory = null;
        if (warConfiguration.isEmpty() || !warConfiguration.containsKey("warSourceDirectory")) {
            warSourceDirectory = new File(pomFile.getBaseDirectory(), "src/main/webapp");
        }
        else {
            warSourceDirectory = new File(warConfiguration.get("warSourceDirectory").toString());
        }

        if (Validate.isReadable(warSourceDirectory)) {
            WebArchive webapp = ShrinkWrap.create(ExplodedImporter.class, "webapp.war")
                    .importDirectory(warSourceDirectory)
                    .as(WebArchive.class);

            archive = archive.merge(webapp);
        }

        // add dependencies
        this.session = AddAllDeclaredDependenciesTask.INSTANCE.execute(session);
        final Collection<MavenResolvedArtifact> artifacts = session.resolveDependencies(strategy);
        for (MavenResolvedArtifact artifact : artifacts) {
            archive.addAsLibrary(artifact.asFile());
        }

        return this;
    }
}
