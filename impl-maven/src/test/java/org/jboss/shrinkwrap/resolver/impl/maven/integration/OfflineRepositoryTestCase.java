/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.shrinkwrap.resolver.impl.maven.integration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.shrinkwrap.resolver.api.NoResolvedResultException;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.ScopeType;
import org.jboss.shrinkwrap.resolver.impl.maven.bootstrap.MavenSettingsBuilder;
import org.jboss.shrinkwrap.resolver.impl.maven.util.TestFileUtil;
import org.jboss.shrinkwrap.resolver.impl.maven.util.ValidationUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.AbstractHandler;

/**
 * Tests resolution of the artifacts without enabling any remote repository
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 */
public class OfflineRepositoryTestCase {
    private static final Logger log = Logger.getLogger(OfflineRepositoryTestCase.class.getName());

    private static final int HTTP_TEST_PORT = 12345;

    private static final String JETTY_REPOSITORY = "target/jetty-repository";

    private static final String OFFLINE_REPOSITORY = "target/offline-repository";

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @BeforeClass
    public static void initialize() {
        System.clearProperty(MavenSettingsBuilder.ALT_LOCAL_REPOSITORY_LOCATION); // May conflict with release settings
    }

    /**
     * Cleanup, remove the repositories from previous tests
     */
    @Before
    public void cleanup() throws IOException {
        TestFileUtil.removeDirectory(new File(JETTY_REPOSITORY));
        TestFileUtil.removeDirectory(new File(OFFLINE_REPOSITORY));
    }

    /**
     * Goes offline from settings.xml
     *
     */
    @Test(expected = NoResolvedResultException.class)
    public void searchJunitOnOffineSettingsTest() {
        Maven.configureResolver().fromFile("target/settings/profiles/settings-offline.xml")
                .resolve("junit:junit:3.8.2").withTransitivity().as(File.class);
    }

    /**
     * Goes offline if specified by user
     */
    @Test
    public void offlineProgramatically() throws IOException {

        final String settingsFile = "target/settings/profiles/settings-jetty.xml";
        final String artifactWhichShouldNotResolve = "junit:junit:3.8.2";

        // Precondition; we can resolve when connected
        final File file = Maven.configureResolver().fromFile(settingsFile).resolve(artifactWhichShouldNotResolve)
                .withTransitivity().asSingle(File.class);
        new ValidationUtil("junit-3.8.2.jar").validate(file);

        // Manually cleanup; we're gonna run a test again
        this.cleanup();

        // Now try in offline mode and ensure we cannot resolve
        exception.expect(NoResolvedResultException.class);
        Maven.configureResolver().fromFile(settingsFile).offline().resolve(artifactWhichShouldNotResolve)
                .withTransitivity().asSingle(File.class);
    }

    /**
     * Goes offline with .pom based resolver
     */
    @Test
    public void offlineProgramaticallyPomBased() throws IOException {
        // set local repository to point to offline repository
        System.setProperty(MavenSettingsBuilder.ALT_LOCAL_REPOSITORY_LOCATION, OFFLINE_REPOSITORY);
        try {
            final String pomFile = "poms/test-parent.xml";

            // Precondition; we can resolve when connected
            final File[] files = Maven.resolver().loadPomFromClassLoaderResource(pomFile).importRuntimeDependencies()
                    .resolve().withTransitivity().as(File.class);
            ValidationUtil.fromDependencyTree(new File("src/test/resources/dependency-trees/test-parent.tree"),
                    ScopeType.COMPILE, ScopeType.RUNTIME).validate(files);

            // Manually cleanup; we're gonna run a test again
            this.cleanup();

            // Now try in offline mode and ensure we cannot resolve because we cannot hit repository defined in pom.xml (working
            // offline) and local repository was cleaned
            exception.expect(NoResolvedResultException.class);
            Maven.resolver().offline().loadPomFromClassLoaderResource(pomFile).importRuntimeDependencies().resolve()
                    .withTransitivity().as(File.class);
        } finally {
            System.clearProperty(MavenSettingsBuilder.ALT_LOCAL_REPOSITORY_LOCATION);
        }
    }

