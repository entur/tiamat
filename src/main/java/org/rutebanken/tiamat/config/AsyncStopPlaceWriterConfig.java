package org.rutebanken.tiamat.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.concurrent.DelegatingSecurityContextExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@ConditionalOnProperty(name = "tiamat.write-api.in-memory-processor.enabled", havingValue = "true")
public class AsyncStopPlaceWriterConfig {

    @Bean(name = "stopPlaceWriteExecutor")
    public Executor stopPlaceExecutor(
            @Value("${tiamat.write-api.queue-capacity:0}") int queueCapacity
    ) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(1);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("StopPlaceWriter-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        executor.initialize();
        return new DelegatingSecurityContextExecutor(executor);
    }
}
