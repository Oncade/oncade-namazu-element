package com.oncade.pong.presale;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PresaleHttpClientTest {

    @AfterEach
    void tearDown() {
        PresaleHttpClient.resetEnvProvider();
    }

    @Test
    void usesDefaultBaseUrlWhenEnvMissing() {
        PresaleHttpClient.setEnvProvider(key -> null);
        assertEquals(PresaleHttpClient.DEFAULT_BASE_URL, PresaleHttpClient.resolveBaseUrl());
    }

    @Test
    void readsBaseUrlFromEnvironment() {
        PresaleHttpClient.setEnvProvider(key -> PresaleHttpClient.BASE_URL_ENV.equals(key) ? "https://example.test" : null);
        assertEquals("https://example.test", PresaleHttpClient.resolveBaseUrl());
    }

    @Test
    void usesDefaultTimeoutForMissingOrInvalidEnv() {
        PresaleHttpClient.setEnvProvider(key -> PresaleHttpClient.CONNECT_TIMEOUT_ENV.equals(key) ? "" : null);
        assertEquals(PresaleHttpClient.DEFAULT_CONNECT_TIMEOUT, PresaleHttpClient.resolveConnectTimeout());

        PresaleHttpClient.setEnvProvider(key -> PresaleHttpClient.CONNECT_TIMEOUT_ENV.equals(key) ? "-10" : null);
        assertEquals(PresaleHttpClient.DEFAULT_CONNECT_TIMEOUT, PresaleHttpClient.resolveConnectTimeout());

        PresaleHttpClient.setEnvProvider(key -> PresaleHttpClient.CONNECT_TIMEOUT_ENV.equals(key) ? "not-a-number" : null);
        assertEquals(PresaleHttpClient.DEFAULT_CONNECT_TIMEOUT, PresaleHttpClient.resolveConnectTimeout());
    }

    @Test
    void parsesConnectTimeoutFromEnvironment() {
        PresaleHttpClient.setEnvProvider(key -> PresaleHttpClient.CONNECT_TIMEOUT_ENV.equals(key) ? "1500" : null);
        assertEquals(Duration.ofMillis(1500), PresaleHttpClient.resolveConnectTimeout());
    }
}
