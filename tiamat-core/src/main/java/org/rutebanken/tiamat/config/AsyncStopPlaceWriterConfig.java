package org.rutebanken.tiamat.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@ConditionalOnProperty(name = "tiamat.write-api.transport", havingValue = "in-memory")
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
        // Not wrapped in a DelegatingSecurityContextExecutor: the principal is carried on the
        // job and reinstated by the handler, which works for a worker in another pod as well as
        // one on another thread. Wrapping here would only mask a transport that does not.
        return executor;
    }
}
