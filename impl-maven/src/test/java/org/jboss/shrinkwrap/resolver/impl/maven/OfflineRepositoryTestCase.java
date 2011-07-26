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

import junit.framework.Assert;

import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.ResolutionException;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;
import org.jboss.shrinkwrap.resolver.util.FileUtil;
import org.junit.Before;
import org.junit.Test;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.AbstractHandler;

/**
 * Tests resolution of the artifacts without enabling any remote repository
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 */
public class OfflineRepositoryTestCase
{
   private static final Logger log = Logger.getLogger(OfflineRepositoryTestCase.class.getName());

   private static final int HTTP_TEST_PORT = 12345;

   /**
    * Cleanup, remove the repositories from previous tests
    */
   @Before
   public void cleanup() throws Exception
   {
      FileUtil.removeDirectory(new File("target/jetty-repository"));
      FileUtil.removeDirectory(new File("target/offline-repository"));
   }

   /**
    * Goes offline from settings.xml
    *
    * @throws Exception
    */
   @Test
   public void searchJunitOnOffineSettingsTest() throws Exception
   {
      try
      {
         DependencyResolvers.use(MavenDependencyResolver.class)
               .configureFrom("target/settings/profiles/settings-offline.xml").artifact("junit:junit:3.8.2")
               .resolveAsFiles();
      }
      catch (ResolutionException e)
      {
         Assert.assertTrue("Unable to resolve an artifact", e.getMessage().startsWith("Unable to resolve an artifact"));
      }
   }

   /**
    * Goes offline if specified by user
    *
    * @throws Exception
    */
   @Test
   public void searchJunitOnOffineProgrammaticTest() throws Exception
   {

      try
      {
         DependencyResolvers.use(MavenDependencyResolver.class).configureFrom("target/settings/profiles/settings.xml")
               .goOffline().artifact("junit:junit:3.8.2").resolveAsFiles();
         Assert.fail("Artifact junit:junit:3.8.2 is not present in local repository");
      }
      catch (ResolutionException e)
      {
         Assert.assertTrue("Unable to resolve an artifact", e.getMessage().startsWith("Unable to resolve an artifact"));
      }
   }

   /**
    * Goes offline if specified by system property
    *
    * @throws Exception
    */
   @Test
   public void searchJunitOnOffinePropertyTest() throws Exception
   {
      System.setProperty(MavenSettingsBuilder.ALT_MAVEN_OFFLINE, "true");

      try
      {
         DependencyResolvers.use(MavenDependencyResolver.class).configureFrom("target/settings/profiles/settings.xml")
               .artifact("junit:junit:3.8.2").resolveAsFiles();
         Assert.fail("Artifact junit:junit:3.8.2 is not present in local repository");
      }
      catch (ResolutionException e)
      {
         Assert.assertTrue("Unable to resolve an artifact", e.getMessage().startsWith("Unable to resolve an artifact"));
      }

      System.clearProperty(MavenSettingsBuilder.ALT_MAVEN_OFFLINE);
   }

   @Test
   public void searchWithRemoteOffAndOn() throws Exception
   {
      // offline
      try
      {
         DependencyResolvers.use(MavenDependencyResolver.class)
               .configureFrom("target/settings/profiles/settings-jetty.xml").goOffline()
               .artifact("org.jboss.shrinkwrap.test:test-deps-i:1.0.0").resolveAsFiles();
         Assert.fail("Artifact org.jboss.shrinkwrap.test:test-deps-i:1.0.0 is not present in local repository");

      }
      catch (ResolutionException e)
      {
         Assert.assertTrue("Unable to resolve an artifact", e.getMessage().startsWith("Unable to resolve an artifact"));
      }

      // online
      Server server = startHttpServer();
      File[] file = DependencyResolvers.use(MavenDependencyResolver.class)
            .configureFrom("target/settings/profiles/settings-jetty.xml")
            .artifact("org.jboss.shrinkwrap.test:test-deps-i:1.0.0").resolveAsFiles();
      shutdownHttpServer(server);
      Assert.assertEquals("One file was retrieved", 1, file.length);

      // offline with artifact in local repository
      file = DependencyResolvers.use(MavenDependencyResolver.class)
            .configureFrom("target/settings/profiles/settings-jetty.xml").goOffline()
            .artifact("org.jboss.shrinkwrap.test:test-deps-i:1.0.0").resolveAsFiles();

      Assert.assertEquals("One file was retrieved", 1, file.length);
   }

   private Server startHttpServer()
   {
      // Start an Embedded HTTP Server
      final Handler handler = new StaticFileHandler();
      final Server httpServer = new Server(HTTP_TEST_PORT);
      httpServer.setHandler(handler);
      try
      {
         httpServer.start();
         log.info("HTTP Server Started: " + httpServer);
         return httpServer;
      }
      catch (final Exception e)
      {
         throw new RuntimeException("Could not start server");
      }
   }

   private void shutdownHttpServer(Server httpServer)
   {
      if (httpServer != null)
      {
         try
         {
            httpServer.stop();
         }
         catch (final Exception e)
         {
            // Swallow
            log.severe("Could not stop HTTP Server cleanly, " + e.getMessage());
         }
         log.info("HTTP Server Stopped: " + httpServer);
      }
   }

   /**
    * Jetty Handler to serve a static character file from the web root
    */
   private static class StaticFileHandler extends AbstractHandler implements Handler
   {
      /*
       * (non-Javadoc)
       *
       * @see org.mortbay.jetty.Handler#handle(java.lang.String, javax.servlet.http.HttpServletRequest,
       * javax.servlet.http.HttpServletResponse, int)
       */
      @Override
      public void handle(final String target, final HttpServletRequest request, final HttpServletResponse response,
            final int dispatch) throws IOException, ServletException
      {
         // Set content type and status before we write anything to the stream
         response.setContentType("text/xml");
         response.setStatus(HttpServletResponse.SC_OK);

         // Obtain the requested file relative to the webroot
         final URL root = getCodebaseLocation();
         final URL fileUrl = new URL(root.toExternalForm() + target);
         URI uri = null;
         try
         {
            uri = fileUrl.toURI();
         }
         catch (final URISyntaxException urise)
         {
            throw new RuntimeException(urise);
         }
         final File file = new File(uri);

         // File not found, so 404
         if (!file.exists())
         {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            log.warning("Requested file is not found: " + file);
            return;
         }

         // Write out each line
         final BufferedReader reader = new BufferedReader(new FileReader(file));
         final PrintWriter writer = response.getWriter();
         String line = null;
         while ((line = reader.readLine()) != null)
         {
            writer.println(line);
         }

         // Close 'er up
         writer.flush();
         reader.close();
         writer.close();
      }

      private URL getCodebaseLocation() throws MalformedURLException
      {
         return new File("target/repository").toURI().toURL();
      }

   }

}
