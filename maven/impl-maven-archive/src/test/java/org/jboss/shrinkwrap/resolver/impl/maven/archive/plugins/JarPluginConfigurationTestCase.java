/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat Middleware LLC, and individual contributors
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

import org.codehaus.plexus.compiler.CompilerConfiguration;
import org.codehaus.plexus.compiler.javac.JavacCompiler;
import org.jboss.shrinkwrap.resolver.api.maven.MavenWorkingSession;
import org.jboss.shrinkwrap.resolver.impl.maven.MavenWorkingSessionImpl;
import org.jboss.shrinkwrap.resolver.impl.maven.task.LoadPomTask;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test Jar Plugin configuration
 *
 * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
 *
 */
class JarPluginConfigurationTestCase {

    @Test
    void additionalCompilerArguments() {
        MavenWorkingSession session = new MavenWorkingSessionImpl();
        LoadPomTask.loadPomFromFile("src/test/resources/poms/jar-with-compiler-args.xml").execute(session);

        CompilerPluginConfiguration configuration = new CompilerPluginConfiguration(session.getParsedPomFile());
        CompilerConfiguration compilerConf = configuration.asCompilerConfiguration();
        compilerConf.setOutputLocation("target");
        String[] args = JavacCompiler.buildCompilerArguments(compilerConf, new String[0]);



        Assertions.assertNotNull(configuration.getAdditionalCompilerArgs(), "Additional configuration is passed");
        Assertions.assertEquals("1.7", configuration.getAdditionalCompilerArgs().get("-source"), "Source is 1.7");

        // source and target are set twice to 1.7
        // this test interpolation of the properties in POM as well
        Assertions.assertEquals(2, countOccurrences(args, "-source", "1.7"), "There are two -source 1.7");
        Assertions.assertEquals(2, countOccurrences(args, "-target", "1.7"), "There are two -target 1.7");
    }

    private int countOccurrences(String[] args, String arg, String value) {

        int occurrences = 0;
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals(arg) && i < (args.length - 1) && args[i + 1].equals(value)) {
                occurrences++;
            }
        }
        return occurrences;
    }
}
