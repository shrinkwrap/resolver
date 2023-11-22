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
package org.jboss.shrinkwrap.resolver.impl.maven.internal.decrypt;

import java.util.Collections;
import java.util.List;

import org.apache.maven.settings.Proxy;
import org.apache.maven.settings.Server;
import org.apache.maven.settings.building.SettingsProblem;
import org.apache.maven.settings.crypto.SettingsDecryptionResult;

/**
 * Basic envelope for MavenSettingsDecryption result
 *
 * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
 *
 */
class MavenSettingsDecryptionResult implements SettingsDecryptionResult {

    private final List<Server> servers;

    private final List<Proxy> proxies;

    private final List<SettingsProblem> problems;

    MavenSettingsDecryptionResult(List<Server> servers, List<Proxy> proxies, List<SettingsProblem> problems) {
        this.servers = servers == null ? Collections.emptyList() : servers;
        this.proxies = proxies == null ? Collections.emptyList() : proxies;
        this.problems = problems == null ? Collections.emptyList() : problems;
    }

    @Override
    public Server getServer() {
        return servers.isEmpty() ? null : servers.get(0);
    }

    @Override
    public List<Server> getServers() {
        return servers;
    }

    @Override
    public Proxy getProxy() {
        return proxies.isEmpty() ? null : proxies.get(0);
    }

    @Override
    public List<Proxy> getProxies() {
        return proxies;
    }

    @Override
    public List<SettingsProblem> getProblems() {
        return problems;
    }

}
