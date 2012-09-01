/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
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
package org.jboss.shrinkwrap.resolver.impl.maven.aether;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.repository.WorkspaceReader;
import org.sonatype.aether.repository.WorkspaceRepository;
import org.sonatype.aether.util.artifact.DefaultArtifact;
import org.w3c.dom.Document;

/**
 * {@link WorkspaceReader} implementation capable of reading from the ClassPath
 *
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 */
public class ClasspathWorkspaceReader implements WorkspaceReader {
    private static final Logger log = Logger.getLogger(ClasspathWorkspaceReader.class.getName());

    // class path entry
    private static final String CLASS_PATH_KEY = "java.class.path";
    // surefire cannot modify class path for test execution, so it have to store it in a different variable
    private static final String SUREFIRE_CLASS_PATH_KEY = "surefire.test.class.path";

    private final Set<String> classPathEntries;

    public ClasspathWorkspaceReader() {
        String classPath = SecurityActions.getProperty(CLASS_PATH_KEY);
        String surefireClassPath = SecurityActions.getProperty(SUREFIRE_CLASS_PATH_KEY);

        this.classPathEntries = new LinkedHashSet<String>();
        this.classPathEntries.addAll(getClassPathEntries(surefireClassPath));
        this.classPathEntries.addAll(getClassPathEntries(classPath));

    }

    @Override
    public WorkspaceRepository getRepository() {
        return new WorkspaceRepository("classpath");
    }

    @Override
    public File findArtifact(Artifact artifact) {
        for (String classpathEntry : classPathEntries) {
            File file = new File(classpathEntry);
            if (file.isDirectory()) {
                // TODO: This is not reliable, file might have different name
                // FIXME: Surefire might user jar in the classpath instead of the target/classes
                File pomFile = new File(file.getParentFile().getParentFile(), "pom.xml");
                if (pomFile.isFile()) {
                    try {
                        if (log.isLoggable(Level.FINE)) {
                            log.fine("Processing " + pomFile.getAbsolutePath() + " for classpath artifact resolution");
                        }

                        // TODO: load pom using Maven Model?
                        // This might include a cycle in graph reconstruction, to be investigated
                        Document pom = loadPom(pomFile);

                        XPathFactory factory = XPathFactory.newInstance();
                        XPath xpath = factory.newXPath();

                        String groupId = xpath.evaluate("/project/groupId", pom);
                        String artifactId = xpath.evaluate("/project/artifactId", pom);
                        String type = xpath.evaluate("/project/packaging", pom);
                        String version = xpath.evaluate("/project/version", pom);

                        if (groupId == null || groupId.equals("")) {
                            groupId = xpath.evaluate("/project/parent/groupId", pom);
                        }
                        if (type == null || type.equals("")) {
                            type = "jar";
                        }
                        if (version == null || version.equals("")) {
                            version = xpath.evaluate("/project/parent/version", pom);
                        }

                        // TODO: cache parsed artifacts to avoid re-parsing..
                        Artifact foundArtifact = new DefaultArtifact(groupId + ":" + artifactId + ":" + type + ":"
                            + version);
                        foundArtifact.setFile(pomFile);

                        if (foundArtifact.getGroupId().equals(artifact.getGroupId())
                            && foundArtifact.getArtifactId().equals(artifact.getArtifactId())) {
                            return pomFile;
                        }
                    } catch (Exception e) {
                        throw new RuntimeException("Could not parse pom.xml: " + pomFile, e);
                    }
                }
            }
            // this is needed for Surefire when runned as 'mvn package'
            else if (file.isFile()) {
                StringBuilder name = new StringBuilder().append(artifact.getArtifactId()).append("-")
                    .append(artifact.getVersion());

                // TODO: This is nasty
                // we need to get a a pom.xml file to be sure we fetch transitive deps as well
                if (file.getAbsolutePath().contains(name.toString())) {
                    if ("pom".equals(artifact.getExtension())) {
                        // try to get pom file for the project
                        File pomFile = new File(file.getParentFile().getParentFile(), "pom.xml");
                        if (pomFile.isFile()) {
                            return pomFile;
                        }
                    }
                    // we are looking for a non pom artifact, let's get it
                    name.append(".").append(artifact.getExtension());
                    if (file.getAbsolutePath().endsWith(name.toString())) {
                        // return raw file
                        return file;
                    }
                }
            }
        }
        return null;
    }

    private Document loadPom(File pom) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(pom);
    }

    @Override
    public List<String> findVersions(Artifact artifact) {
        return new ArrayList<String>();
    }

    private Set<String> getClassPathEntries(String classPath) {
        if (classPath == null || classPath.length() < 1) {
            return Collections.emptySet();
        }
        return new LinkedHashSet<String>(Arrays.asList(classPath.split(String.valueOf(File.pathSeparatorChar))));
    }
}
