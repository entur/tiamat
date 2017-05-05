package org.rutebanken.tiamat.hazelcast;

import com.hazelcast.config.EvictionPolicy;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MaxSizeConfig;
import com.hazelcast.core.HazelcastInstance;
import org.rutebanken.hazelcasthelper.service.HazelCastService;
import org.rutebanken.hazelcasthelper.service.KubernetesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class ExtendedHazelcastService extends HazelCastService {

    private static final Logger logger = LoggerFactory.getLogger(ExtendedHazelcastService.class);

    public ExtendedHazelcastService(KubernetesService kubernetesService, String hazelcastManagementUrl) {
        super(kubernetesService, hazelcastManagementUrl);
    }

    @Override
    public List<MapConfig> getAdditionalMapConfigurations() {

        MapConfig mapConfig = new MapConfig("tiamatEntityCacheRegion")
                .setEvictionPolicy(EvictionPolicy.LRU)
                .setTimeToLiveSeconds(604800)
                .setMaxSizeConfig(new MaxSizeConfig(100000, MaxSizeConfig.MaxSizePolicy.PER_NODE));

        return Arrays.asList(mapConfig);
    }


    public HazelcastInstance getHazelcastInstance() {
        return hazelcast;
    }
}
