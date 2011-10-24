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

import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;
import org.jboss.shrinkwrap.resolver.api.maven.MavenRepositoryBuilder;

/**
 *
 * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
 *
 */
class MavenRepositoryBuilderImpl implements MavenRepositoryBuilder, MavenEnvironmentRetrieval {

    private MavenEnvironment maven;

    public MavenRepositoryBuilderImpl(MavenEnvironment maven) {
        this.maven = maven;
    }

    @Override
    public MavenDependencyResolver up() {
        return new MavenDependencyResolverImpl(maven);
    }

    @Override
    public MavenRepositoryBuilder layout(String layout) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MavenRepositoryBuilder repository(String url) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MavenEnvironment getMavenEnvironment() {
        return maven;
    }

}
