package de.codecentric;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.annotation.RegistryType;
import org.jboss.logging.Logger;

import javax.inject.Inject;

@Readiness
public class MyReadinessCheck implements HealthCheck {
    private static final Logger logger = Logger.getLogger(MyReadinessCheck.class);

    @Inject
    @RegistryType(type = MetricRegistry.Type.APPLICATION)
    MetricRegistry metricRegistry;

    private final WaitHelper helper = new WaitHelper("readiness", "ready");

    @Override
    public HealthCheckResponse call() {
        logger.info("readiness check started");
        this.metricRegistry.counter("app_readiness_probe").inc();
        return helper.call();
    }
}
