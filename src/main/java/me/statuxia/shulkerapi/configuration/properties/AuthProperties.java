package me.statuxia.shulkerapi.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("auth")
public class AuthProperties {

    protected Long startedLifetimeSeconds = 60 * 5L; // 5 minutes
    protected Long notNotifiedLifetimeSeconds = 5L; // 5 seconds (need return state)
    protected Long acceptedLifetimeSeconds = 60L; // 1 minute
    protected Long rejectedLifetimeSeconds = 60 * 60 * 24 * 31L; // 31 days
    protected Long outdatedLifetimeSeconds = 5L; // 5 seconds (need return state)

    public Long getStartedLifetimeSeconds() {
        return startedLifetimeSeconds;
    }

    public AuthProperties setStartedLifetimeSeconds(Long startedLifetimeSeconds) {
        this.startedLifetimeSeconds = startedLifetimeSeconds;
        return this;
    }

    public Long getNotNotifiedLifetimeSeconds() {
        return notNotifiedLifetimeSeconds;
    }

    public AuthProperties setNotNotifiedLifetimeSeconds(Long notNotifiedLifetimeSeconds) {
        this.notNotifiedLifetimeSeconds = notNotifiedLifetimeSeconds;
        return this;
    }

    public Long getAcceptedLifetimeSeconds() {
        return acceptedLifetimeSeconds;
    }

    public AuthProperties setAcceptedLifetimeSeconds(Long acceptedLifetimeSeconds) {
        this.acceptedLifetimeSeconds = acceptedLifetimeSeconds;
        return this;
    }

    public Long getRejectedLifetimeSeconds() {
        return rejectedLifetimeSeconds;
    }

    public AuthProperties setRejectedLifetimeSeconds(Long rejectedLifetimeSeconds) {
        this.rejectedLifetimeSeconds = rejectedLifetimeSeconds;
        return this;
    }

    public Long getOutdatedLifetimeSeconds() {
        return outdatedLifetimeSeconds;
    }

    public AuthProperties setOutdatedLifetimeSeconds(Long outdatedLifetimeSeconds) {
        this.outdatedLifetimeSeconds = outdatedLifetimeSeconds;
        return this;
    }
}
