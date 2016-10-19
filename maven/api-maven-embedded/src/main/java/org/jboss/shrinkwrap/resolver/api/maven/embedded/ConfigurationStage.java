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

package org.jboss.shrinkwrap.resolver.api.maven.embedded;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import org.apache.maven.shared.invoker.InvokerLogger;

import static org.apache.maven.shared.invoker.InvocationRequest.CHECKSUM_POLICY_FAIL;
import static org.apache.maven.shared.invoker.InvocationRequest.CHECKSUM_POLICY_WARN;
import static org.apache.maven.shared.invoker.InvocationRequest.REACTOR_FAIL_AT_END;
import static org.apache.maven.shared.invoker.InvocationRequest.REACTOR_FAIL_FAST;
import static org.apache.maven.shared.invoker.InvocationRequest.REACTOR_FAIL_NEVER;

/**
 * @author <a href="mailto:mjobanek@gmail.com">Matous Jobanek</a>
 */
public interface ConfigurationStage extends BuildStage {

     /**
      * Sets the interaction mode of the Maven invocation. Inverse equivalent of -B and --batch-mode
      *
      * @param interactive true if Maven should be executed in interactive mode, false if the batch mode is used.
      * @return Modified instance of EmbeddedMaven
      */
     ConfigurationStage setInteractive(boolean interactive);

     /**
      * Sets the network mode of the Maven invocation. Equivalent of -o and --offline
      *
      * @param offline true if Maven should be executed in offline mode, false if the online mode is used.
      * @return Modified instance of EmbeddedMaven
      */
     ConfigurationStage setOffline(boolean offline);

     /**
      * Sets the debug mode of the Maven invocation. Equivalent of -X and --debug
      *
      * @param debug true if Maven should be executed in debug mode, false if the normal mode should be used.
      * @return Modified instance of EmbeddedMaven
      */
     ConfigurationStage setDebug(boolean debug);

     /**
      * Sets the exception output mode of the Maven invocation. Equivalent of -e and --errors
      *
      * @param showErrors true if Maven should print stack traces, false otherwise.
      * @return Modified instance of EmbeddedMaven
      */
     ConfigurationStage setShowErrors(boolean showErrors);

     /**
      * Specifies whether Maven should enforce an update check for plugins and snapshots. Equivalent of -U and --update-snapshots
      *
      * @param updateSnapshots true if plugins and snapshots should be updated, false otherwise.
      * @return Modified instance of EmbeddedMaven
      */
     ConfigurationStage setUpdateSnapshots(boolean updateSnapshots);

     /**
      * Sets the failure mode of the Maven invocation. Equivalent of -ff and --fail-fast, -fae and --fail-at-end, -fn and --fail-never
      *
      * @param failureBehavior The failure mode, must be one of {@link REACTOR_FAIL_FAST}, {@link REACTOR_FAIL_AT_END} and {@link REACTOR_FAIL_NEVER}.
      * @return Modified instance of EmbeddedMaven
      */
     ConfigurationStage setFailureBehavior(String failureBehavior);

     /**
      * Dynamically constructs a reactor using the subdirectories of the current directory
      *
      * @param includes a list of filename patterns to include, or null, in which case the default is *&#47;pom.xml
      * @param excludes a list of filename patterns to exclude, or null, in which case nothing is excluded
      * @return Modified instance of EmbeddedMaven
      */
     ConfigurationStage activateReactor(String[] includes, String[] excludes);

     /**
      * Sets the path to the base directory of the local repository to use for the Maven invocation.
      *
      * @param localRepositoryDirectory The path to the base directory of the local repository, may be null.
      * @return Modified instance of EmbeddedMaven
      */
     ConfigurationStage setLocalRepositoryDirectory(File localRepositoryDirectory);

     /**
      * Sets the input stream used to provide input for the invoked Maven build. This is in particular useful when invoking Maven in interactive mode.
      *
      * @param invokerLogger The input stream used to provide input for the invoked Maven build, may be null if not required.
      * @return Modified instance of EmbeddedMaven
      */
     ConfigurationStage setLogger(InvokerLogger invokerLogger);

     /**
      * Sets the working directory for the Maven invocation.
      *
      * @param workingDirectory The working directory for the Maven invocation, may be null to derive the working directory from the base directory of the processed POM.
      * @return Modified instance of EmbeddedMaven
      */
     ConfigurationStage setWorkingDirectory(File workingDirectory);

     /**
      * Sets the input stream used to provide input for the invoked Maven build. This is in particular useful when invoking Maven in interactive mode.
      *
      * @param inputStream The input stream used to provide input for the invoked Maven build, may be null if not required.
      * @return Modified instance of EmbeddedMaven
      */
     ConfigurationStage setInputStream(InputStream inputStream);

     /**
      * Sets the path to the base directory of the Java installation used to run Maven.
      *
      * @param javaHome The path to the base directory of the Java installation used to run Maven, may be null to use the default Java home.
      * @return Modified instance of EmbeddedMaven
      */
     ConfigurationStage setJavaHome(File javaHome);

     /**
      * Sets the system properties for the Maven invocation.
      *
      * @param properties The system properties for the Maven invocation, may be null if not set.
      * @return Modified instance of EmbeddedMaven
      */
     ConfigurationStage setProperties(Properties properties);

     /**
      * Adds the property to the list of properties.
      *
      * @param key The key of the property to be added
      * @param value The value of the property to be added
      * @return Modified instance of EmbeddedMaven
      */
     ConfigurationStage addProperty(String key, String value);

