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
package org.jboss.shrinkwrap.resolver.impl.maven.archive.importer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

import org.apache.maven.cli.MavenCli;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.Assignable;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.resolver.api.maven.MavenWorkingSession;
import org.jboss.shrinkwrap.resolver.api.maven.archive.importer.EmbeddedMavenImporterException;
import org.jboss.shrinkwrap.resolver.api.maven.archive.importer.PomEquippedEmbeddedMavenImporter;
import org.jboss.shrinkwrap.resolver.impl.maven.MavenWorkingSessionImpl;
import org.jboss.shrinkwrap.resolver.impl.maven.task.LoadPomTask;

public class PomEquippedEmbeddedMavenImporterImpl implements PomEquippedEmbeddedMavenImporter {
    private final Archive<?> archive;

    static void checkSuccess(OutputHolder output) {
        if (!output.getStandardOutput().contains("BUILD SUCCESS")) {
            throw new EmbeddedMavenImporterException("Packaging project with Maven failed:\n\n" + output.getStandardOutput());
        }
    }

    public PomEquippedEmbeddedMavenImporterImpl(Archive<?> archive, String pomFilePath) {
        this.archive = archive;
        pomFile = new File(pomFilePath);
        pomFileDirectory = pomFile.getParent();
        pomFileName = pomFile.getName();
        session = LoadPomTask.loadPomFromFile(pomFile).execute(new MavenWorkingSessionImpl());
    }

    final File pomFile;
    final String pomFileDirectory;
    final String pomFileName;

    final MavenWorkingSession session;

    @Override
    public PomEquippedEmbeddedMavenImporter importBuildOutput() {
        final OutputHolder output = new OutputHolder();
        new MavenCli().doMain(new String[]{"-f" + pomFile.getAbsolutePath(), "-DskipTests=true", "package"}, pomFileDirectory, output.createStandardPrintStream(), output.createErrorPrintStream());
        checkSuccess(output);
//        TODO ParsedPomFile should return path to built archive or at least outputDirectory
        JavaArchive importedArchive = ShrinkWrap.create(ZipImporter.class, "exploded.jar")
                .importFrom(new File(pomFileDirectory + "/target/" + session.getParsedPomFile().getFinalName()))
                .as(JavaArchive.class);
        archive.merge(importedArchive);
        return this;
    }

    @Override
    public <TYPE extends Assignable> TYPE as(Class<TYPE> clazz) {
        return archive.as(clazz);
    }

    static class OutputHolder {
        ByteArrayOutputStream errorOutputStream = new ByteArrayOutputStream();
        ByteArrayOutputStream standardOutputStream = new ByteArrayOutputStream();

        public PrintStream createErrorPrintStream() {
            return new PrintStream(errorOutputStream);
        }

        public PrintStream createStandardPrintStream() {
            return new PrintStream(standardOutputStream);
        }

        public String getStandardOutput() {
            return standardOutputStream.toString();
        }

    }
}
