package no.rutebanken.tiamat.config;

import com.google.common.cache.CacheBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.guava.GuavaCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.Max;
import java.util.concurrent.TimeUnit;

@Configuration
public class GuavaCacheManagerConfig {

    private static final Logger logger = LoggerFactory.getLogger(GuavaCacheManagerConfig.class);

    private static final int MAX_CACHE_SIZE = 20000;

    @Bean(name = "guavaCacheManager")
    public CacheManager guavaCacheManager() {
        int expiresAfter = 30;
        TimeUnit timeUnit = TimeUnit.MINUTES;
        int maxCacheSize = MAX_CACHE_SIZE;

        logger.info("Setting up guava cache manager with max cache size {} and expiration time {} {}",
                maxCacheSize, expiresAfter, timeUnit);

        GuavaCacheManager guavaCacheManager =  new GuavaCacheManager();
        guavaCacheManager.setCacheBuilder(
                CacheBuilder.newBuilder()
                        .expireAfterAccess(expiresAfter, timeUnit)
                        .maximumSize(maxCacheSize)
        );
        return guavaCacheManager;
    }
}