    /**
     * Goes offline if specified by system property
     */
    @Test(expected = NoResolvedResultException.class)
    public void offlineBySysProp() {
        System.setProperty(MavenSettingsBuilder.ALT_MAVEN_OFFLINE, "true");
        try {
            Maven.configureResolver().fromFile("target/settings/profiles/settings-jetty.xml")
                    .resolve("junit:junit:3.8.2").withTransitivity().as(File.class);
            Assert.fail("Artifact junit:junit:3.8.2 should not be present in local repository");
        } finally {
            System.clearProperty(MavenSettingsBuilder.ALT_MAVEN_OFFLINE);
        }
    }

    @Test
    public void searchWithRemoteOffAndOn() {
        // offline
        try {
            System.setProperty(MavenSettingsBuilder.ALT_MAVEN_OFFLINE, "true");

            Maven.configureResolver().fromFile("target/settings/profiles/settings-jetty.xml")
                    .resolve("org.jboss.shrinkwrap.test:test-deps-i:1.0.0").withTransitivity().asSingle(File.class);

            Assert.fail("Artifact org.jboss.shrinkwrap.test:test-deps-i:1.0.0 is not present in local repository");

        } catch (NoResolvedResultException e) {
            // this is ignored, we switch to online mode
        }

        // online
        Server server = startHttpServer();

        System.clearProperty(MavenSettingsBuilder.ALT_MAVEN_OFFLINE);

        Maven.configureResolver().fromFile("target/settings/profiles/settings-jetty.xml")
                .resolve("org.jboss.shrinkwrap.test:test-deps-i:1.0.0").withTransitivity().asSingle(File.class);
        shutdownHttpServer(server);

        // offline with artifact in local repository
        System.setProperty(MavenSettingsBuilder.ALT_MAVEN_OFFLINE, "true");

        Maven.configureResolver().fromFile("target/settings/profiles/settings-jetty.xml")
                .resolve("org.jboss.shrinkwrap.test:test-deps-i:1.0.0").withTransitivity().asSingle(File.class);

        System.clearProperty(MavenSettingsBuilder.ALT_MAVEN_OFFLINE);
    }

    private Server startHttpServer() {
        // Start an Embedded HTTP Server
        final Handler handler = new StaticFileHandler();
        final Server httpServer = new Server(HTTP_TEST_PORT);
        httpServer.setHandler(handler);
        try {
            httpServer.start();
            log.info("HTTP Server Started: " + httpServer);
            return httpServer;
        } catch (final Exception e) {
            throw new RuntimeException("Could not start server");
        }
    }

    private void shutdownHttpServer(Server httpServer) {
        if (httpServer != null) {
            try {
                httpServer.stop();
            } catch (final Exception e) {
                // Swallow
                log.severe("Could not stop HTTP Server cleanly, " + e.getMessage());
            }
            log.info("HTTP Server Stopped: " + httpServer);
        }
    }

    /**
     * Jetty Handler to serve a static character file from the web root
     */
    private static class StaticFileHandler extends AbstractHandler implements Handler {
        /*
         * (non-Javadoc)
         *
         * @see org.mortbay.jetty.Handler#handle(java.lang.String, javax.servlet.http.HttpServletRequest,
         * javax.servlet.http.HttpServletResponse, int)
         */
        @Override
        public void handle(final String target, final HttpServletRequest request, final HttpServletResponse response,
                final int dispatch) throws IOException, ServletException {
            // Set content type and status before we write anything to the stream
            response.setContentType("text/xml");
            response.setStatus(HttpServletResponse.SC_OK);

            // Obtain the requested file relative to the webroot
            final URL root = getCodebaseLocation();
            final URL fileUrl = new URL(root.toExternalForm() + target);
            URI uri = null;
            try {
                uri = fileUrl.toURI();
            } catch (final URISyntaxException urise) {
                throw new RuntimeException(urise);
            }
            final File file = new File(uri);

            // File not found, so 404
            if (!file.exists()) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                log.warning("Requested file is not found: " + file);
                return;
            }

            // Write out each line
            final BufferedReader reader = new BufferedReader(new FileReader(file));
            final PrintWriter writer = response.getWriter();
            String line = null;
            while ((line = reader.readLine()) != null) {
                writer.println(line);
            }

            // Close 'er up
            writer.flush();
            reader.close();
            writer.close();
        }

        private URL getCodebaseLocation() throws MalformedURLException {
            return new File("target/repository").toURI().toURL();
        }

    }

}
