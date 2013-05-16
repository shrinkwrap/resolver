package org.jboss.shrinkwrap.resolver.plugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.MavenArtifactInfo;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolvedArtifact;
import org.jboss.shrinkwrap.resolver.api.maven.ScopeType;
import org.jboss.shrinkwrap.resolver.api.maven.strategy.AcceptAllStrategy;
import org.jboss.shrinkwrap.resolver.api.maven.strategy.AcceptScopesStrategy;
import org.jboss.shrinkwrap.resolver.api.maven.strategy.MavenResolutionStrategy;

/**
 * Writes a dependency tree output
 *
 * Following properties are propagated:
 *
 * @goal dependency-tree
 * @requiresProject
 * @requiresDirectInvocation
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

        // resolve
        MavenResolutionStrategy resolutionStrategy = AcceptAllStrategy.INSTANCE;
        if (scope != null && !"".equals(scope)) {
            resolutionStrategy = new AcceptScopesStrategy(ScopeType.fromScopeType(scope));
        }

        MavenResolvedArtifact[] artifacts = Maven.configureResolverViaPlugin()
                .importRuntimeAndTestDependencies(resolutionStrategy).asResolvedArtifact();

        String dependencyTree = buildDependencyTree(new StringBuilder(), "", artifacts);

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

        for (MavenArtifactInfo artifact : artifacts) {
            sb.append(indent).append(artifact.getCoordinate().toCanonicalForm()).append(" [").append(artifact.getScope())
                    .append("]").append("\n");
            buildDependencyTree(sb, indent + "  ", artifact.getDependencies());
        }

        return sb.toString();
    }

}
