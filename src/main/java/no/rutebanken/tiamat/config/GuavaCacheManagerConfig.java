package no.rutebanken.tiamat.config;

import com.google.common.cache.CacheBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.guava.GuavaCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class GuavaCacheManagerConfig {

    private static final Logger logger = LoggerFactory.getLogger(GuavaCacheManagerConfig.class);

    @Bean(name = "guavaCacheManager")
    public CacheManager guavaCacheManager(
            @Value("${guavaCacheManager.maxSize:20000}") int maxSize,
            @Value("${guavaCacheManager.expiresAfter:30}") int expiresAfter,
            @Value("${guavaCacheManager.expiresAfterTimeUnit:MINUTES}") TimeUnit expiresAfterTimeUnit) {


        logger.info("Setting up guava cache manager with max cache size {} and expiration time {} {}",
                maxSize, expiresAfter, expiresAfterTimeUnit);

        GuavaCacheManager guavaCacheManager =  new GuavaCacheManager();
        guavaCacheManager.setCacheBuilder(
                CacheBuilder.newBuilder()
                        .expireAfterAccess(expiresAfter, expiresAfterTimeUnit)
                        .maximumSize(maxSize)
        );
        return guavaCacheManager;
    }
}
