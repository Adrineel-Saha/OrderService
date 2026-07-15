package com.cognizant.orderservice.test.health;

import com.cognizant.orderservice.health.CircuitBreakersHealthIndicator;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.Status;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TestCircuitBreakersHealthIndicator {

    @Mock
    private CircuitBreakerRegistry circuitBreakerRegistry;
    @InjectMocks
    private CircuitBreakersHealthIndicator healthIndicator;

    private AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }

    private CircuitBreaker mockBreaker(String name, CircuitBreaker.State state) {
        CircuitBreaker cb = mock(CircuitBreaker.class);
        CircuitBreaker.Metrics metrics = mock(CircuitBreaker.Metrics.class);
        when(cb.getName()).thenReturn(name);
        when(cb.getState()).thenReturn(state);
        when(cb.getMetrics()).thenReturn(metrics);
        when(metrics.getFailureRate()).thenReturn(50.0f);
        when(metrics.getNumberOfBufferedCalls()).thenReturn(10);
        when(metrics.getNumberOfFailedCalls()).thenReturn(5);
        when(metrics.getNumberOfNotPermittedCalls()).thenReturn(2L);
        return cb;
    }

    @Test
    void testHealthWhenBreakerClosedReturnsUp() {
        CircuitBreaker cb = mockBreaker("OrderMicroservice", CircuitBreaker.State.CLOSED);
        when(circuitBreakerRegistry.getAllCircuitBreakers()).thenReturn(Set.of(cb));

        Health health = healthIndicator.health();

        assertEquals(Status.UP, health.getStatus());
        assertThat(health.getDetails()).containsKey("OrderMicroservice");
    }

    @Test
    void testHealthWhenBreakerHalfOpenReturnsUp() {
        CircuitBreaker cb = mockBreaker("OrderMicroservice", CircuitBreaker.State.HALF_OPEN);
        when(circuitBreakerRegistry.getAllCircuitBreakers()).thenReturn(Set.of(cb));

        assertEquals(Status.UP, healthIndicator.health().getStatus());
    }

    @Test
    void testHealthWhenBreakerOpenReturnsDown() {
        CircuitBreaker cb = mockBreaker("OrderMicroservice", CircuitBreaker.State.OPEN);
        when(circuitBreakerRegistry.getAllCircuitBreakers()).thenReturn(Set.of(cb));

        assertEquals(Status.DOWN, healthIndicator.health().getStatus());
    }

    @Test
    void testHealthWhenBreakerForcedOpenReturnsDown() {
        CircuitBreaker cb = mockBreaker("OrderMicroservice", CircuitBreaker.State.FORCED_OPEN);
        when(circuitBreakerRegistry.getAllCircuitBreakers()).thenReturn(Set.of(cb));

        assertEquals(Status.DOWN, healthIndicator.health().getStatus());
    }

    @Test
    void testHealthWhenNoBreakersReturnsUp() {
        when(circuitBreakerRegistry.getAllCircuitBreakers()).thenReturn(Set.<CircuitBreaker>of());

        Health health = healthIndicator.health();

        assertEquals(Status.UP, health.getStatus());
        assertThat(health.getDetails()).isEmpty();
    }

    @Test
    @SuppressWarnings("unchecked")
    void testHealthDetailsContainMetrics() {
        CircuitBreaker cb = mockBreaker("OrderMicroservice", CircuitBreaker.State.CLOSED);
        when(circuitBreakerRegistry.getAllCircuitBreakers()).thenReturn(Set.of(cb));

        Health health = healthIndicator.health();

        Map<String, Object> details = (Map<String, Object>) health.getDetails().get("OrderMicroservice");
        assertThat(details).containsKeys("state", "failureRate", "bufferedCalls", "failedCalls", "notPermittedCalls");
        assertEquals(CircuitBreaker.State.CLOSED, details.get("state"));
        assertEquals("50.0%", details.get("failureRate"));
    }
}
