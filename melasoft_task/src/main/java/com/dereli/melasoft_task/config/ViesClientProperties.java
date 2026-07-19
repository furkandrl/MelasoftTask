package com.dereli.melasoft_task.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "vies")
public class ViesClientProperties {

    private final Retry retry = new Retry();

    private long connectionTimeoutMs;
    private long receiveTimeoutMs;

    public Retry getRetry() {
        return retry;
    }

    public long getConnectionTimeoutMs() {
        return connectionTimeoutMs;
    }

    public void setConnectionTimeoutMs(long connectionTimeoutMs) {
        this.connectionTimeoutMs = connectionTimeoutMs;
    }

    public long getReceiveTimeoutMs() {
        return receiveTimeoutMs;
    }

    public void setReceiveTimeoutMs(long receiveTimeoutMs) {
        this.receiveTimeoutMs = receiveTimeoutMs;
    }

    public static class Retry {

        private int maxAttempts;
        private long delayMs;

        public int getMaxAttempts() {
            return maxAttempts;
        }

        public void setMaxAttempts(int maxAttempts) {
            this.maxAttempts = maxAttempts;
        }

        public long getDelayMs() {
            return delayMs;
        }

        public void setDelayMs(long delayMs) {
            this.delayMs = delayMs;
        }
    }
}
