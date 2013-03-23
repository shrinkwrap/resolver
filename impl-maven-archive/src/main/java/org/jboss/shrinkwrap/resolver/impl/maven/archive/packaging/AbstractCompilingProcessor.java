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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.codehaus.plexus.compiler.CompilerConfiguration;
import org.codehaus.plexus.compiler.CompilerException;
import org.codehaus.plexus.compiler.CompilerMessage;
import org.codehaus.plexus.compiler.CompilerResult;
import org.codehaus.plexus.compiler.javac.JavacCompiler;
import org.codehaus.plexus.util.DirectoryScanner;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolvedArtifact;
import org.jboss.shrinkwrap.resolver.api.maven.MavenWorkingSession;
import org.jboss.shrinkwrap.resolver.api.maven.ScopeType;
import org.jboss.shrinkwrap.resolver.api.maven.archive.importer.MavenImporterException;
import org.jboss.shrinkwrap.resolver.api.maven.strategy.AcceptScopesStrategy;
import org.jboss.shrinkwrap.resolver.api.maven.strategy.MavenResolutionStrategy;
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

    public static final String MAVEN_COMPILER_PLUGIN_KEY = "org.apache.maven.plugins:maven-compiler-plugin";
    public static final String MAVEN_COMPILER_SOURCE_VERSION = "1.5";
    public static final String MAVEN_COMPILER_TARGET_VERSION = "1.5";

    public static final String[] DEFAULT_INCLUDES = {"**/**"};

    protected MavenWorkingSession session;

    protected static void addTokenized(Map<String, Object> warConfiguration, ArrayList<String> excludes, String configurationKey) {
        final Object packagingExcludes = warConfiguration.get(configurationKey);
        addTokenized(excludes, packagingExcludes);
    }

    protected static void addTokenized(List<String> excludes, Object element) {
        if (element != null) {
            final StringTokenizer tokenizer = new StringTokenizer(element.toString(), ",");
            while (tokenizer.hasMoreElements()) {
                excludes.add(tokenizer.nextToken());
            }
        }
    }

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
        CompilerConfiguration configuration = new CompilerConfiguration();
        Map<String, Object> map = session.getParsedPomFile().getPluginConfiguration(
                "org.apache.maven.plugins:maven-compiler-plugin");

        // if Maven Compiler Plugin Configuration is empty, set the defaults
        if (map.isEmpty()) {
            configuration.setSourceVersion(MAVEN_COMPILER_SOURCE_VERSION);
            configuration.setTargetVersion(MAVEN_COMPILER_TARGET_VERSION);
        }

        // FIXME this should be handled better
        configuration.setWorkingDirectory(new File("."));

        // TODO include more compiler plugin configuration values
        if (map.containsKey("verbose")) {
            configuration.setVerbose(Boolean.parseBoolean(map.get("verbose").toString()));
        }

        // ensure default value are set if empty
        if (Validate.isNullOrEmpty(configuration.getSourceVersion())) {
            configuration.setSourceVersion(MAVEN_COMPILER_SOURCE_VERSION);
        }
        if (Validate.isNullOrEmpty(configuration.getTargetVersion())) {
            configuration.setTargetVersion(MAVEN_COMPILER_TARGET_VERSION);
        }

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

    protected abstract String[] getExcludes(Map<String, Object> configuration);

    protected abstract String[] getIncludes(Map<String, Object> configuration);

    /**
     * Returns the file to copy. If the includes are <tt>null</tt> or empty, the
     * default includes are used.
     *
     * @param baseDir  the base directory to start from
     * @param includes the includes
     * @param excludes the excludes
     * @return the files to copy
     */
    protected String[] getFilesToIncludes(File baseDir, String[] includes, String[] excludes) {
        final DirectoryScanner scanner = new DirectoryScanner();
        scanner.setBasedir(baseDir);

        if (excludes != null) {
            scanner.setExcludes(excludes);
        }
        scanner.addDefaultExcludes();

        if (includes != null && includes.length > 0) {
            scanner.setIncludes(includes);
        } else {
            scanner.setIncludes(DEFAULT_INCLUDES);
        }

        scanner.scan();

        final String[] includedFiles = scanner.getIncludedFiles();
        for (int i = 0; i < includedFiles.length; i++) {
            includedFiles[i] = "/" + includedFiles[i].replace(File.separator, "/");
        }
        return includedFiles;

    }


}
