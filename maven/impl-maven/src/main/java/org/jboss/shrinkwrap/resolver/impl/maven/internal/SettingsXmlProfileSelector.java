/*
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
package org.jboss.shrinkwrap.resolver.impl.maven.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.maven.model.profile.ProfileSelector;
import org.jboss.shrinkwrap.resolver.impl.maven.util.Validate;

/**
 * {@link ProfileSelector} implementation backed by metadata defined by <code>settings.xml</code>
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 */
public class SettingsXmlProfileSelector {
    private SettingsXmlProfileSelector() {}

    // selects all profile ids to be activated
    public static List<String> explicitlyActivatedProfiles(String... profiles) {
        if (profiles.length == 0) {
            return Collections.<String> emptyList();
        }
        List<String> activated = new ArrayList<>();
        for (String profileId : profiles) {
            Validate.notNullOrEmpty(profileId, "Invalid name (\"" + profileId + "\") of a profile to be activated");
            if (!(profileId.startsWith("-") || profileId.startsWith("!"))) {
                activated.add(profileId);
            }
        }

        return activated;
    }

    // selects all profiles ids to be disabled
    public static List<String> explicitlyDisabledProfiles(String... profiles) {
        if (profiles.length == 0) {
            return Collections.<String> emptyList();
        }
        List<String> disabled = new ArrayList<>();
        for (String profileId : profiles) {
            if (profileId != null && (profileId.startsWith("-") || profileId.startsWith("!"))) {
                String disabledId = profileId.substring(1);
                Validate.notNullOrEmpty(disabledId, "Invalid name (\"" + profileId + "\") of a profile do be disabled");
                disabled.add(disabledId);
            }
        }

        return disabled;
    }
}
