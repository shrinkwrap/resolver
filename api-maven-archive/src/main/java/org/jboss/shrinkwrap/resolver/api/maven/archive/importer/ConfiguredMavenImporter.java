/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.shrinkwrap.resolver.api.maven.archive.importer;

/**
 * Instance of {@link MavenImporter} that has configuration from a settings.xml file loaded
 *
 * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
 *
 */
public interface ConfiguredMavenImporter extends PomlessMavenImporter {

    /**
     * Sets whether resolution should be done in "offline" (ie. not connected to Internet) mode.
     *
     * @param offline
     * @return
     */
    PomlessMavenImporter offline(boolean offline);

    /**
     * Sets that resolution should be done in "offline" (ie. not connected to Internet) mode. Alias to
     * {@link ConfiguredMavenImporter#offline(boolean)}, passing <code>true</code> as a parameter.
     *
     * @return
     */
    PomlessMavenImporter offline();
}