     /**
      * Specifies whether the test of the project should be skipped during Maven build - Equivalent of --skipTests.
      * Default is <code>true</code>
      *
      * @param skipTests false the test of the project should be invoked
      * @return Modified instance of EmbeddedMaven
      */
     ConfigurationStage skipTests(boolean skipTests);

     /**
      * Sets the goals for the Maven invocation.
      *
      * @param goals The goals for the Maven invocation, may be null to execute the POMs default goal.
      * @return Modified instance of EmbeddedMaven
      */
     ConfigurationStage setGoals(List<String> goals);

     /**
      * Sets the goals for the Maven invocation.
      *
      * @param goals The goals for the Maven invocation, may be null to execute the POMs default goal.
      * @return Modified instance of EmbeddedMaven
      */
     ConfigurationStage setGoals(String... goals);

     /**
      * Sets the profiles for the Maven invocation. Equivalent of -P and --active-profiles
      *
      * @param profiles The profiles for the Maven invocation, may be null to use the default profiles.
      * @return Modified instance of EmbeddedMaven
      */
     ConfigurationStage setProfiles(List<String> profiles);

     /**
      * Sets the profiles for the Maven invocation. Equivalent of -P and --active-profiles
      *
      * @param profiles The profiles for the Maven invocation, may be null to use the default profiles.
      * @return Modified instance of EmbeddedMaven
      */
     ConfigurationStage setProfiles(String... profiles);

     /**
      * Specifies whether the environment variables of the current process should be propagated to the Maven invocation.
      *
      * @param shellEnvironmentInherited true if the environment variables should be propagated, false otherwise.
      * @return Modified instance of EmbeddedMaven
      */
     ConfigurationStage setShellEnvironmentInherited(boolean shellEnvironmentInherited);

     /**
      * Sets the path to the user settings for the Maven invocation. Equivalent of -s and --settings
      *
      * @param userSettingsFile The path to the user settings for the Maven invocation, may be null to load the user settings from the default location.
      * @return Modified instance of EmbeddedMaven
      */
     ConfigurationStage setUserSettingsFile(File userSettingsFile);

     /**
      * Sets the path to the global settings for the Maven invocation. Equivalent of -gs and --global-settings
      *
      * @param globalSettingsFile The path to the global settings for the Maven invocation, may be null to load the global settings from the default location.
      * @return Modified instance of EmbeddedMaven
      */
     ConfigurationStage setGlobalSettingsFile(File globalSettingsFile);

     /**
      * Sets the alternate path for the user toolchains file Equivalent of -t or --toolchains
      *
      * @param toolchainsFile the alternate path for the user toolchains file
      * @return Modified instance of EmbeddedMaven
      */
     ConfigurationStage setToolchainsFile(File toolchainsFile);

     /**
      * Sets the checksum mode of the Maven invocation. Equivalent of -c or --lax-checksums, -C or --strict-checksums
      *
      * @param globalChecksumPolicy The checksum mode, must be one of ${@link CHECKSUM_POLICY_WARN} and ${@link CHECKSUM_POLICY_FAIL}.
      * @return Modified instance of EmbeddedMaven
      */
     ConfigurationStage setGlobalChecksumPolicy(String globalChecksumPolicy);

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
     ConfigurationStage setNonPluginUpdates(boolean nonPluginUpdates);

     /**
      * Sets the recursion behavior of a reactor invocation. Inverse equivalent of -N and --non-recursive
      *
      * @param recursive true if sub modules should be build, false otherwise.
      * @return Modified instance of EmbeddedMaven
      */
     ConfigurationStage setRecursive(boolean recursive);

     /**
      * Adds the specified environment variable to the Maven invocation.
      *
      * @param name The name of the environment variable, must not be null.
      * @param value The value of the environment variable, must not be null.
      * @return Modified instance of EmbeddedMaven
      */
     ConfigurationStage addShellEnvironment(String name, String value);

     /**
      * Sets the value of the MAVEN_OPTS environment variable.
      *
      * @param mavenOpts The value of the MAVEN_OPTS environment variable, may be null to use the default options.
      * @return Modified instance of EmbeddedMaven
      */
     ConfigurationStage setMavenOpts(String mavenOpts);

     /**
      * enable displaying version without stopping the build Equivalent of -V or --show-version
      *
      * @param showVersion enable displaying version
      * @return Modified instance of EmbeddedMaven
      */
     ConfigurationStage setShowVersion(boolean showVersion);

     /**
      * Thread count, for instance 2.0C where C is core multiplied Equivalent of -T or --threads
      *
      * @param threads the threadcount
      * @return Modified instance of EmbeddedMaven
      */
     ConfigurationStage setThreads(String threads);

     /**
      * Sets the reactor project list. Equivalent of -P or --projects
      *
      * @param projects the reactor project list
      * @return Modified instance of EmbeddedMaven
      */
     ConfigurationStage setProjects(List<String> projects);

     /**
      * Enable the 'also make' mode. Equivalent of -am or --also-make
      *
      * @param alsoMake enable 'also make' mode
      * @return Modified instance of EmbeddedMaven
      */
     ConfigurationStage setAlsoMake(boolean alsoMake);

     /**
      * Enable the 'also make dependents' mode. Equivalent of -amd or --also-make-dependents
      *
      * @param alsoMakeDependents enable 'also make' mode
      * @return Modified instance of EmbeddedMaven
      */
     ConfigurationStage setAlsoMakeDependents(boolean alsoMakeDependents);

     /**
      * Resume reactor from specified project. Equivalent of -rf or --resume-from
      *
      * @param resumeFrom set the project to resume from
      * @return Modified instance of EmbeddedMaven
      */
     ConfigurationStage setResumeFrom(String resumeFrom);
}
