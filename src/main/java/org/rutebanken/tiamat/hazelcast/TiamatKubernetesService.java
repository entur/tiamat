package org.rutebanken.tiamat.hazelcast;

import org.rutebanken.hazelcasthelper.service.KubernetesService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TiamatKubernetesService extends KubernetesService {

    public TiamatKubernetesService(@Value("${rutebanken.kubernetes.url:}") String kubernetesUrl,
                                        @Value("${rutebanken.kubernetes.namespace:default}") String namespace,
                                        @Value("${rutebanken.kubernetes.enabled:true}") boolean kubernetesEnabled) {
        super(kubernetesUrl, namespace, kubernetesEnabled);
    }

}
