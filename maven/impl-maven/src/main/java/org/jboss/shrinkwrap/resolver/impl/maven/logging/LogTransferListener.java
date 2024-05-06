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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.eclipse.aether.transfer.AbstractTransferListener;
import org.eclipse.aether.transfer.TransferEvent;
import org.eclipse.aether.transfer.TransferResource;

/**
 * A listener which reports Maven transfer events to a logger.
 * <p>
 * The logger is shared with {@link LogRepositoryListener}.
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 *
 */
public class LogTransferListener extends AbstractTransferListener {
    // set up new logger with output directed to standard out
    private static final Logger log = Logger.getLogger(LogTransferListener.class.getName());

    // a map of transferred data sizes for the last notification
    private final Map<TransferResource, Long> downloads = new ConcurrentHashMap<>();

    // a minimal amount of data transferred for an artifact required to inform
    // the user
    private static final long TRANSFER_THRESHOLD = 1024 * 50;

    /*
     * (non-Javadoc)
     *
     * @see org.sonatype.aether.util.listener.AbstractTransferListener#transferInitiated
     * (org.sonatype.aether.transfer.TransferEvent)
     */
    @Override
    public void transferInitiated(TransferEvent event) {
        TransferResource resource = event.getResource();

        StringBuilder sb = new StringBuilder()
            .append(event.getRequestType() == TransferEvent.RequestType.PUT ? "Uploading" : "Downloading").append(":")
            .append(resource.getRepositoryUrl()).append(resource.getResourceName());

        downloads.put(resource, 0L);
        log.fine(sb.toString());
    }

    /*
     * (non-Javadoc)
     *
     * @see org.sonatype.aether.util.listener.AbstractTransferListener#transferProgressed
     * (org.sonatype.aether.transfer.TransferEvent)
     */
    @Override
    public void transferProgressed(TransferEvent event) {
        TransferResource resource = event.getResource();

        long lastTransferred = downloads.get(resource);
        long transferred = event.getTransferredBytes();

        if (transferred - lastTransferred >= TRANSFER_THRESHOLD) {
            downloads.put(resource, transferred);
            long total = resource.getContentLength();
            log.finer(getStatus(transferred, total) + ", ");
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.sonatype.aether.util.listener.AbstractTransferListener#transferSucceeded
     * (org.sonatype.aether.transfer.TransferEvent)
     */
    @Override
    public void transferSucceeded(TransferEvent event) {
        TransferResource resource = event.getResource();

        downloads.remove(resource);

        long contentLength = event.getTransferredBytes();
        if (contentLength >= 0) {
            long duration = System.currentTimeMillis() - resource.getTransferStartTime();
            double kbPerSec = (contentLength / 1024.0) / (duration / 1000.0);

            StringBuilder sb = new StringBuilder().append("Completed")
                .append(event.getRequestType() == TransferEvent.RequestType.PUT ? " upload of " : " download of ")
                .append(resource.getResourceName())
                .append(event.getRequestType() == TransferEvent.RequestType.PUT ? " into " : " from ")
                .append(resource.getRepositoryUrl()).append(", transferred ")
                .append(contentLength >= 1024 ? toKB(contentLength) + " KB" : contentLength + " B").append(" at ")
                .append(new DecimalFormat("0.0", new DecimalFormatSymbols(Locale.ENGLISH)).format(kbPerSec))
                .append("KB/sec");

            log.fine(sb.toString());
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.sonatype.aether.util.listener.AbstractTransferListener#transferFailed
     * (org.sonatype.aether.transfer.TransferEvent)
     */
    @Override
    public void transferFailed(TransferEvent event) {
        TransferResource resource = event.getResource();

        downloads.remove(resource);

        StringBuilder sb = new StringBuilder().append("Failed")
            .append(event.getRequestType() == TransferEvent.RequestType.PUT ? " uploading " : " downloading ")
            .append(resource.getResourceName())
            .append(event.getRequestType() == TransferEvent.RequestType.PUT ? " into " : " from ")
            .append(resource.getRepositoryUrl()).append(". ");

        if (event.getException() != null) {
            sb.append("Reason: \n").append(event.getException());
        }

        log.warning(sb.toString());
    }

    /*
     * (non-Javadoc)
     *
     * @see org.sonatype.aether.util.listener.AbstractTransferListener#transferCorrupted
     * (org.sonatype.aether.transfer.TransferEvent)
     */
    @Override
    public void transferCorrupted(TransferEvent event) {
        TransferResource resource = event.getResource();

        downloads.remove(resource);

        StringBuilder sb = new StringBuilder().append("Corrupted")
            .append(event.getRequestType() == TransferEvent.RequestType.PUT ? " upload of " : " download of ")
            .append(resource.getResourceName())
            .append(event.getRequestType() == TransferEvent.RequestType.PUT ? " into " : " from ")
            .append(resource.getRepositoryUrl()).append(". ");

        if (event.getException() != null) {
            sb.append("Reason: \n").append(event.getException());
        }

        log.warning(sb.toString());

    }

    // converts into status message
    private String getStatus(long complete, long total) {
        if (total >= 1024) {
            return toKB(complete) + "/" + toKB(total) + " KB";
        } else if (total >= 0) {
            return complete + "/" + total + " B";
        } else if (complete >= 1024) {
            return toKB(complete) + " KB";
        } else {
            return complete + " B";
        }
    }

    // converts bytes to kilobytes
    private long toKB(long bytes) {
        return (bytes + 1023) / 1024;
    }
}
