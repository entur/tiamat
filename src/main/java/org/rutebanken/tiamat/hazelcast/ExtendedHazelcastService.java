package org.rutebanken.tiamat.hazelcast;

import com.hazelcast.config.Config;
import com.hazelcast.core.*;

import org.rutebanken.hazelcasthelper.service.HazelCastService;
import org.rutebanken.hazelcasthelper.service.KubernetesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;


import java.math.BigInteger;
import java.time.Instant;
import java.util.Collection;
import java.util.Set;

public class ExtendedHazelcastService extends HazelCastService {

    private static final Logger logger = LoggerFactory.getLogger(ExtendedHazelcastService.class);

    public ExtendedHazelcastService(KubernetesService kubernetesService, String hazelcastManagementUrl) {
        super(kubernetesService, hazelcastManagementUrl);
    }

    public HazelcastInstance getHazelcastInstance() {
        return hazelcast;
    }
}
