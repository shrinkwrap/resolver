/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.shrinkwrap.resolver.impl.maven.archive.plugins;

import java.util.Arrays;

import org.codehaus.plexus.compiler.CompilerConfiguration;
import org.codehaus.plexus.compiler.javac.JavacCompiler;
import org.jboss.shrinkwrap.resolver.api.maven.MavenWorkingSession;
import org.jboss.shrinkwrap.resolver.impl.maven.MavenWorkingSessionImpl;
import org.jboss.shrinkwrap.resolver.impl.maven.task.LoadPomTask;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Test Compiler Plugin configuration
 *
 * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
 *
 */
public class CompilerPluginConfigurationTestCase {

    @Test
    public void additionalCompilerArgs() {
        MavenWorkingSession session = new MavenWorkingSessionImpl();
        LoadPomTask.loadPomFromFile("src/test/resources/poms/compiler-args.xml").execute(session);

        CompilerPluginConfiguration configuration = new CompilerPluginConfiguration(session.getParsedPomFile());
        CompilerConfiguration compilerConf = configuration.asCompilerConfiguration();
        compilerConf.setOutputLocation("target");
        String[] args = JavacCompiler.buildCompilerArguments(compilerConf, new String[0], compilerConf.getSourceVersion());

        assertThat(Arrays.asList(args),
            hasItems("-verbose", "-Xlint:unchecked", "-Xlint:cast", "-source", "1.7"));
    }

    // SHRINKRES-164
    @Test
    public void compilerEncoding() {
        MavenWorkingSession session = new MavenWorkingSessionImpl();
        LoadPomTask.loadPomFromFile("src/test/resources/poms/source-encoding.xml").execute(session);

        CompilerPluginConfiguration configuration = new CompilerPluginConfiguration(session.getParsedPomFile());
        CompilerConfiguration compilerConf = configuration.asCompilerConfiguration();
        compilerConf.setOutputLocation("target");
        String[] args = JavacCompiler.buildCompilerArguments(compilerConf, new String[0], compilerConf.getSourceVersion());

        assertThat(Arrays.asList(args),
            hasItems("-verbose", "-Xlint:unchecked", "-Xlint:cast", "-source", "1.7", "-encoding", "ISO-8859-2"));
    }

    // SHRINKRES-164
    @Test
    public void compilerEncodingFromProperty() {
        MavenWorkingSession session = new MavenWorkingSessionImpl();
        LoadPomTask.loadPomFromFile("src/test/resources/poms/source-encoding-property.xml").execute(session);

        CompilerPluginConfiguration configuration = new CompilerPluginConfiguration(session.getParsedPomFile());
        CompilerConfiguration compilerConf = configuration.asCompilerConfiguration();
        compilerConf.setOutputLocation("target");
        String[] args = JavacCompiler.buildCompilerArguments(compilerConf, new String[0], compilerConf.getSourceVersion());

        assertThat(Arrays.asList(args),
            hasItems("-verbose", "-Xlint:unchecked", "-Xlint:cast", "-source", "1.7", "-encoding", "ISO-8859-2"));
    }
}
