/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
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
package org.jboss.shrinkwrap.resolver.impl.maven.internal;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.repository.WorkspaceReader;
import org.sonatype.aether.repository.WorkspaceRepository;

/**
 * ClasspathWorkspaceReader
 *
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @version $Revision: $
 */
public class ClasspathWorkspaceReader implements WorkspaceReader {
    private static final Logger log = Logger.getLogger(ClasspathWorkspaceReader.class.getName());

    @Override
    public WorkspaceRepository getRepository() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public File findArtifact(Artifact artifact) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<String> findVersions(Artifact artifact) {
        // TODO Auto-generated method stub
        return null;
    }

}
