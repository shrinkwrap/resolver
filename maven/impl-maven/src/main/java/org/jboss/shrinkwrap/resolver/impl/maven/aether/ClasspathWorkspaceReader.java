/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors
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
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFactoryConfigurationException;

import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.repository.WorkspaceReader;
import org.eclipse.aether.repository.WorkspaceRepository;
import org.jboss.shrinkwrap.resolver.impl.maven.util.Validate;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * {@link WorkspaceReader} implementation capable of reading from the ClassPath
 *
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @author <a href="mailto:mmatloka@gmail.com">Michal Matloka</a>
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class ClasspathWorkspaceReader implements WorkspaceReader {
    private static final Logger log = Logger.getLogger(ClasspathWorkspaceReader.class.getName());

    /**
     * class path entry
     */
    private static final String CLASS_PATH_KEY = "java.class.path";

    /**
     * surefire cannot modify class path for test execution, so it have to store it in a different variable
     */
    static final String SUREFIRE_CLASS_PATH_KEY = "surefire.test.class.path";

    /**
     * System property to override the relative path of the "flattened" pom.xml to prefer over the regular pom.xml, if present.
     *
     * @since <a href="https://issues.redhat.com/browse/SHRINKRES-299">SHRINKRES-299</a>
     */
    static final String FLATTENED_POM_PATH_KEY = "org.apache.maven.flattened-pom-path";

    /**
     * Contains File object and retrieved cached isFile and isDirectory values
     */
    private static final class FileInfo {
        private final File file;
        private final boolean isFile;
        private final boolean isDirectory;

        private FileInfo(final File file, final boolean isFile, final boolean isDirectory) {
            this.file = file;
            this.isFile = isFile;
            this.isDirectory = isDirectory;
        }

        private FileInfo(final File file) {
            this(file, file.isFile(), file.isDirectory());
        }

        private FileInfo(final String classpathEntry) {
            this(new File(classpathEntry));
        }

        private File getFile() {
            return file;
        }

        private boolean isFile() {
            return isFile;
        }

        private boolean isDirectory() {
            return isDirectory;
        }
    }

    private final Set<String> classPathEntries = new LinkedHashSet<>();

    /**
     * Cache classpath File objects and retrieved isFile isDirectory values. Key is a classpath entry
     *
     * @see #getClasspathFileInfo(String)
     */
    private final Map<String, FileInfo> classpathFileInfoCache = new HashMap<>();

    /**
     * Cache pom File objects and retrieved isFile isDirectory values. Key - child File
     *
     * @see #getPomFileInfo(java.io.File)
     */
    private final Map<File, FileInfo> pomFileInfoCache = new HashMap<>();

    /**
     * Cache Found in classpath artifacts. Key is a pom file.
     *
     * @see #getFoundArtifact(java.io.File)
     */
    private final Map<File, Artifact> foundArtifactCache = new HashMap<>();

    /**
     * The relative path of the "flattened" pom.xml to prefer over the regular pom.xml, if present.
     *
     * @see #createPomFileInfo(File)
     * @since <a href="https://issues.redhat.com/browse/SHRINKRES-299">SHRINKRES-299</a>
     */
    private final String flattenedPomPath;

    /**
     * Reuse DocumentBuilder.
     *
     * @see #getDocumentBuilder()
     */
    private DocumentBuilder documentBuilder;

    /**
     * Reuse XPath
     *
     * @see #getXPath()
     */
    private XPath xPath;

    /*
     * Compiled lazy-loaded xpath expressions. See getter methods.
     */
    private XPathExpression xPathParentGroupIdExpression;
    private XPathExpression xPathGroupIdExpression;
    private XPathExpression xPathArtifactIdExpression;
    private XPathExpression xPathTypeExpression;
    private XPathExpression xPathVersionExpression;
    private XPathExpression xPathParentVersionExpression;

    public ClasspathWorkspaceReader() {
        final String classPath = SecurityActions.getProperty(CLASS_PATH_KEY);
        final String surefireClassPath = SecurityActions.getProperty(SUREFIRE_CLASS_PATH_KEY);

        this.classPathEntries.addAll(getClassPathEntries(surefireClassPath));
        this.classPathEntries.addAll(getClassPathEntries(classPath));

        final String configuredFlattenedPomPath = SecurityActions.getProperty(FLATTENED_POM_PATH_KEY);
        this.flattenedPomPath = configuredFlattenedPomPath != null ? configuredFlattenedPomPath : ".flattened-pom.xml";
    }

    @Override
    public WorkspaceRepository getRepository() {
        return new WorkspaceRepository("classpath");
    }

    @Override
    public File findArtifact(final Artifact artifact) {
        for (String classpathEntry : classPathEntries) {
            final FileInfo fileInfo = getClasspathFileInfo(classpathEntry);
            final File file = fileInfo.getFile();

            if (fileInfo.isDirectory()) {
                // TODO: This is not reliable, file might have different name
                // FIXME: Surefire might user jar in the classpath instead of the target/classes
                final FileInfo pomFileInfo = getPomFileInfo(file);
                if (pomFileInfo != null && pomFileInfo.isFile()) {
                    final File pomFile = pomFileInfo.getFile();
                    final Artifact foundArtifact = getFoundArtifact(pomFile);
                    if (areEquivalent(artifact, foundArtifact)) {
                        return pomFile;
                    }
                }
            }
            // this is needed for Surefire when executed as 'mvn package'
            else if (fileInfo.isFile()) {

                final StringBuilder name = new StringBuilder(artifact.getArtifactId()).append("-").append(
                        artifact.getVersion());

                // SHRINKRES-102, consider classifier as well
                if (!Validate.isNullOrEmpty(artifact.getClassifier())) {
                    name.append("-").append(artifact.getClassifier());
                }

                String candidateName = file.getName();
                int suffixPosition = candidateName.lastIndexOf('.');
                if (suffixPosition != -1) {
                    candidateName = candidateName.substring(0, suffixPosition);
                }

                // TODO: This is nasty
                // we need to get a a pom.xml file to be sure we fetch transitive deps as well
                if (candidateName.contentEquals(name)) {
                    if ("pom".equals(artifact.getExtension())) {
                        // try to get pom file for the project
                        final FileInfo pomFileInfo = getPomFileInfo(file);
                        if (pomFileInfo != null && pomFileInfo.isFile()) {
                            final File pomFile = pomFileInfo.getFile();
                            final Artifact foundArtifact = getFoundArtifact(pomFile);
                            if (areEquivalent(artifact, foundArtifact)) {
                                return pomFile;
                            }
                        }
                    }

                    // we are looking for a non pom artifact, let's get it
                    name.append(".").append(artifact.getExtension());
                    if (file.getName().endsWith(name.toString())) {
                        // return raw file
                        return file;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Returns if two artifacts are equivalent, that is, have the same groupId, artifactId and Version
     *
     * @param artifact
     * left side artifact to be compared
     * @param foundArtifact
     * right side artifact to be compared
     *
     * @return true if the groupId, artifactId and version matches
     */
    private boolean areEquivalent(final Artifact artifact, final Artifact foundArtifact) {
        boolean areEquivalent = (foundArtifact.getGroupId().equals(artifact.getGroupId())
                && foundArtifact.getArtifactId().equals(artifact.getArtifactId()) && foundArtifact.getVersion().equals(
                artifact.getVersion()));
        return areEquivalent;
    }

    @Override
    public List<String> findVersions(final Artifact artifact) {
        return Collections.emptyList();
    }

    private Set<String> getClassPathEntries(final String classPath) {
        if (Validate.isNullOrEmpty(classPath)) {
            return Collections.emptySet();
        }
        return new LinkedHashSet<>(Arrays.asList(classPath.split(String.valueOf(File.pathSeparatorChar))));
    }

    private FileInfo getClasspathFileInfo(final String classpathEntry) {
        FileInfo classpathFileInfo = classpathFileInfoCache.get(classpathEntry);
        if (classpathFileInfo == null) {
            classpathFileInfo = new FileInfo(classpathEntry);
            classpathFileInfoCache.put(classpathEntry, classpathFileInfo);
        }
        return classpathFileInfo;
    }

    private FileInfo getPomFileInfo(final File childFile) {
        FileInfo pomFileInfo = pomFileInfoCache.get(childFile);
        if (pomFileInfo == null) {
            pomFileInfo = createPomFileInfo(childFile);
            if (pomFileInfo != null) {
                pomFileInfoCache.put(childFile, pomFileInfo);
            }
        }
        return pomFileInfo;
    }

    private FileInfo createPomFileInfo(final File childFile) {

        // assuming that directory entry on classpath is target/classes directory, we need
        // to go two directories up in the structure and grab a pom.xml file from there
        File parent = childFile.getParentFile();
        if (parent != null) {
            parent = parent.getParentFile();
            if (parent != null) {
                final File pomFile = new File(parent, "pom.xml");
                return new FileInfo(pomFile);
            }
        }

        return null;
    }

    private Artifact getFoundArtifact(final File pomFile) {
        Artifact foundArtifact = foundArtifactCache.get(pomFile);
        if (foundArtifact == null) {
            foundArtifact = createFoundArtifact(pomFile);
            foundArtifactCache.put(pomFile, foundArtifact);
        }
        return foundArtifact;
    }

    private Artifact createFoundArtifact(final File pomFile) {
        try {
            if (log.isLoggable(Level.FINE)) {
                log.fine("Processing " + pomFile.getAbsolutePath() + " for classpath artifact resolution");
            }

            // TODO: load pom using Maven Model?
            // This might include a cycle in graph reconstruction, to be investigated
            final Document pom = loadPom(choosePomToLoad(pomFile));

            String groupId = getXPathGroupIdExpression().evaluate(pom);
            String artifactId = getXPathArtifactIdExpression().evaluate(pom);
            String type = getXPathTypeExpression().evaluate(pom);
            String version = getXPathVersionExpression().evaluate(pom);

            if (Validate.isNullOrEmpty(groupId)) {
                groupId = getXPathParentGroupIdExpression().evaluate(pom);
            }
            if (Validate.isNullOrEmpty(type)) {
                type = "jar";
            }
            if (version == null || version.isEmpty()) {
                version = getXPathParentVersionExpression().evaluate(pom);
            }

            final Artifact foundArtifact = new DefaultArtifact(groupId + ":" + artifactId + ":" + type + ":" + version);
            foundArtifact.setFile(pomFile);
            return foundArtifact;
        } catch (final Exception e) {
            throw new RuntimeException("Could not parse pom.xml: " + pomFile, e);
        }
    }

    // SHRINKRES-299, "Maven CI Friendly Versions": we prefer a "flattened" pom.xml (written by flatten-maven-plugin), if present,
    // effectively acting as a kind of "proxy" for the regular pom.xml
    private File choosePomToLoad(final File regularPomFile) {
        final File parentDir = regularPomFile.getParentFile();
        if (parentDir != null) {
            final File flattenedPomFile = new File(parentDir, flattenedPomPath);
            if (flattenedPomFile.isFile()) {
                return flattenedPomFile;
            }
        }
        return regularPomFile;
    }

    private Document loadPom(final File pom) throws IOException, SAXException, ParserConfigurationException {
        final DocumentBuilder documentBuilder = getDocumentBuilder();
        return documentBuilder.parse(pom);
    }

    private DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
        if (documentBuilder == null) {
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            documentBuilder = factory.newDocumentBuilder();
        }
        return documentBuilder;
    }

    /*
     * XPath expressions reuse
     */

    private XPath getXPath() {
        if (xPath == null) {
            XPathFactory factory;
            try {
                factory = XPathFactory.newInstance(XPathFactory.DEFAULT_OBJECT_MODEL_URI,
                                                   "com.sun.org.apache.xpath.internal.jaxp.XPathFactoryImpl",
                                                   ClassLoader.getSystemClassLoader());
            } catch (XPathFactoryConfigurationException e) {
                factory = XPathFactory.newInstance();
            }
            xPath = factory.newXPath();
        }
        return xPath;
    }

    private XPathExpression getXPathParentGroupIdExpression() throws XPathExpressionException {
        if (xPathParentGroupIdExpression == null) {
            xPathParentGroupIdExpression = getXPath().compile("/project/parent/groupId");
        }
        return xPathParentGroupIdExpression;
    }

    private XPathExpression getXPathGroupIdExpression() throws XPathExpressionException {
        if (xPathGroupIdExpression == null) {
            xPathGroupIdExpression = getXPath().compile("/project/groupId");
        }
        return xPathGroupIdExpression;
    }

    private XPathExpression getXPathArtifactIdExpression() throws XPathExpressionException {
        if (xPathArtifactIdExpression == null) {
            xPathArtifactIdExpression = getXPath().compile("/project/artifactId");
        }
        return xPathArtifactIdExpression;
    }

    private XPathExpression getXPathTypeExpression() throws XPathExpressionException {
        if (xPathTypeExpression == null) {
            xPathTypeExpression = getXPath().compile("/project/packaging");
        }
        return xPathTypeExpression;
    }

    private XPathExpression getXPathVersionExpression() throws XPathExpressionException {
        if (xPathVersionExpression == null) {
            xPathVersionExpression = getXPath().compile("/project/version");
        }
        return xPathVersionExpression;
    }

    private XPathExpression getXPathParentVersionExpression() throws XPathExpressionException {
        if (xPathParentVersionExpression == null) {
            xPathParentVersionExpression = getXPath().compile("/project/parent/version");
        }
        return xPathParentVersionExpression;
    }

}
