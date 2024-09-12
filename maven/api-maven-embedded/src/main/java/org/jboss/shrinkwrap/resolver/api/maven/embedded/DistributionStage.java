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
import java.net.URL;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.daemon.DaemonBuildTrigger;

/**
 * @author <a href="mailto:mjobanek@gmail.com">Matous Jobanek</a>
 */
public interface DistributionStage<NEXT_STEP extends BuildStage<DAEMON_TRIGGER_TYPE>, DAEMON_TRIGGER_TYPE extends DaemonBuildTrigger>
    extends BuildStage<DAEMON_TRIGGER_TYPE> {

    String DEFAULT_MAVEN_VERSION = "3.9.9";

    /**
     * Configures EmbeddedMaven to build project with Maven 3 of given version. If the zip file is not cached in directory
     * $HOME/.arquillian/resolver/maven/ then it will be downloaded from Apache web pages and zip file cached.
     *
     * @param version Version which will be used
     * @return Modified EmbeddedMaven instance
     */
    NEXT_STEP useMaven3Version(String version);

    /**
     * Configures EmbeddedMaven to build project with given Maven distribution. If you set {@code useCache} to {@code true}
     * then the cache directory $HOME/.arquillian/resolver/maven/ if checked for the presence of the zip file. If the zip file
     * is not present, then it will be downloaded from the given URL and the zip file cached.
     *
     * @param mavenDistribution Maven distribution which will be used
     * @param useCache Whether the cache directory $HOME/.arquillian/resolver/maven/ should be used
     * @return Modified EmbeddedMaven instance
     */
    NEXT_STEP useDistribution(URL mavenDistribution, boolean useCache);

    /**
     * Use specified Maven installation.
     *
     * @param mavenHome Maven distribution which will be used
     * @return Modified EmbeddedMaven instance
     */
    NEXT_STEP useInstallation(File mavenHome);

    /**
     * Use default Maven distribution with version {@value DEFAULT_MAVEN_VERSION}.
     *
     * @return Modified EmbeddedMaven instance
     */
    NEXT_STEP  useDefaultDistribution();

    /**
     * Use local Maven installation that is available on your PATH.
     *
     * @return Modified EmbeddedMaven instance
     */
    NEXT_STEP useLocalInstallation();
}
