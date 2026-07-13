package org.rutebanken.tiamat.service.metrics;

import io.micrometer.core.instrument.ImmutableTag;
import io.micrometer.core.instrument.Tag;
import io.micrometer.prometheusmetrics.PrometheusConfig;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;
import jakarta.annotation.PreDestroy;
import org.rutebanken.tiamat.model.identification.IdentifiedEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PrometheusMetricsService extends PrometheusMeterRegistry {
    private static final Logger logger= LoggerFactory.getLogger(PrometheusMeterRegistry.class);

    private static final String METRICS_PREFIX = "app.tiamat.";
    private static final String CLIENT_COUNTER= METRICS_PREFIX + "client";
    private static final String USER_COUNTER= METRICS_PREFIX + "user";
    private static final String ENTITY_COUNTER= METRICS_PREFIX + "entity";

    public PrometheusMetricsService() {
        super(PrometheusConfig.DEFAULT);
    }

    @PreDestroy
    public void shutdown() {
        this.close();
    }

    public void registerRequestFromClient(String clientName, String clientId,long total) {
        clientName = clientName == null? "unknown":clientName;
        clientId = clientId == null? "unknown":clientId;
        logger.info("Registering data {},{}",clientName,clientId);
        List<Tag> counterTags = new ArrayList<>();
        counterTags.add(new ImmutableTag("clientName", clientName));
        counterTags.add(new ImmutableTag("clientId", clientId));
        counter(CLIENT_COUNTER,counterTags).increment(total);
    }

    public void registerRequestFromUser(String userName,long total) {
        userName = userName == null? "unknown":userName;
        List<Tag> counterTags = new ArrayList<>();
        counterTags.add(new ImmutableTag("userName", userName));
        counter(USER_COUNTER,counterTags).increment(total);
    }

    public void registerEntitySaved(Class<? extends IdentifiedEntity> entityClass,long total) {
        List<Tag> counterTags = new ArrayList<>();
        counterTags.add(new ImmutableTag("IdentifiedEntity", entityClass.getSimpleName()));
        counter(ENTITY_COUNTER,counterTags).increment(total);
    }
}
