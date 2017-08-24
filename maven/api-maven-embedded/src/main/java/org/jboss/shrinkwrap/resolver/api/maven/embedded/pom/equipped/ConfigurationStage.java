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

package org.jboss.shrinkwrap.resolver.api.maven.embedded.pom.equipped;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvokerLogger;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.BuildStage;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.BuiltProject;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.daemon.DaemonBuildTrigger;

/**
 * @author <a href="mailto:mjobanek@gmail.com">Matous Jobanek</a>
 */
public interface ConfigurationStage<DIST_OR_CONFIG extends ConfigurationStage, DAEMON_TRIGGER_TYPE extends DaemonBuildTrigger>
    extends BuildStage<DAEMON_TRIGGER_TYPE> {

     /**
      * Sets the interaction mode of the Maven invocation. Equivalent of {@code -B} and {@code --batch-mode}
      *
      * @param batchMode <code>true</code> if Maven should be executed in non-interactive mode, <code>false</code> if the
      *            interactive modes is used.
      * @return Modified instance of EmbeddedMaven
      */
     DIST_OR_CONFIG setBatchMode(boolean batchMode);

     /**
      * Sets the network mode of the Maven invocation. Equivalent of -o and --offline
      *
      * @param offline true if Maven should be executed in offline mode, false if the online mode is used.
      * @return Modified instance of EmbeddedMaven
      */
     DIST_OR_CONFIG setOffline(boolean offline);

     /**
      * Sets the debug mode of the Maven invocation. Equivalent of -X and --debug
      *
      * @param debug true if Maven should be executed in debug mode, false if the normal mode should be used.
      * @return Modified instance of EmbeddedMaven
      */
     DIST_OR_CONFIG setDebug(boolean debug);

     /**
      * Sets the exception output mode of the Maven invocation. Equivalent of -e and --errors
      *
      * @param showErrors true if Maven should print stack traces, false otherwise.
      * @return Modified instance of EmbeddedMaven
      */
     DIST_OR_CONFIG setShowErrors(boolean showErrors);

     /**
      * Specifies whether Maven should enforce an update check for plugins and snapshots. Equivalent of -U and --update-snapshots
      *
      * @param updateSnapshots true if plugins and snapshots should be updated, false otherwise.
      * @return Modified instance of EmbeddedMaven
      */
     DIST_OR_CONFIG setUpdateSnapshots(boolean updateSnapshots);

     /**
      * Sets the failure mode of the Maven invocation. Equivalent of {@code -ff} and {@code --fail-fast}, {@code -fae}
      * and {@code --fail-at-end}, {@code -fn} and {@code --fail-never}
      *
      * @param failureBehavior The failure mode, must be one of {@link InvocationRequest.ReactorFailureBehavior#FailFast},
      *            {@link InvocationRequest.ReactorFailureBehavior#FailAtEnd} and {@link InvocationRequest.ReactorFailureBehavior#FailNever}.
      * @return Modified instance of EmbeddedMaven
      */
     DIST_OR_CONFIG setReactorFailureBehavior(InvocationRequest.ReactorFailureBehavior reactorFailureBehavior);

     /**
      * The id of the build strategy to use. equivalent of {@code --builder id}. <b>Note. This is available since Maven
      * 3.2.1</b>
      *
      * @param id The builder id.
      * @return Modified instance of EmbeddedMaven
      */
     DIST_OR_CONFIG setBuilder(String id);

     /**
      * Sets the path to the base directory of the local repository to use for the Maven invocation.
      *
      * @param localRepositoryDirectory The path to the base directory of the local repository, may be null.
      * @return Modified instance of EmbeddedMaven
      */
     DIST_OR_CONFIG setLocalRepositoryDirectory(File localRepositoryDirectory);

     /**
      * Sets the input stream used to provide input for the invoked Maven build. This is in particular useful when invoking Maven in interactive mode.
      *
      * @param invokerLogger The input stream used to provide input for the invoked Maven build, may be null if not required.
      * @return Modified instance of EmbeddedMaven
      */
     DIST_OR_CONFIG setLogger(InvokerLogger invokerLogger);

     /**
      * Sets the working directory for the Maven invocation.
      *
      * @param workingDirectory The working directory for the Maven invocation, may be null to derive the working directory from the base directory of the processed POM.
      * @return Modified instance of EmbeddedMaven
      */
     DIST_OR_CONFIG setWorkingDirectory(File workingDirectory);

     /**
      * Sets the input stream used to provide input for the invoked Maven build. This is in particular useful when invoking Maven in interactive mode.
      *
      * @param inputStream The input stream used to provide input for the invoked Maven build, may be null if not required.
      * @return Modified instance of EmbeddedMaven
      */
     DIST_OR_CONFIG setInputStream(InputStream inputStream);

     /**
      * Sets the path to the base directory of the Java installation used to run Maven.
      *
      * @param javaHome The path to the base directory of the Java installation used to run Maven, may be null to use the default Java home.
      * @return Modified instance of EmbeddedMaven
      */
     DIST_OR_CONFIG setJavaHome(File javaHome);

     /**
      * Sets the system properties for the Maven invocation.
      *
      * @param properties The system properties for the Maven invocation, may be null if not set.
      * @return Modified instance of EmbeddedMaven
      */
     DIST_OR_CONFIG setProperties(Properties properties);

     /**
      * Adds the property to the list of properties.
      *
      * @param key The key of the property to be added
      * @param value The value of the property to be added
      * @return Modified instance of EmbeddedMaven
      */
     DIST_OR_CONFIG addProperty(String key, String value);

     /**
      * Specifies whether the test of the project should be skipped during Maven build - Equivalent of --skipTests.
      * Default is <code>true</code>
      *
      * @param skipTests false the test of the project should be invoked
      * @return Modified instance of EmbeddedMaven
      */
     DIST_OR_CONFIG skipTests(boolean skipTests);

     /**
      * Sets the goals for the Maven invocation.
      *
      * @param goals The goals for the Maven invocation, may be null to execute the POMs default goal.
      * @return Modified instance of EmbeddedMaven
      */
     DIST_OR_CONFIG setGoals(List<String> goals);

     /**
      * Sets the goals for the Maven invocation.
      *
      * @param goals The goals for the Maven invocation, may be null to execute the POMs default goal.
      * @return Modified instance of EmbeddedMaven
      */
     DIST_OR_CONFIG setGoals(String... goals);

     /**
      * Sets the profiles for the Maven invocation. Equivalent of -P and --active-profiles
      *
      * @param profiles The profiles for the Maven invocation, may be null to use the default profiles.
      * @return Modified instance of EmbeddedMaven
      */
     DIST_OR_CONFIG setProfiles(List<String> profiles);

     /**
      * Sets the profiles for the Maven invocation. Equivalent of -P and --active-profiles
      *
      * @param profiles The profiles for the Maven invocation, may be null to use the default profiles.
      * @return Modified instance of EmbeddedMaven
      */
     DIST_OR_CONFIG setProfiles(String... profiles);

     /**
      * Specifies whether the environment variables of the current process should be propagated to the Maven invocation.
      *
      * @param shellEnvironmentInherited true if the environment variables should be propagated, false otherwise.
      * @return Modified instance of EmbeddedMaven
      */
     DIST_OR_CONFIG setShellEnvironmentInherited(boolean shellEnvironmentInherited);

     /**
      * Sets the path to the user settings for the Maven invocation. Equivalent of -s and --settings
      *
      * @param userSettingsFile The path to the user settings for the Maven invocation, may be null to load the user settings from the default location.
      * @return Modified instance of EmbeddedMaven
      */
     DIST_OR_CONFIG setUserSettingsFile(File userSettingsFile);

     /**
      * Sets the path to the global settings for the Maven invocation. Equivalent of -gs and --global-settings
      *
      * @param globalSettingsFile The path to the global settings for the Maven invocation, may be null to load the global settings from the default location.
      * @return Modified instance of EmbeddedMaven
      */
     DIST_OR_CONFIG setGlobalSettingsFile(File globalSettingsFile);

     /**
      * Sets the alternate path for the user toolchains file Equivalent of -t or --toolchains
      *
      * @param toolchainsFile the alternate path for the user toolchains file
      * @return Modified instance of EmbeddedMaven
      */
     DIST_OR_CONFIG setToolchainsFile(File toolchainsFile);

     /**
      * Sets the alternate path for the global toolchains file Equivalent of {@code -gt} or {@code --global-toolchains}
      *
      * @param toolchains
      *     the alternate path for the global toolchains file
      *
      * @return Modified instance of EmbeddedMaven
      */
     DIST_OR_CONFIG setGlobalToolchainsFile(File toolchains);

     /**
      * Sets the checksum mode of the Maven invocation. Equivalent of {@code -c} or {@code --lax-checksums}, {@code -C}
      * or {@code --strict-checksums}
      *
      * @param globalChecksumPolicy The checksum mode, must be one of {@link InvocationRequest.CheckSumPolicy#Warn} and
      *            {@link InvocationRequest.CheckSumPolicy#Fail}.
      * @return Modified instance of EmbeddedMaven
      */
     DIST_OR_CONFIG setGlobalChecksumPolicy(InvocationRequest.CheckSumPolicy globalChecksumPolicy);

     /**
      * Specifies whether Maven should check for plugin updates.
      * <p>
      * Equivalent of -npu or --no-plugin-updates
      * note: Ineffective with Maven3, only kept for backward compatibility
      * </p>
      *
      * @param nonPluginUpdates true if plugin updates should be suppressed, false otherwise.
      * @return Modified instance of EmbeddedMaven
      */
     DIST_OR_CONFIG setNonPluginUpdates(boolean nonPluginUpdates);

     /**
      * Sets the recursion behavior of a reactor invocation. Inverse equivalent of -N and --non-recursive
      *
      * @param recursive true if sub modules should be build, false otherwise.
      * @return Modified instance of EmbeddedMaven
      */
     DIST_OR_CONFIG setRecursive(boolean recursive);

     /**
      * Adds the specified environment variable to the Maven invocation.
      *
      * @param name The name of the environment variable, must not be null.
      * @param value The value of the environment variable, must not be null.
      * @return Modified instance of EmbeddedMaven
      */
     DIST_OR_CONFIG addShellEnvironment(String name, String value);

     /**
      * Sets the value of the MAVEN_OPTS environment variable.
      *
      * @param mavenOpts The value of the MAVEN_OPTS environment variable, may be null to use the default options.
      * @return Modified instance of EmbeddedMaven
      */
     DIST_OR_CONFIG setMavenOpts(String mavenOpts);

     /**
      * enable displaying version without stopping the build Equivalent of -V or --show-version
      *
      * @param showVersion enable displaying version
      * @return Modified instance of EmbeddedMaven
      */
     DIST_OR_CONFIG setShowVersion(boolean showVersion);

     /**
      * Thread count, for instance 2.0C where C is core multiplied Equivalent of -T or --threads
      *
      * @param threads the threadcount
      * @return Modified instance of EmbeddedMaven
      */
     DIST_OR_CONFIG setThreads(String threads);

     /**
      * Sets the reactor project list. Equivalent of -P or --projects
      *
      * @param projects the reactor project list
      * @return Modified instance of EmbeddedMaven
      */
     DIST_OR_CONFIG setProjects(List<String> projects);

     /**
      * Sets the reactor project list. Equivalent of -P or --projects
      *
      * @param projects the reactor project list
      * @return Modified instance of EmbeddedMaven
      */
     DIST_OR_CONFIG setProjects(String... projects);

     /**
      * Enable the 'also make' mode. Equivalent of -am or --also-make
      *
      * @param alsoMake enable 'also make' mode
      * @return Modified instance of EmbeddedMaven
      */
     DIST_OR_CONFIG setAlsoMake(boolean alsoMake);

     /**
      * Enable the 'also make dependents' mode. Equivalent of -amd or --also-make-dependents
      *
      * @param alsoMakeDependents enable 'also make' mode
      * @return Modified instance of EmbeddedMaven
      */
     DIST_OR_CONFIG setAlsoMakeDependents(boolean alsoMakeDependents);

     /**
      * Resume reactor from specified project. Equivalent of -rf or --resume-from
      *
      * @param resumeFrom set the project to resume from
      * @return Modified instance of EmbeddedMaven
      */
     DIST_OR_CONFIG setResumeFrom(String resumeFrom);

     /**
      * Sets if the build output shold be in the quiet mode or not.
      * It means that the output will not be printed on standard output, but it will be accessible via the
      * {@link BuiltProject#getMavenLog()}. Default is false
      *
      * @param quiet If the build should be in quite mode;
      * @return Modified EmbeddedMaven instance
      */
     DIST_OR_CONFIG setQuiet(boolean quiet);

     /**
      * Puts the build output into the quiet mode. It means that the output will not be printed on standard output,
      * but it will be accessible via the {@link BuiltProject#getMavenLog()}.
      *
      * @return Modified EmbeddedMaven instance
      */
     DIST_OR_CONFIG setQuiet();

    /**
     * Sets an alternate POM file. Equivalent of -f or --file
     *
     * @param pomFile the alternate POM file (or directory with pom.xml)
     * @return Modified instance of EmbeddedMaven
     */
    DIST_OR_CONFIG setAlternatePomFile(String pomFile);
}
