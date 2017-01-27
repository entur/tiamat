package org.rutebanken.tiamat.hazelcast;

import org.rutebanken.hazelcasthelper.service.KubernetesService;
import org.rutebanken.tiamat.config.HazelcastConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public final class ExtendedKubernetesService extends KubernetesService {

    @Autowired
    public ExtendedKubernetesService(HazelcastConfiguration hazelcastConfiguration) {
        super(hazelcastConfiguration.getKubernetesUrl(), hazelcastConfiguration.getNamespace(), hazelcastConfiguration.isKuberentesEnabled());
    }
}