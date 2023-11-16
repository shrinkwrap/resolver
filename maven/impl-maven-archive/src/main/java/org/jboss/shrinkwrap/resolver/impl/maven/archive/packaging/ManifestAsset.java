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
package org.jboss.shrinkwrap.resolver.impl.maven.archive.packaging;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.jar.Manifest;

import org.jboss.shrinkwrap.api.asset.StringAsset;

/**
 * An Asset that makes work with Java Archive manifest more convenient
 *
 * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
 *
 */
class ManifestAsset extends StringAsset {

    /**
     * Creates an asset that would contain manifest
     *
     * @param manifest the manifest to be transformed into an asset
     */
    ManifestAsset(Manifest manifest) {
        super(manifestAsString(manifest));
    }

    /**
     * Conversion method from Manifest to String.
     *
     * @param manifest Manifest to be transformed into String
     * @return String representation of the Manifest
     */
    private static String manifestAsString(Manifest manifest) {

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            manifest.write(baos);
            return baos.toString("UTF-8");
        } catch (IOException e) {
            throw new IllegalStateException("Unable to write MANIFEST.MF to an archive Asset", e);
        }
    }

    @Override
    public String toString() {
        return ManifestAsset.class.getSimpleName() + " [content " + getSource() + "]";
    }
}
