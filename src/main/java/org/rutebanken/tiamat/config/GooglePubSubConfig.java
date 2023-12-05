package org.rutebanken.tiamat.config;

import org.springframework.beans.factory.annotation.Value;
//import org.springframework.cloud.gcp.pubsub.core.PubSubTemplate;
//import org.springframework.cloud.gcp.pubsub.integration.outbound.PubSubMessageHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.MessageHandler;

// TODO: Remove properly. Google pubsub is not used

//@Configuration
public class GooglePubSubConfig {
/*
    @Value("${changelog.topic.name:ror.tiamat.changelog}")
    private String pubSubTopic;

    @Bean
    @ServiceActivator(inputChannel = "pubsubOutputChannel")
    public MessageHandler messageSender(PubSubTemplate pubsubTemplate) {
        return new PubSubMessageHandler(pubsubTemplate, pubSubTopic);
    }

    @MessagingGateway(defaultRequestChannel = "pubsubOutputChannel")
    public interface PubsubOutboundGateway {
        void sendToPubsub(String text);
    }
 */
}
