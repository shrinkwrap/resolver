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

import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.Invoker;
import org.jboss.shrinkwrap.resolver.api.Resolvers;

/**
 * @author <a href="mailto:mjobanek@gmail.com">Matous Jobanek</a>
 */
public class EmbeddedMaven {

    public static PomEquippedEmbeddedMaven forProject(final File pomFile) {
        PomUnequippedEmbeddedMaven embeddedMaven = Resolvers.use(PomUnequippedEmbeddedMaven.class);
        return embeddedMaven.setPom(pomFile);
    }

    public static PomEquippedEmbeddedMaven forProject(final String pomFile) {
        return forProject(new File(pomFile));
    }

    public static MavenInvokerEquippedEmbeddedMaven withMavenInvokerSet(InvocationRequest request, Invoker invoker) {
        MavenInvokerUnequippedEmbeddedMaven embeddedMaven = Resolvers.use(MavenInvokerUnequippedEmbeddedMaven.class);
        return embeddedMaven.setMavenInvoker(request, invoker);

    }
}
