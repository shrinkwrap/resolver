/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2010 Sonatype, Inc. All rights reserved.
 *
 * JBoss, Home of Professional Open Source
 * Copyright 2012, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.shrinkwrap.resolver.impl.maven.logging;

import java.util.logging.Logger;

import org.eclipse.aether.AbstractRepositoryListener;
import org.eclipse.aether.RepositoryEvent;

/**
 * A listener which reports Maven repository event to a logger.
 *
 * The logger is shared with {@link LogTransferListener}
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 *
 */
public class LogRepositoryListener extends AbstractRepositoryListener {
    private static final Logger log = Logger.getLogger(LogRepositoryListener.class.getName());

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.aether.util.listener.AbstractRepositoryListener#artifactDeployed
     * (org.eclipse.aether.RepositoryEvent)
     */
    @Override
    public void artifactDeployed(RepositoryEvent event) {
        log.fine("Deployed " + event.getArtifact() + " to " + event.getRepository());
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.aether.util.listener.AbstractRepositoryListener#artifactDeploying
     * (org.eclipse.aether.RepositoryEvent)
     */
    @Override
    public void artifactDeploying(RepositoryEvent event) {
        log.finer("Deploying " + event.getArtifact() + " to " + event.getRepository());
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.aether.util.listener.AbstractRepositoryListener#
     * artifactDescriptorInvalid(org.eclipse.aether.RepositoryEvent)
     */
    @Override
    public void artifactDescriptorInvalid(RepositoryEvent event) {
        log.warning("Invalid artifact descriptor for " + event.getArtifact() + ": " + event.getException().getMessage());
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.aether.util.listener.AbstractRepositoryListener#
     * artifactDescriptorMissing(org.eclipse.aether.RepositoryEvent)
     */
    @Override
    public void artifactDescriptorMissing(RepositoryEvent event) {
        log.warning("Missing artifact descriptor for " + event.getArtifact());
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.aether.util.listener.AbstractRepositoryListener#artifactInstalled
     * (org.eclipse.aether.RepositoryEvent)
     */
    @Override
    public void artifactInstalled(RepositoryEvent event) {
        log.fine("Installed " + event.getArtifact() + " to " + event.getFile());
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.aether.util.listener.AbstractRepositoryListener#
     * artifactInstalling(org.eclipse.aether.RepositoryEvent)
     */
    @Override
    public void artifactInstalling(RepositoryEvent event) {
        log.finer("Installing " + event.getArtifact() + " to " + event.getFile());
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.aether.util.listener.AbstractRepositoryListener#artifactResolved
     * (org.eclipse.aether.RepositoryEvent)
     */
    @Override
    public void artifactResolved(RepositoryEvent event) {
        log.fine("Resolved artifact " + event.getArtifact() + " from " + event.getRepository());
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.aether.util.listener.AbstractRepositoryListener#artifactResolving
     * (org.eclipse.aether.RepositoryEvent)
     */
    @Override
    public void artifactResolving(RepositoryEvent event) {
        log.finer("Resolving artifact " + event.getArtifact());
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.aether.util.listener.AbstractRepositoryListener#metadataDeployed
     * (org.eclipse.aether.RepositoryEvent)
     */
    @Override
    public void metadataDeployed(RepositoryEvent event) {
        log.fine("Deployed " + event.getMetadata() + " to " + event.getRepository());
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.aether.util.listener.AbstractRepositoryListener#metadataDeploying
     * (org.eclipse.aether.RepositoryEvent)
     */
    @Override
    public void metadataDeploying(RepositoryEvent event) {
        log.finer("Deploying " + event.getMetadata() + " to " + event.getRepository());
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.aether.util.listener.AbstractRepositoryListener#metadataInstalled
     * (org.eclipse.aether.RepositoryEvent)
     */
    @Override
    public void metadataInstalled(RepositoryEvent event) {
        log.fine("Installed " + event.getMetadata() + " to " + event.getFile());
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.aether.util.listener.AbstractRepositoryListener#
     * metadataInstalling(org.eclipse.aether.RepositoryEvent)
     */
    @Override
    public void metadataInstalling(RepositoryEvent event) {
        log.finer("Installing " + event.getMetadata() + " to " + event.getFile());
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.aether.util.listener.AbstractRepositoryListener#metadataInvalid
     * (org.eclipse.aether.RepositoryEvent)
     */
    @Override
    public void metadataInvalid(RepositoryEvent event) {
        log.warning("Invalid metadata " + event.getMetadata());
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.aether.util.listener.AbstractRepositoryListener#metadataResolved
     * (org.eclipse.aether.RepositoryEvent)
     */
    @Override
    public void metadataResolved(RepositoryEvent event) {
        log.fine("Resolved metadata " + event.getMetadata() + " from " + event.getRepository());
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.aether.util.listener.AbstractRepositoryListener#metadataResolving
     * (org.eclipse.aether.RepositoryEvent)
     */
    @Override
    public void metadataResolving(RepositoryEvent event) {
        log.finer("Resolving metadata " + event.getMetadata() + " from " + event.getRepository());
    }
}
