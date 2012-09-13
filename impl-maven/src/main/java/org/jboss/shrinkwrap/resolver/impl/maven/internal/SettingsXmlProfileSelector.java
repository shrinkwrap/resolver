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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.maven.model.Profile;
import org.apache.maven.model.building.ModelProblemCollector;
import org.apache.maven.model.path.DefaultPathTranslator;
import org.apache.maven.model.profile.ProfileActivationContext;
import org.apache.maven.model.profile.ProfileSelector;
import org.apache.maven.model.profile.activation.FileProfileActivator;
import org.apache.maven.model.profile.activation.JdkVersionProfileActivator;
import org.apache.maven.model.profile.activation.OperatingSystemProfileActivator;
import org.apache.maven.model.profile.activation.ProfileActivator;
import org.apache.maven.model.profile.activation.PropertyProfileActivator;
import org.jboss.shrinkwrap.resolver.impl.maven.util.Validate;

/**
 * {@link ProfileSelector} implementation backed by metadata defined by <code>settings.xml</code>
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 */
public class SettingsXmlProfileSelector implements ProfileSelector {

    private final List<ProfileActivator> activators;

    public SettingsXmlProfileSelector() {
        this.activators = new ArrayList<ProfileActivator>();
        activators.addAll(Arrays.asList(new JdkVersionProfileActivator(), new PropertyProfileActivator(),
            new OperatingSystemProfileActivator(),
            new FileProfileActivator().setPathTranslator(new DefaultPathTranslator())));
    }

    @Override
    public List<Profile> getActiveProfiles(Collection<Profile> profiles, ProfileActivationContext context,
        ModelProblemCollector problems) {

        List<Profile> activeProfiles = new ArrayList<Profile>();

        for (Profile p : profiles) {
            String id = p.getId();
            if (p.getId() != null && context.getActiveProfileIds().contains(id)
                && !context.getInactiveProfileIds().contains(id)) {
                activeProfiles.add(p);
            }
            if (p.getActivation() != null && p.getActivation().isActiveByDefault()
                && !context.getInactiveProfileIds().contains(p.getId())) {
                activeProfiles.add(p);
                break;
            }
            for (ProfileActivator activator : activators) {
                if (activator.isActive(p, context, problems)) {
                    activeProfiles.add(p);
                    break;
                }
            }
        }

        return activeProfiles;
    }

    // selects all profile ids to be activated
    public static List<String> explicitlyActivatedProfiles(String... profiles) {
        if (profiles.length == 0) {
            return Collections.<String> emptyList();
        }
        List<String> activated = new ArrayList<String>();
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
        List<String> disabled = new ArrayList<String>();
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
