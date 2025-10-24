package me.statuxia.shulkerapi.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("session.limitation")
public class SessionLimitationProperties {
    public static final long SESSION_RATE_LIMIT = 120L;
    public static final long SESSION_RATE_RESET_SECONDS = 60L;

    private long sessionRateLimit = SESSION_RATE_LIMIT;
    private long sessionRateResetSeconds = SESSION_RATE_RESET_SECONDS;

    public long getSessionRateLimit() {
        return sessionRateLimit;
    }

    public void setSessionRateLimit(long sessionRateLimit) {
        this.sessionRateLimit = sessionRateLimit;
    }

    public long getSessionRateResetSeconds() {
        return sessionRateResetSeconds;
    }

    public void setSessionRateResetSeconds(long sessionRateResetSeconds) {
        this.sessionRateResetSeconds = sessionRateResetSeconds;
    }
}
