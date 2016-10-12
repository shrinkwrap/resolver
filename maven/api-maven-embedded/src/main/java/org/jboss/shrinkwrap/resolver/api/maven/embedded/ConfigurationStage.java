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
import java.util.List;
import java.util.Properties;

import org.apache.maven.shared.invoker.InvokerLogger;

/**
 * @author <a href="mailto:mjobanek@gmail.com">Matous Jobanek</a>
 */
public interface ConfigurationStage extends BuildStage {

     ConfigurationStage setInteractive(boolean interactive);

     ConfigurationStage setOffline(boolean offline);

     ConfigurationStage setDebug(boolean debug);

     ConfigurationStage setShowErrors(boolean showErrors);

     ConfigurationStage setUpdateSnapshots(boolean updateSnapshots);

     ConfigurationStage setFailureBehavior(String failureBehavior);

     ConfigurationStage activateReactor(String[] includes, String[] excludes);

     ConfigurationStage setLocalRepositoryDirectory(File localRepositoryDirectory);

     ConfigurationStage setLogger(InvokerLogger invokerLogger);

     ConfigurationStage setWorkingDirectory(File workingDirectory);

     ConfigurationStage setJavaHome(File javaHome);

     ConfigurationStage setProperties(Properties properties);

     ConfigurationStage addProperty(String key, String value);

     ConfigurationStage skipTests(boolean skipTests);

     ConfigurationStage setGoals(List<String> goals);

     ConfigurationStage setGoals(String... goals);

     ConfigurationStage setProfiles(List<String> profiles);

     ConfigurationStage setProfiles(String... profiles);

     ConfigurationStage setShellEnvironmentInherited(boolean shellEnvironmentInherited);

     ConfigurationStage setUserSettingsFile(File userSettingsFile);

     ConfigurationStage setGlobalSettingsFile(File globalSettingsFile);

     ConfigurationStage setToolchainsFile(File toolchainsFile);

     ConfigurationStage setGlobalChecksumPolicy(String globalChecksumPolicy);

     ConfigurationStage setNonPluginUpdates(boolean nonPluginUpdates);

     ConfigurationStage setRecursive(boolean recursive);

     ConfigurationStage addShellEnvironment(String name, String value);

     ConfigurationStage setMavenOpts(String mavenOpts);

     ConfigurationStage setShowVersion(boolean showVersion);

     ConfigurationStage setThreads(String threads);

     ConfigurationStage setProjects(List<String> projects);

     ConfigurationStage setAlsoMake(boolean alsoMake);

     ConfigurationStage setAlsoMakeDependents(boolean alsoMakeDependents);

     ConfigurationStage setResumeFrom(String resumeFrom);
}
