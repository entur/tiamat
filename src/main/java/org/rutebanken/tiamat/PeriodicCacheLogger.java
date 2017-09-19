/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

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
