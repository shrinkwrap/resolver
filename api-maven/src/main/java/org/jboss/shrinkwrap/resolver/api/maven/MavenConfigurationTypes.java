package org.jboss.shrinkwrap.resolver.api.maven;

/**
 * A set for configuration types which are available by default in ShrinkWrap Maven Dependency Resolver
 *
 * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
 *
 */
public class MavenConfigurationTypes {

    /**
     * A default implementation of {@link MavenConfigurationType}.
     *
     * Allows user to specify all the required information by a fluent API.
     */
    public static final MavenConfigurationType<MavenDependencyResolver> MANUAL = new MavenConfigurationType<MavenDependencyResolver>() {
        @Override
        public MavenDependencyResolver configure(MavenDependencyResolver resolver) {
            return resolver;
        }
    };

    /**
     * A more advanced implementation of {@link MavenConfigurationType}.
     *
     * It retrieves configuration from currently running Maven process. It requires Resolver Maven Plugin distributed with
     * ShrinkWrap to be activated in proper phase.
     *
     * Use this implementation if you enabled the plugin and you want to reuse information about your project from currently
     * running Maven execution.
     */
    public static final MavenConfigurationType<EffectivePomMavenDependencyResolver> ENVIRONMENT = new MavenConfigurationType<EffectivePomMavenDependencyResolver>() {

        public static final String POM_FILE_KEY = "maven.execution.pom-file";
        public static final String OFFLINE_KEY = "maven.execution.offline";
        public static final String USER_SETTINGS_KEY = "maven.execution.user-settings";
        public static final String GLOBAL_SETTINGS_KEY = "maven.execution.global-settings";
        public static final String ACTIVE_PROFILES_KEY = "maven.execution.active-profiles";

        private static final String CONSTRUCTION_EXCEPTION = "Configuration from environment requires that user has following properties set, however they were not detected in runtime environment:\n"
                + "\t"
                + POM_FILE_KEY
                + "\n"
                + "\t"
                + OFFLINE_KEY
                + "\n"
                + "\t"
                + USER_SETTINGS_KEY
                + "\n"
                + "\t"
                + GLOBAL_SETTINGS_KEY
                + "\n"
                + "\t"
                + ACTIVE_PROFILES_KEY
                + "\n"
                + "\n"
                + "You should enable ShrinkWrap Maven Resolver to get them set for you automatically if executing from Maven via adding following to your <build> section:\n\n"
                + "<plugin>\n"
                + "\t<groupId>org.jboss.shrinkwrap.resolver</groupId>\n"
                + "\t<artifactId>resolver-maven-plugin</artifactId>\n"
                + "\t<executions>\n"
                + "\t\t<execution>\n"
                + "\t\t\t<goals>\n"
                + "\t\t\t\t<goal>propagate-execution-context</goal>\n"
                + "\t\t\t</goals>\n"
                + "\t\t</execution>\n" + "\t</executions>\n" + "</plugin>\n";

        @Override
        public EffectivePomMavenDependencyResolver configure(MavenDependencyResolver resolver) {

            String pomFile = SecurityActions.getProperty(POM_FILE_KEY);
            Validate.stateNotNullOrEmpty(pomFile, CONSTRUCTION_EXCEPTION);
            Validate.isReadable(pomFile, "POM file " + pomFile + " does not represent a readable file");

            String userSettings = SecurityActions.getProperty(USER_SETTINGS_KEY);
            Validate.stateNotNullOrEmpty(userSettings, CONSTRUCTION_EXCEPTION);

            boolean hasSettingsXml = true;
            try {
                Validate.isReadable(userSettings, "Settings.xml file " + userSettings
                    + " does not represent a readable file");
            } catch (final IllegalArgumentException iae) {
                hasSettingsXml = false;
            }

            MavenDependencyResolver updatedResolver;
            if (hasSettingsXml) {
                updatedResolver = resolver.loadSettings(userSettings);
            } else {
                updatedResolver = resolver;
            }

            // FIXME use global settings as well
            boolean offline = "true".equals(SecurityActions.getProperty(OFFLINE_KEY));
            if (offline) {
                updatedResolver = updatedResolver.goOffline();
            }

            // FIXME implement active profiles
            return updatedResolver.loadEffectivePom(pomFile);
        }

    };
}
