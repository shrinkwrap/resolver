package org.jboss.shrinkwrap.resolver.api.maven.embedded;

import java.io.File;
import java.util.List;

import org.apache.maven.model.Model;
import org.jboss.shrinkwrap.api.Archive;

/**
 * @author <a href="mailto:mjobanek@redhat.com">Matous Jobanek</a>
 */
public interface BuiltProject {

    Archive getDefaultBuiltArchive();

    BuiltProject getModule(String moduleName);

    List<BuiltProject> getModules();

    File getTargetDirectory();

    List<Archive> getArchives();

    <A extends Archive<?>> List<A> getArchives(Class<A> type);

    Model getModel();

    String getMavenLog();

    void setMavenLog(String mavenLog);

    int getMavenBuildExitCode();

    void setMavenBuildExitCode(int mavenBuildExitCode);
}
