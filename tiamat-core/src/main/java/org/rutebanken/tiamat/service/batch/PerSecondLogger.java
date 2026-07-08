package org.rutebanken.tiamat.service.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Logs process.
 */
public class PerSecondLogger {

    private static final Logger logger = LoggerFactory.getLogger(PerSecondLogger.class);

    private long lastLogTime;

    private long lastCount;

    private final AtomicInteger iteratedCount;

    private final AtomicInteger updatecCount;

    private final String logMessage;

    private final long startTime;

    public PerSecondLogger(long startTime, AtomicInteger iteratedCount, AtomicInteger updatedCount, String logMessage) {
        this.iteratedCount = iteratedCount;
        this.updatecCount = updatedCount;
        this.logMessage = logMessage;
        this.startTime = startTime;
    }

    public void log() {

        long now = System.currentTimeMillis();

        if(moreThanXSecondsSinceLastLog(now, 1000)
                || increaseFromLastLogMoreThan(1000, iteratedCount.get())) {


            long duration = now - startTime;
            String perSecond = String.valueOf(iteratedCount.get() / (duration / 1000f));

            logger.info("{}. iterated: {}, updated: {}, iterated per second: {}", logMessage, iteratedCount.get(), updatecCount.get(), perSecond);

            lastLogTime = now;
            lastCount = iteratedCount.get();
        }
    }

    private boolean increaseFromLastLogMoreThan(int diffThreshold, int count) {
        return lastCount - count > diffThreshold;

    }

    private boolean moreThanXSecondsSinceLastLog(long now, int xSeconds) {
        return now - lastLogTime > xSeconds;
    }

}
