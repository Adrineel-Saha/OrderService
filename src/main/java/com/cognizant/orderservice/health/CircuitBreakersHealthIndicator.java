package com.cognizant.orderservice.health;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class CircuitBreakersHealthIndicator implements HealthIndicator {

    private final CircuitBreakerRegistry circuitBreakerRegistry;

    public CircuitBreakersHealthIndicator(CircuitBreakerRegistry circuitBreakerRegistry) {
        this.circuitBreakerRegistry = circuitBreakerRegistry;
    }

    @Override
    public Health health() {
        Health.Builder builder = Health.up();
        for (CircuitBreaker circuitBreaker : circuitBreakerRegistry.getAllCircuitBreakers()) {
            CircuitBreaker.State state = circuitBreaker.getState();
            CircuitBreaker.Metrics metrics = circuitBreaker.getMetrics();

            Map<String, Object> details = new LinkedHashMap<>();
            details.put("state", state);
            details.put("failureRate", metrics.getFailureRate() + "%");
            details.put("bufferedCalls", metrics.getNumberOfBufferedCalls());
            details.put("failedCalls", metrics.getNumberOfFailedCalls());
            details.put("notPermittedCalls", metrics.getNumberOfNotPermittedCalls());
            builder.withDetail(circuitBreaker.getName(), details);

            if (state == CircuitBreaker.State.OPEN || state == CircuitBreaker.State.FORCED_OPEN) {
                builder.down();
            }
        }
        return builder.build();
    }
}
