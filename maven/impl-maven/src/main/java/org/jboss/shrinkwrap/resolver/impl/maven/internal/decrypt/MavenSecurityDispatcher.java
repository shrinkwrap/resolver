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

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.shrinkwrap.resolver.api.InvalidConfigurationFileException;
import org.jboss.shrinkwrap.resolver.impl.maven.bootstrap.MavenSettingsBuilder;
import org.jboss.shrinkwrap.resolver.impl.maven.util.Validate;
import org.sonatype.plexus.components.cipher.PlexusCipher;
import org.sonatype.plexus.components.cipher.PlexusCipherException;
import org.sonatype.plexus.components.sec.dispatcher.SecDispatcher;
import org.sonatype.plexus.components.sec.dispatcher.SecDispatcherException;
import org.sonatype.plexus.components.sec.dispatcher.SecUtil;
import org.sonatype.plexus.components.sec.dispatcher.model.SettingsSecurity;

/**
 * Inspired by DefaultSecDispatched from Sonatype
 *
 * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
 * @author Oleg Gusakov
 *
 */
class MavenSecurityDispatcher implements SecDispatcher {
    private static final Logger log = Logger.getLogger(MavenSecurityDispatcher.class.getName());

    private static final String DEFAULT_PASSPHRASE = "settings.security";

    private static final String TYPE_ATTR = "type";

    private static final char ATTR_START = '[';

    private static final char ATTR_STOP = ']';

    private final PlexusCipher cipher;

    private SettingsSecurity securitySettings;

    private final File securitySettingsPath;

    MavenSecurityDispatcher(File securitySettings) throws InvalidConfigurationFileException {
        this.cipher = new MavenPlexusCipher();
        this.securitySettingsPath = securitySettings;
        // settings-security is loaded only if it exists
        // error is raised later only in case that it is missing but needed
        if (Validate.isReadable(securitySettings.getAbsoluteFile())) {
            try {
                this.securitySettings = SecUtil.read(securitySettings.getAbsolutePath(), true);
            } catch (SecDispatcherException e) {
                // exception is ignored, just logged to end user, so he's aware of the problem
                // this is default Maven behavior
                log.log(Level.WARNING, "Unable to read security configuration from: "
                        + securitySettings.getAbsolutePath() + ". Configuration will be ignored.", e);
            }
        }
    }

    @Override
    public String decrypt(String str) throws SecDispatcherException {
        if (!isEncryptedString(str)) {
            return str;
        }

        String bare = null;

        try {
            bare = cipher.unDecorate(str);
        } catch (PlexusCipherException e1) {
            throw new SecDispatcherException(e1);
        }

        Map<String, String> attr = stripAttributes(bare);

        String res = null;

        if (attr == null || attr.get("type") == null) {
            String master = getMaster();
            try {
                res = cipher.decrypt(bare, master);
            } catch (PlexusCipherException e) {
                throw new SecDispatcherException("Unable to decrypt encrypted string", e);
            }
        }
        else {
            String type = attr.get(TYPE_ATTR);
            throw new UnsupportedOperationException("Unable to lookup security dispatched of type " + type);
        }

        return res;
    }

    private Map<String, String> stripAttributes(String str) {
        int start = str.indexOf(ATTR_START);
        int stop = str.indexOf(ATTR_STOP);

        if (start != -1 && stop != -1 && stop > start) {
            if (stop == start + 1) {
                return null;
            }

            String attrs = str.substring(start + 1, stop).trim();

            if (attrs == null || attrs.isEmpty()) {
                return null;
            }

            Map<String, String> res = null;
            StringTokenizer st = new StringTokenizer(attrs, ", ");

            while (st.hasMoreTokens()) {
                if (res == null) {
                    res = new HashMap<>(st.countTokens());
                }

                String pair = st.nextToken();

                int pos = pair.indexOf('=');

                if (pos == -1) {
                    continue;
                }

                String key = pair.substring(0, pos).trim();

                if (pos == pair.length()) {
                    res.put(key, null);
                    continue;
                }

                String val = pair.substring(pos + 1);

                res.put(key, val.trim());
            }

            return res;
        }

        return null;
    }

    private boolean isEncryptedString(String str) {
        if (str == null) {
            return false;
        }

        return cipher.isEncryptedString(str);
    }

    private String getMaster() throws SecDispatcherException, InvalidConfigurationFileException {

        if (securitySettings == null) {
            throw new InvalidConfigurationFileException(
                    "Unable to get security configuration from " + securitySettingsPath.getPath()
                            + ". Please define path to the settings-security.xml file via -D" +
                            MavenSettingsBuilder.ALT_SECURITY_SETTINGS_XML_LOCATION
                            + ", or put it the the default location defined by Maven.");
        }
        String master = securitySettings.getMaster();

        if (master == null) {
            throw new InvalidConfigurationFileException("Security configuration from " + securitySettingsPath.getPath()
                    + " does not contain master password");
        }

        try {
            return cipher.decryptDecorated(master, DEFAULT_PASSPHRASE);
        } catch (PlexusCipherException e) {
            throw new SecDispatcherException(e);
        }
    }
}
