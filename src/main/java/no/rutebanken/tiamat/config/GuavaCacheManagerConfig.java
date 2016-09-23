package no.rutebanken.tiamat.config;

import com.google.common.cache.CacheBuilder;
import org.springframework.cache.CacheManager;
import org.springframework.cache.guava.GuavaCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class GuavaCacheManagerConfig {

    private static final int MAX_CACHE_SIZE = 20000;

    @Bean
    public CacheManager cacheManager() {
        GuavaCacheManager guavaCacheManager =  new GuavaCacheManager();
        guavaCacheManager.setCacheBuilder(
                CacheBuilder.newBuilder()
                        .expireAfterAccess(30, TimeUnit.MINUTES)
                        .maximumSize(MAX_CACHE_SIZE)
        );
        return guavaCacheManager;
    }
}
