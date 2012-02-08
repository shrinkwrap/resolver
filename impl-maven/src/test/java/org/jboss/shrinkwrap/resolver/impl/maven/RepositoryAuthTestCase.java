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
package org.jboss.shrinkwrap.resolver.impl.maven;

import junit.framework.Assert;
import org.apache.commons.codec.binary.Base64;
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;
import org.jboss.shrinkwrap.resolver.util.FileUtil;
import org.junit.Before;
import org.junit.Test;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.logging.Logger;

/**
 * Tests resolution of the artifacts witch remote repository protected by password
 * 
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 */
public class RepositoryAuthTestCase {
    private static final Logger log = Logger.getLogger(RepositoryAuthTestCase.class.getName());

    private static final int HTTP_TEST_PORT = 12345;

    /**
     * Cleanup, remove the repositories from previous tests
     */
    @Before
    public void cleanup() throws Exception {
        FileUtil.removeDirectory(new File("target/auth-repository"));
    }

    @Test
    public void searchRemoteWithPassword() throws Exception {
        // online
        Server server = startHttpServer();
        File[] file = DependencyResolvers.use(MavenDependencyResolver.class)
                .configureFrom("target/settings/profiles/settings-auth.xml")
                .artifact("org.jboss.shrinkwrap.test:test-deps-i:1.0.0").resolveAsFiles();
        shutdownHttpServer(server);
        Assert.assertEquals("One file was retrieved", 1, file.length);

        // offline with artifact in local repository
        file = DependencyResolvers.use(MavenDependencyResolver.class)
                .configureFrom("target/settings/profiles/settings-auth.xml").goOffline()
                .artifact("org.jboss.shrinkwrap.test:test-deps-i:1.0.0").resolveAsFiles();

        Assert.assertEquals("One file was retrieved", 1, file.length);
    }

    private Server startHttpServer() {
        // Start an Embedded HTTP Server
        final Handler handler = new AuthStaticFileHandler("shrinkwrap", "shrinkwrap");
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
     * Jetty Handler to serve a static character file from the web root with Authorization check
     */
    private static class AuthStaticFileHandler extends AbstractHandler implements Handler {

        private static final String AUTH_HEADER = "Authorization";

        private String user;
        private String password;

        public AuthStaticFileHandler(String user, String password) {
            super();
            this.user = user;
            this.password = password;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.mortbay.jetty.Handler#handle(java.lang.String, javax.servlet.http.HttpServletRequest,
         * javax.servlet.http.HttpServletResponse, int)
         */
        @Override
        public void handle(final String target, final HttpServletRequest request, final HttpServletResponse response,
                final int dispatch) throws IOException, ServletException {

            log.fine("Authorizing request for artifact");
            String authHeader = request.getHeader(AUTH_HEADER);
            if (authHeader == null || authHeader.length() == 0) {
                log.warning("Unauthorized access, please provide credentials");
                response.addHeader("WWW-Authenticate", "Basic realm=\"Secure Area\"");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized access, please provide credentials");
                return;
            }

            if (!authorize(request)) {
                log.warning("Invalid credentials");
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid credentials");
                return;
            }

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

        private boolean authorize(HttpServletRequest request) {
            String authHeader = request.getHeader(AUTH_HEADER);

            // Basic auth
            if (authHeader != null && authHeader.startsWith("Basic")) {
                String credentials = user + ":" + password;

                String challenge = "Basic "
                        + new String(Base64.encodeBase64(credentials.getBytes(Charset.defaultCharset())),
                                Charset.defaultCharset());

                return authHeader.equals(challenge);
            }

            return false;
        }
    }

}
