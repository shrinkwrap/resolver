package org.jboss.shrinkwrap.resolver.impl.maven;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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

class SettingsXmlProfileSelector implements ProfileSelector {

    private List<ProfileActivator> activators;

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
}
