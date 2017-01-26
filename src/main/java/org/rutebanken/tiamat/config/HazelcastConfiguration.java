package org.rutebanken.tiamat.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HazelcastConfiguration {

    @Value("${rutebanken.kubernetes.url:}")
    private String kubernetesUrl;

    @Value("${rutebanken.kubernetes.enabled:true}")
    private boolean kuberentesEnabled;

    @Value("${rutebanken.kubernetes.namespace:default}")
    private String namespace;


    @Value("${rutebanken.hazelcast.management.url:}")
    private String hazelcastManagementUrl;

    public String getHazelcastManagementUrl() {
        return hazelcastManagementUrl;
    }

    public String getKubernetesUrl() {
        return kubernetesUrl;
    }

    public boolean isKuberentesEnabled() {
        return kuberentesEnabled;
    }

    public String getNamespace() {
        return namespace;
    }
}
