package org.jboss.shrinkwrap.resolver.plugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.MavenArtifactInfo;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolvedArtifact;
import org.jboss.shrinkwrap.resolver.api.maven.ScopeType;

/**
 * Writes a dependency tree output
 *
 * Following properties are propagated:
 *
 * @goal dependency-tree
 * @requiresProject
 * @requiresDirectInvocation
 * @requiresDependencyCollection test
 * @executionStrategy always
 *
 */
public class DependencyTreeMojo extends AbstractMojo {

    private static final String OUTPUT_DELIMITER;
    static {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 79; i++) {
            sb.append('-');
        }
        OUTPUT_DELIMITER = sb.toString();
    }

    /**
     * The current build session instance.
     *
     * @parameter expression="${session}"
     * @required
     * @readonly
     */
    private MavenSession session;

    /**
     * Output file for the dependency tree, can be omitted
     *
     * @parameter expression="${outputFile}"
     */
    private File outputFile;

    /**
     * Optional scope to use for dependency tree resolution
     *
     * @parameter expression="${scope}"
     */
    private String scope;

    public void execute() throws MojoExecutionException {

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

        // skip resolution if no dependencies are in the project (e.g. parent agreggator)
        MavenResolvedArtifact[] artifacts;
        if (project.getDependencies() == null || project.getDependencies().size() == 0) {
            artifacts = new MavenResolvedArtifact[0];
        } else {
            artifacts = Maven.configureResolverViaPlugin().importDependencies(scopes).resolve().withTransitivity()
                    .asResolvedArtifact();
        }

        StringBuilder projectGAV = new StringBuilder();
        projectGAV.append(project.getGroupId()).append(":").append(project.getArtifactId()).append(":")
                .append(project.getPackaging()).append(":").append(project.getVersion()).append("\n");

        String dependencyTree = buildDependencyTree(projectGAV, "+- ", artifacts);

        // write output to file if specified
        if (outputFile != null) {
            FileWriter writer = null;
            try {
                writer = new FileWriter(outputFile);
                writer.write(dependencyTree);
                getLog().info("Dependency tree output was writen into: " + outputFile.getAbsolutePath());
            } catch (IOException e) {

            } finally {
                try {
                    if (writer != null) {
                        writer.close();
                    }
                } catch (IOException e) {
                }
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
}
