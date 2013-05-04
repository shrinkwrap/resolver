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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.codehaus.plexus.compiler.CompilerConfiguration;
import org.codehaus.plexus.compiler.CompilerException;
import org.codehaus.plexus.compiler.CompilerMessage;
import org.codehaus.plexus.compiler.CompilerResult;
import org.codehaus.plexus.compiler.javac.JavacCompiler;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolvedArtifact;
import org.jboss.shrinkwrap.resolver.api.maven.MavenWorkingSession;
import org.jboss.shrinkwrap.resolver.api.maven.ScopeType;
import org.jboss.shrinkwrap.resolver.api.maven.archive.importer.MavenImporterException;
import org.jboss.shrinkwrap.resolver.api.maven.strategy.AcceptScopesStrategy;
import org.jboss.shrinkwrap.resolver.api.maven.strategy.MavenResolutionStrategy;
import org.jboss.shrinkwrap.resolver.impl.maven.archive.plugins.CompilerPluginConfiguration;
import org.jboss.shrinkwrap.resolver.impl.maven.task.AddScopedDependenciesTask;
import org.jboss.shrinkwrap.resolver.impl.maven.util.Validate;
import org.jboss.shrinkwrap.resolver.spi.maven.archive.packaging.PackagingProcessor;

/**
 * Packaging processor which is able to compile Java sources
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 *
 * @param <ARCHIVETYPE> Type of the archive produced
 */
public abstract class AbstractCompilingProcessor<ARCHIVETYPE extends Archive<ARCHIVETYPE>> implements
        PackagingProcessor<ARCHIVETYPE> {
    private static final Logger log = Logger.getLogger(AbstractCompilingProcessor.class.getName());

    protected MavenWorkingSession session;

    protected PackagingProcessor<ARCHIVETYPE> configure(MavenWorkingSession session) {
        this.session = session;
        return this;
    }

    protected AbstractCompilingProcessor<ARCHIVETYPE> compile(File inputDirectory, File outputDirectory, ScopeType... scopes) {

        Validate.notNullAndNoNullValues(scopes, "Cannot compile sources, there were no scopes defined");
        Validate.notNull(inputDirectory, "Directory with sources to be compiled must not be null");
        Validate.notNull(outputDirectory, "Target directory for compiled sources must not be null");

        JavacCompiler compiler = new JavacCompiler();
        CompilerConfiguration configuration = getCompilerConfiguration();

        if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, "Compiling sources from {0} directory into {1}",
                    new Object[] { inputDirectory, outputDirectory });
        }

        // in order to compile sources, we need to resolve dependencies first
        // so we have a classpath available
        new AddScopedDependenciesTask(ScopeType.values()).execute(session);
        final MavenResolutionStrategy scopeStrategy = new AcceptScopesStrategy(scopes);
        final Collection<MavenResolvedArtifact> artifactResults = session.resolveDependencies(scopeStrategy);

        for (MavenResolvedArtifact artifact : artifactResults) {
            String classpathEntry = artifact.asFile().getAbsolutePath();
            configuration.addClasspathEntry(classpathEntry);
            if (log.isLoggable(Level.FINER)) {
                log.log(Level.FINER, "Adding {0} to compilation classpath", classpathEntry);
            }
        }

        configuration.addSourceLocation(inputDirectory.getPath());
        configuration.setOutputLocation(outputDirectory.getPath());
        try {
            CompilerResult result = compiler.performCompile(configuration);
            if (!result.isSuccess()) {
                throw constructCompilationException(result, inputDirectory);
            }
        } catch (CompilerException e) {
            throw new MavenImporterException("Unable to compile source at " + inputDirectory.getPath() + " due to: ", e);
        }

        return this;
    }

    private CompilerConfiguration getCompilerConfiguration() {
        CompilerPluginConfiguration pluginConfiguration = new CompilerPluginConfiguration(session.getParsedPomFile());
        CompilerConfiguration configuration = new CompilerConfiguration();

        configuration.setVerbose(pluginConfiguration.isVerbose());
        configuration.setSourceVersion(pluginConfiguration.getSourceVersion());
        configuration.setTargetVersion(pluginConfiguration.getTargetVersion());

        // FIXME this should be handled better
        configuration.setWorkingDirectory(new File("."));

        return configuration;
    }

    private static MavenImporterException constructCompilationException(CompilerResult result, File sourceDirectory) {
        StringBuilder sb = new StringBuilder("Unable to compile sources at ");
        sb.append(sourceDirectory.getPath());
        sb.append(" due to following reason(s): ");

        for (CompilerMessage m : result.getCompilerMessages()) {
            sb.append(m.toString());
            sb.append(", ");
        }
        // trim
        if (sb.indexOf(", ") != -1) {
            sb.delete(sb.length() - 2, sb.length());
        }

        return new MavenImporterException(sb.toString());
    }

}
