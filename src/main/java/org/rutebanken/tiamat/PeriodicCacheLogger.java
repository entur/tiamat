package org.rutebanken.tiamat;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheStats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Log cache stats periodically if stats has changed.
 * The cache must have stats enabled.
 */
@Component
public class PeriodicCacheLogger {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public void scheduleCacheStatsLogging(Cache cache, Logger logger) {
        Logger statsLogger = LoggerFactory.getLogger(logger.getName() + "-cache-stats");
        scheduler.scheduleAtFixedRate(new CacheStatsLogger(cache, statsLogger), 1, 2, TimeUnit.MINUTES);
    }

    public class CacheStatsLogger implements Runnable {

        private CacheStats lastStats = null;

        private Cache cache;
        private Logger statsLogger;

        public CacheStatsLogger(Cache cache, Logger statsLogger) {
            this.cache = cache;
            this.statsLogger = statsLogger;
        }

        @Override
        public void run() {
            CacheStats newStats = cache.stats();
            if (lastStats == null || !newStats.toString().equals(lastStats.toString())) {
                statsLogger.info("{}", newStats);
            }
            lastStats = newStats;
        }
    }

}
