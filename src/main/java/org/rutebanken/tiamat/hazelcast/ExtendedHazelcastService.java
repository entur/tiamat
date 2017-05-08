package org.rutebanken.tiamat.hazelcast;

import com.hazelcast.config.Config;
import com.hazelcast.config.EvictionPolicy;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MaxSizeConfig;
import com.hazelcast.core.HazelcastInstance;
import org.rutebanken.hazelcasthelper.service.HazelCastService;
import org.rutebanken.hazelcasthelper.service.KubernetesService;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ExtendedHazelcastService extends HazelCastService {

    private static final Logger logger = LoggerFactory.getLogger(ExtendedHazelcastService.class);

    public ExtendedHazelcastService(KubernetesService kubernetesService, String hazelcastManagementUrl) {
        super(kubernetesService, hazelcastManagementUrl);
    }

    @Override
    public void updateDefaultMapConfig(MapConfig mapConfig) {

        mapConfig
                .setEvictionPolicy(EvictionPolicy.LRU)
                .setTimeToLiveSeconds(604800)
                .setMaxSizeConfig(
                        new MaxSizeConfig(70, MaxSizeConfig.MaxSizePolicy.USED_HEAP_PERCENTAGE));

        logger.info("Map config: {}", mapConfig);

    }


    public HazelcastInstance getHazelcastInstance() {
        return hazelcast;
    }
}
