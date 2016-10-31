package org.jboss.shrinkwrap.resolver.api.maven.embedded;

import java.io.File;
import java.util.List;

import org.apache.maven.model.Model;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.Invoker;
import org.jboss.shrinkwrap.api.Archive;

/**
 * @author <a href="mailto:mjobanek@redhat.com">Matous Jobanek</a>
 */
public interface BuiltProject {

    /**
     * Tries to find an {@link Archive} with a default name.
     * <p>
     *     As a "default archive name" is understood:
     *     <ul>
     *         <li>either combination of artifactId + version + packaging suffix</li>
     *         <li>or a finalName set in &lt;build&gt; section of project's POM file + packaging suffix</li>
     *     </ul>
     * </p>
     * If no archive with a corresponding name is found, then {@code null} is returned
     *
     *
     * @return An {@link Archive} with a default name. If no archive is found then null is returned
     */
    Archive getDefaultBuiltArchive();

    /**
     * Returns an instance of {@link BuiltProject} representing module with the given name.
     * The name has to equal to the string specified in current project POM file
     *
     * @param moduleName Name of the required module
     * @return An instance of {@link BuiltProject} representing module with the given name.
     */
    BuiltProject getModule(String moduleName);

    /**
     * Returns a list of instances of {@link BuiltProject} representing all modules specified in the POM file.
     *
     * @return A list of instances of {@link BuiltProject} representing all modules specified in the POM file.
     */
    List<BuiltProject> getModules();

    /**
     * Returns a {@link File} representing a build target directory
     *
     * @return A {@link File} representing a build target directory
     */
    File getTargetDirectory();

    /**
     * Returns a list of all supported {@link Archive}s found in the first level of the build target directory.
     * If no target directory is present then null is returned.
     *
     * @return A list of all supported {@link Archive}s found in the first level of the build target directory.
     * If no target directory is present then null is returned.
     */
    List<Archive> getArchives();

    /**
     * Returns a list of all {@link Archive}s of the given type of {@link Archive} found in the first level of the build
     * target directory. If no target directory is present then null is returned.
     *
     * @param type The required type of {@link Archive}
     * @param <A> The required type of {@link Archive}
     * @return A list of all {@link Archive}s of the given type of {@link Archive} found in the first level of the build
     * target directory. If no target directory is present then null is returned.
     */
    <A extends Archive<?>> List<A> getArchives(Class<A> type);

    /**
     * Returns an instance of {@link Model} representing the set and parsed POM file
     *
     * @return An instance of {@link Model} representing the set and parsed POM file
     */
    Model getModel();

    /**
     * Returns a log of a Maven build of this project. If no log has been retrieved and set, then null is returned.
     * <p>
     *     NOTE: Please be aware that if you retrieve this instance of {@link BuiltProject} from the EmbeddedMaven
     *     build and you use the method {@link EmbeddedMaven#withMavenInvokerSet(InvocationRequest, Invoker)} for it,
     *     then no Maven log is set by default and this method returns {@code null}!
     * </p>
     *
     * @return A log of a Maven build of this project. If no log has been retrieved and set, then null is returned.
     */
    String getMavenLog();

    /**
     * Sets the given log a Maven build for this instance
     *
     * @param mavenLog The log of a Maven build to be set
     */
    void setMavenLog(String mavenLog);

    /**
     * Returns an exit code of a Maven build set for this instance. Default value is 0
     *
     * @return An exit code of a Maven build set for this instance. Default value is 0
     */
    int getMavenBuildExitCode();

    /**
     * Sets the given exit code of a Maven build for this instance. Default value is 0
     *
     * @param mavenBuildExitCode The exit code of a Maven build to be set. Default value is 0
     */
    void setMavenBuildExitCode(int mavenBuildExitCode);
}
