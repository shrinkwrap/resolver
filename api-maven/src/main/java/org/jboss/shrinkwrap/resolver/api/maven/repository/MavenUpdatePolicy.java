package org.jboss.shrinkwrap.resolver.api.maven.repository;

public enum MavenUpdatePolicy {
    /**
     * Never update locally cached data.
     */
    UPDATE_POLICY_NEVER("never"),
    /**
     * Always update locally cached data.
     */
    UPDATE_POLICY_ALWAYS("always"),
    /**
     * Update locally cached data once a day.
     */
    UPDATE_POLICY_DAILY("daily"),
    /**
     * Update locally cached data every X minutes as given by "interval:X".
     */
    UPDATE_POLICY_INTERVAL("interval");

    private final String apiValue;

    MavenUpdatePolicy(String apiValue) {
        this.apiValue = apiValue;
    }

    public String apiValue() {
        return apiValue;
    }
}
