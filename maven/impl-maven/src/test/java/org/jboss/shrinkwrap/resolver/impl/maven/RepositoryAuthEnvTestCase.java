/*
 * JBoss, Home of Professional Open Source
 * Copyright 2015, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.shrinkwrap.resolver.impl.maven;

import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.impl.maven.util.Validate;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test case for SHRINKRES-227 - ShrinkWrap Maven Resolver doesn't support env vars in settings.xml.
 * There has been used the PATH as the env variable in this test case - it is expected to be available on all
 * platforms by default.
 * <p>
 * NOTE: This test case is in the main package because of the visibility of the method
 * {@link ConfigurableMavenWorkingSessionImpl#getSettings()}.
 * </p>
 *
 * @author <a href="mailto:mjobanek@redhat.com">Matous Jobanek</a>
 *
 */
public class RepositoryAuthEnvTestCase {

    private static String ENV_PROPERTY = "${env.PATH}";

    /**
     * There should be replaced the {@code ${env.PATH}} variable into the username and password elements
     */
    @Test
    public void envPathShouldBeReplaced() {
        MavenWorkingSessionContainer container = (MavenWorkingSessionContainer) Maven.configureResolver().fromFile(
            "target/settings/profiles/settings-auth-env.xml");

        ConfigurableMavenWorkingSessionImpl mavenWorkingSession =
            (MavenWorkingSessionImpl) container.getMavenWorkingSession();

        Settings settings = mavenWorkingSession.getSettings();
        Server server = settings.getServer("auth-repository");

        verifyIsEnvPathReplaced(server.getPassword());
        verifyIsEnvPathReplaced(server.getUsername());
    }

    private void verifyIsEnvPathReplaced(String value) {
        Validate.notNullOrEmpty(value, "The value should be neither null nor empty");
        Assert.assertNotEquals(ENV_PROPERTY, value);
    }
}
