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

package org.jboss.shrinkwrap.resolver.impl.maven.embedded.pom.equipped;

import java.io.File;
import java.util.Properties;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.Invoker;
import org.jboss.shrinkwrap.impl.base.Validate;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.pom.equipped.ConfigurationDistributionStage;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.pom.equipped.ConfigurationStage;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.pom.equipped.PomEquippedEmbeddedMaven;

/**
 * @author <a href="mailto:mjobanek@gmail.com">Matous Jobanek</a>
 */
public class PomEquippedEmbeddedMavenImpl extends ConfigurationStageImpl implements PomEquippedEmbeddedMaven {

    protected final InvocationRequest request = new DefaultInvocationRequest();
    protected Invoker invoker = new DefaultInvoker();
    private StringBuffer logBuffer = new StringBuffer("");
    private boolean quiet = false;

    protected PomEquippedEmbeddedMavenImpl(File pomFile) {
        Validate.notNull(pomFile, "Pom file can not be null!");

        final File absoluteFile = pomFile.getAbsoluteFile();
        if (!absoluteFile.exists()) {
            throw new IllegalArgumentException("Given pom file does not exist: " + absoluteFile);
        } else if (!absoluteFile.isFile()) {
            throw new IllegalArgumentException("Given pom file is not a file" + absoluteFile);
        }

        request.setPomFile(absoluteFile);

        Properties properties = new Properties();
        properties.put("skipTests", "true");
        request.setProperties(properties);
    }

    @Override
    protected InvocationRequest getInvocationRequest() {
        return request;
    }

    @Override
    protected Invoker getInvoker() {
        return invoker;
    }

    @Override
    protected ConfigurationStage returnNextStepType() {
        return this;
    }

    protected StringBuffer getLogBuffer(){
        return logBuffer;
    }

    @Override
    public ConfigurationDistributionStage setQuiet(boolean quiet) {
        this.quiet = quiet;
        return this;
    }

    @Override
    public ConfigurationDistributionStage setQuiet() {
        setQuiet(true);
        return this;
    }

    @Override
    protected boolean isQuiet() {
        return quiet;
    }

    @Override
    public ConfigurationDistributionStage setAlternatePomFile(String pomFile) {
        if(pomFile != null) {
            File pom = new File(pomFile).getAbsoluteFile();
            if (pom.isDirectory()) {
                pom = new File(pom, "pom.xml");
            }
            getInvocationRequest().setPomFile(pom);
        }
        return this;
    }
}
