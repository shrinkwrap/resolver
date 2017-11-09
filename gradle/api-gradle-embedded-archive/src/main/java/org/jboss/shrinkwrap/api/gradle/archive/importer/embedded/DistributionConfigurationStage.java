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

package org.jboss.shrinkwrap.api.gradle.archive.importer.embedded;

import java.io.File;
import java.net.URI;

/**
 * @author <a href="mailto:mmatloka@gmail.com">Michal Matloka</a>
 */
public interface DistributionConfigurationStage extends ConfigurationStage {

    /**
     * Configures ShrinkWrap Resolver to build project with Gradle of given version.
     *
     * @param version
     *            Version which will be used
     */
    ConfigurationStage useGradleVersion(String version);

    /**
     * Use specified gradle distribution. It will be downloaded from given address.
     *
     * @param gradleDistribution
     *            Gradle distribution which will be used
     */
    ConfigurationStage useDistribution(URI gradleDistribution);

    /**
     * Use specified gradle installation.
     *
     * @param gradleHome
     *            Gradle distribution which will be used
     */
    ConfigurationStage useInstallation(File gradleHome);

    /**
     * Use default Gradle distribution.
     */
    ConfigurationStage useDefaultDistribution();

}
