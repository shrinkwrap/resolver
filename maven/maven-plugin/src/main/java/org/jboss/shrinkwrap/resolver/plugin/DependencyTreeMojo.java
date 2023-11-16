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
package org.jboss.shrinkwrap.resolver.plugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.maven.classrealm.ClassRealmManager;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.MavenArtifactInfo;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolvedArtifact;
import org.jboss.shrinkwrap.resolver.api.maven.ScopeType;

/**
 * Writes a dependency tree output
 *
 */
@Mojo(name = "dependency-tree", requiresDirectInvocation = true, requiresDependencyCollection = ResolutionScope.TEST)
public class DependencyTreeMojo extends AbstractResolverMojo {

    private static final String OUTPUT_DELIMITER;
    static {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 79; i++) {
            sb.append('-');
        }
        OUTPUT_DELIMITER = sb.toString();
    }

    @Parameter(defaultValue = "${outputFile}")
    private File outputFile;

    /**
     * Optional scope to use for dependency tree resolution
     *
     * @parameter expression="${scope}"
     */
    @Parameter(defaultValue = "${scope}")
    private String scope;

    // Maven is by default removing some of the artifacts from plugin classpath
    // namely, org.apache.maven.DefaultArtifactFilterManager does that
    // as the API is not configurable, we need to get Core Class Realm and combine it with plugin ClassRealm
    @Component
    private ClassRealmManager classRealmManager;

    @Override
    public void execute() {

        // first, we need to propagate environment settings
        PropagateExecutionContextMojo mojo = new PropagateExecutionContextMojo();
        mojo.setNamespace("maven.execution.");
        mojo.setSession(session);
        mojo.execute();

        // propagate into current environment
        SecurityActions.addProperties(session.getUserProperties());

        MavenProject project = session.getCurrentProject();

        // set scope
        ScopeType[] scopes = ScopeType.values();
        if (scope != null && !"".equals(scope)) {
            scopes = new ScopeType[] { ScopeType.fromScopeType(scope) };
        }

        // get ClassLoader that contains both Maven and plugin class path
        ClassLoader cls = getCombinedClassLoader(classRealmManager);

        // skip resolution if no dependencies are in the project (e.g. parent agreggator)
        MavenResolvedArtifact[] artifacts;

        if (project.getDependencies() == null || project.getDependencies().size() == 0) {
            artifacts = new MavenResolvedArtifact[0];
        } else {
            artifacts = Maven.configureResolverViaPlugin(cls)
                .importDependencies(scopes)
                .resolve()
                .withTransitivity()
                .asResolvedArtifact();
        }

        StringBuilder projectGAV = new StringBuilder();
        projectGAV.append(project.getGroupId()).append(":").append(project.getArtifactId()).append(":")
            .append(project.getPackaging()).append(":").append(project.getVersion()).append("\n");

        String dependencyTree = buildDependencyTree(projectGAV, "+- ", artifacts);

        // write output to file if specified
        if (outputFile != null) {
            try (FileWriter writer = new FileWriter(outputFile)) {
                writer.write(dependencyTree);
                getLog().info("Dependency tree output was writen into: " + outputFile.getAbsolutePath());
            } catch (IOException e) {

            }
        }
        // write an output to console
        else {
            StringBuilder outputBuffer = new StringBuilder();
            outputBuffer.append(OUTPUT_DELIMITER).append("\nShrinkWrap Maven: Dependency Tree\n").append(OUTPUT_DELIMITER)
                .append("\n").append(dependencyTree).append(OUTPUT_DELIMITER);

            getLog().info(outputBuffer.toString());
        }

    }

    private static String buildDependencyTree(StringBuilder sb, String indent, MavenArtifactInfo[] artifacts) {

        int length = artifacts.length - 1;
        for (int i = 0; i <= length; i++) {
            MavenArtifactInfo artifact = artifacts[i];

            String parsedIndent = indent;
            String nextLevelIndent = indent.replaceAll("\\+- $", "\\|  ") + "+- ";
            // indent last one in different manner
            if (i == length) {
                parsedIndent = parsedIndent.replaceAll("\\+- $", "\\\\- ");
                nextLevelIndent = indent.replaceAll("\\+- $", "   ") + "+- ";
            }

            sb.append(parsedIndent).append(artifact.getCoordinate().toCanonicalForm()).append(" [").append(artifact.getScope())
                .append("]").append("\n");
            buildDependencyTree(sb, nextLevelIndent, artifact.getDependencies());
        }

        return sb.toString();
    }

    // creates a class loader that has access to both current thread classloader and Maven Core classloader
    private ClassLoader getCombinedClassLoader(ClassRealmManager manager) {

        List<URL> urlList = new ArrayList<>();

        // add thread classpath
        ClassLoader threadCL = SecurityActions.getThreadContextClassLoader();
        if (threadCL instanceof URLClassLoader) {
            urlList.addAll(Arrays.asList(((URLClassLoader) threadCL).getURLs()));
        }

        // add maven core libraries
        ClassRealm core = manager.getCoreRealm();
        if (core != null) {
            urlList.addAll(Arrays.asList(core.getURLs()));
        }

        ClassRealm mavenApi = manager.getMavenApiRealm();
        if (mavenApi != null) {
            urlList.addAll(Arrays.asList(mavenApi.getURLs()));
        }

        // we need to keep threadCL as parent, otherwise we'll get ClassCastException in runtime
        URLClassLoader cl = new URLClassLoader(urlList.toArray(new URL[0]), threadCL);

        // for (URL u : cl.getURLs()) {
        // System.out.println("CLR: " + u);
        // }

        return cl;
    }
}
