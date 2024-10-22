package org.rutebanken.tiamat.config;

import org.springframework.context.annotation.Profile;
import org.springframework.integration.annotation.MessagingGateway;

@Profile("google-pubsub")
@MessagingGateway(defaultRequestChannel = "pubsubOutputChannel")
public interface PubsubOutboundGateway {
    void sendToPubsub(String text);
}
