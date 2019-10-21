package hello;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class HealthChecker implements HealthIndicator {
    @Override
    public Health health() {
        if (!HealthExampleController.check()) {
            return Health.down().withDetail("Error Code", 501).build();
        } else {
            return Health.up().build();
        }
    }
}