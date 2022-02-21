package de.codecentric;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.jboss.logging.Logger;
import java.time.Instant;

public class WaitHelper implements HealthCheck {
    private static final Logger logger = Logger.getLogger(WaitHelper.class);

    private final int waitTime;

    private final Instant successAfter;
    private final String response;

    public WaitHelper(String prefix, String response) {
        this.response = response;
        prefix = prefix.toUpperCase();

        String wait = System.getenv(prefix + "_WAIT_SECONDS");
        String delay = System.getenv(prefix + "DELAY_SECONDS");

        if (wait != null && !wait.isBlank()) {
            waitTime = Integer.parseInt(wait);
        } else {
            waitTime = 0;
        }

        int initialDelay;
        if (delay != null && !delay.isBlank()) {
            initialDelay = Integer.parseInt(delay);
        } else {
            initialDelay = 0;
        }

        successAfter = Instant.now().plusSeconds(initialDelay);
    }


    @Override
    public HealthCheckResponse call() {
        if (Instant.now().isBefore(successAfter)) {
            return HealthCheckResponse.down(response);
        }

        try {
            Thread.sleep(waitTime * 1000L);
        } catch (InterruptedException ex) {
            logger.error(ex);
        }

        return HealthCheckResponse.up(response);
    }
}
