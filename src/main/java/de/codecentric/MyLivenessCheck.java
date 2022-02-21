package de.codecentric;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.annotation.RegistryType;
import org.jboss.logging.Logger;

import javax.inject.Inject;

@Liveness
public class MyLivenessCheck implements HealthCheck {
    private static final Logger logger = Logger.getLogger(MyLivenessCheck.class);

    @Inject
    @RegistryType(type = MetricRegistry.Type.APPLICATION)
    MetricRegistry metricRegistry;

    private final WaitHelper helper = new WaitHelper("liveness", "alive");

    @Override
    public HealthCheckResponse call() {
        logger.info("liveness check started");
        this.metricRegistry.counter("app_liveness_probe").inc();
        return helper.call();
    }

}