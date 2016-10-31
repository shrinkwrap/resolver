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
import org.jboss.shrinkwrap.resolver.api.maven.embedded.invoker.equipped.MavenInvokerEquippedEmbeddedMaven;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.invoker.equipped.MavenInvokerUnequippedEmbeddedMaven;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.pom.equipped.PomEquippedEmbeddedMaven;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.pom.equipped.PomUnequippedEmbeddedMaven;

/**
 * @author <a href="mailto:mjobanek@gmail.com">Matous Jobanek</a>
 */
public class EmbeddedMaven {

    /**
     * Specifies a POM file the EmbeddedMaven should be used for.
     * <p>
     * This method offers you to use a Resolver EmbeddedMaven API for additional easy setting of Maven Invoker that
     * is internally used.
     * </p>
     * <p>
     * If you prefer more powerful approach that is less comfortable and more boilerplate, then use the method
     * {@link #withMavenInvokerSet(InvocationRequest, Invoker)}
     * </p>
     *
     * @param pomFile POM file the EmbeddedMaven should be used for
     * @return Set EmbeddedMaven instance
     */
    public static PomEquippedEmbeddedMaven forProject(File pomFile) {
        PomUnequippedEmbeddedMaven embeddedMaven = Resolvers.use(PomUnequippedEmbeddedMaven.class);
        return embeddedMaven.setPom(pomFile);
    }

    /**
     * Specifies a POM file the EmbeddedMaven should be used for.
     * <p>
     *     This method offers you to use a Resolver EmbeddedMaven API for additional easy setting of Maven Invoker that
     *     is internally used.
     * </p>
     * <p>
     *     If you prefer more powerful approach that is less comfortable and more boilerplate, then use the method
     *     {@link #withMavenInvokerSet(InvocationRequest, Invoker)}
     * </p>
     *
     * @param pomFile POM file the EmbeddedMaven should be used for
     * @return  Set EmbeddedMaven instance
     */
    public static PomEquippedEmbeddedMaven forProject(String pomFile) {
        return forProject(new File(pomFile));
    }

    /**
     * Specifies an {@link InvocationRequest} and an {@link Invoker} the EmbeddedMaven should be used with.
     * <p>
     *     When you use this approach, it is expected that both instances are properly set by you and no additional
     *     parameters (such as -DskipTests) is added by Resolver. You can also observe some limited functionality
     *     provided by Resolver API.
     * </p>
     * <p>
     *     If you prefer more comfortable and less boilerplate approach, then use the method {@link #forProject(String)}
     * </p>
     *
     * @param request An {@link InvocationRequest} the EmbeddedMaven should be used with
     * @param invoker An {@link Invoker} the EmbeddedMaven should be used with
     * @return Set EmbeddedMaven instance
     */
    public static MavenInvokerEquippedEmbeddedMaven withMavenInvokerSet(InvocationRequest request, Invoker invoker) {
        MavenInvokerUnequippedEmbeddedMaven embeddedMaven = Resolvers.use(MavenInvokerUnequippedEmbeddedMaven.class);
        return embeddedMaven.setMavenInvoker(request, invoker);

    }
}
