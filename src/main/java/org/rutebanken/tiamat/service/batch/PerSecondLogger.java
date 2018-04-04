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

    private final AtomicInteger count;

    private final String logMessage;

    private final long startTime;

    public PerSecondLogger(long startTime, AtomicInteger count, String logMessage) {
        this.count = count;
        this.logMessage = logMessage;
        this.startTime = startTime;
    }

    public void log() {

        long now = System.currentTimeMillis();

        if(moreThanXSecondsSinceLastLog(now, 1000)
                || increaseFromLastLogMoreThan(1000, count.get())) {


            long duration = now - startTime;
            String perSecond = String.valueOf(count.get() / (duration / 1000f));

            logger.info("{} count: {}, per second (since start): {}", logMessage, count.get(), perSecond);

            lastLogTime = now;
            lastCount = count.get();
        }
    }

    private boolean increaseFromLastLogMoreThan(int diffThreshold, int count) {
        return lastCount - count > diffThreshold;

    }

    private boolean moreThanXSecondsSinceLastLog(long now, int xSeconds) {
        return now - lastLogTime > xSeconds;
    }

}
