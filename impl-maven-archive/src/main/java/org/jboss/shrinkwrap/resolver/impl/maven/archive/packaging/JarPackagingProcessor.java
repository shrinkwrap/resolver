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

import java.util.jar.Manifest;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ExplodedImporter;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.resolver.api.ResolutionException;
import org.jboss.shrinkwrap.resolver.api.maven.MavenWorkingSession;
import org.jboss.shrinkwrap.resolver.api.maven.PackagingType;
import org.jboss.shrinkwrap.resolver.api.maven.ScopeType;
import org.jboss.shrinkwrap.resolver.api.maven.pom.ParsedPomFile;
import org.jboss.shrinkwrap.resolver.api.maven.pom.Resource;
import org.jboss.shrinkwrap.resolver.api.maven.strategy.MavenResolutionStrategy;
import org.jboss.shrinkwrap.resolver.impl.maven.archive.plugins.JarPluginConfiguration;
import org.jboss.shrinkwrap.resolver.impl.maven.util.Validate;
import org.jboss.shrinkwrap.resolver.spi.maven.archive.packaging.PackagingProcessor;

/**
 * Packaging processor for Maven projects of JAR packaging type
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 *
 */
public class JarPackagingProcessor extends AbstractCompilingProcessor<JavaArchive> implements PackagingProcessor<JavaArchive> {

    public static final String MAVEN_WAR_PLUGIN_KEY = "org.apache.maven.plugins:maven-jar-plugin";

    private JavaArchive archive;

    @Override
    public boolean handles(PackagingType packagingType) {
        return PackagingType.JAR.equals(packagingType);
    }

    @Override
    public JarPackagingProcessor configure(Archive<?> archive, MavenWorkingSession session) {
        super.configure(session);
        // archive is ignored so far
        this.archive = ShrinkWrap.create(JavaArchive.class, session.getParsedPomFile().getFinalName());
        return this;
    }

    @Override
    public JarPackagingProcessor importBuildOutput(MavenResolutionStrategy strategy) throws ResolutionException,
            IllegalArgumentException, UnsupportedOperationException {

        final ParsedPomFile pomFile = session.getParsedPomFile();

        // add source filed if any
        if (Validate.isReadable(pomFile.getSourceDirectory())) {
            compile(pomFile.getSourceDirectory(), pomFile.getBuildOutputDirectory(), ScopeType.COMPILE, ScopeType.IMPORT,
                    ScopeType.PROVIDED, ScopeType.RUNTIME, ScopeType.SYSTEM);

            JavaArchive classes = ShrinkWrap.create(ExplodedImporter.class, "sources.jar")
                    .importDirectory(pomFile.getBuildOutputDirectory()).as(JavaArchive.class);

            archive = archive.merge(classes);
        }

        JarPluginConfiguration jarConfiguration = new JarPluginConfiguration(pomFile);

        // add resources
        for (Resource resource : pomFile.getResources()) {
            archive.addAsResource(resource.getSource(), resource.getTargetPath());
        }

        // set manifest
        Manifest manifest = jarConfiguration.getArchiveConfiguration().asManifest();
        archive.setManifest(new ManifestAsset(manifest));

        // construct new archive via applying includes/excludes
        archive = ArchiveFilteringUtils.filterArchiveContent(archive, JavaArchive.class, jarConfiguration.getIncludes(),
                jarConfiguration.getExcludes());

        return this;
    }

    @Override
    public JavaArchive getResultingArchive() {
        return archive;
    }

}
