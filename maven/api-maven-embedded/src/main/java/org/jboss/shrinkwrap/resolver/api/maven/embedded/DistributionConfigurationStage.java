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

/**
 * @author <a href="mailto:mjobanek@gmail.com">Matous Jobanek</a>
 */
public interface DistributionConfigurationStage<NEXT_STEP> extends BuildStage{

    /**
     * Configures ShrinkWrap Resolver to build project with Maven 3 of given version. It will be downloaded from Apache web pages.
     *
     * @param version Version which will be used
     */
    NEXT_STEP useMaven3Version(final String version);

    /**
     * Use specified Maven distribution. It will be downloaded from given address.
     *
     * @param mavenDistribution Maven distribution which will be used
     */
    NEXT_STEP useDistribution(final URL mavenDistribution);

    /**
     * Use specified Maven installation.
     *
     * @param mavenHome Maven distribution which will be used
     */
    NEXT_STEP useInstallation(final File mavenHome);

    /**
     * Use default Maven distribution that is on your path.
     */
    NEXT_STEP  useDefaultDistribution();

}
