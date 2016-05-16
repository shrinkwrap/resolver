package org.jboss.shrinkwrap.resolver.api.maven.repository;

public enum MavenChecksumPolicy {
    /**
     * Verify checksums and fail the resolution if they do not match.
     */
    CHECKSUM_POLICY_FAIL("fail"),
    /**
     * Verify checksums and warn if they do not match.
     */
    CHECKSUM_POLICY_WARN("warn"),
    /**
     * Do not verify checksums.
     */
    CHECKSUM_POLICY_IGNORE("ignore");

    private final String apiValue;

    MavenChecksumPolicy(String apiValue) {
        this.apiValue = apiValue;
    }

    public String apiValue() {
        return apiValue;
    }
}
