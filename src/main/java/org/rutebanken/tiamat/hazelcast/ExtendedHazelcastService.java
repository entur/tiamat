package org.rutebanken.tiamat.hazelcast;

import com.hazelcast.config.EvictionPolicy;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MaxSizeConfig;
import com.hazelcast.core.HazelcastInstance;
import org.rutebanken.hazelcasthelper.service.HazelCastService;
import org.rutebanken.hazelcasthelper.service.KubernetesService;
import org.rutebanken.tiamat.model.StopPlace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static org.rutebanken.tiamat.netex.id.GeneratedIdState.LAST_IDS_FOR_ENTITY;

public class ExtendedHazelcastService extends HazelCastService {

    private static final Logger logger = LoggerFactory.getLogger(ExtendedHazelcastService.class);

    private static final String MAP_CONFIG_NAME_SECOND_LEVEL_CACHE = StopPlace.class.getPackage().getName() + ".*";

    private static final int DEFAULT_BACKUP_COUNT = 2;

    private static final int MAX_HEAP_PERCENTAGE_SECOND_LEVEL_CACHE = 30;

    public ExtendedHazelcastService(KubernetesService kubernetesService, String hazelcastManagementUrl) {
        super(kubernetesService, hazelcastManagementUrl);
    }

    @Override
    public List<MapConfig> getAdditionalMapConfigurations() {
        List<MapConfig> mapConfigs = super.getAdditionalMapConfigurations();

        mapConfigs.add(
                // Configure map for last entity identificators
                new MapConfig()
                        .setName(LAST_IDS_FOR_ENTITY)
                        .setBackupCount(DEFAULT_BACKUP_COUNT)
                        .setAsyncBackupCount(0)
                        .setTimeToLiveSeconds(0)
                        .setEvictionPolicy(EvictionPolicy.NONE));

        logger.info("Configured map for last ids for entities: {}", mapConfigs.get(0));

        mapConfigs.add(
                // Configure map for hibernate second level cache
                new MapConfig()
                        .setName(MAP_CONFIG_NAME_SECOND_LEVEL_CACHE)
                        // No backup for hibernate cache
                        .setBackupCount(0)
                        .setAsyncBackupCount(2)
                        .setEvictionPolicy(EvictionPolicy.LFU)
                        .setTimeToLiveSeconds(604800)
                        .setMaxSizeConfig(
                                new MaxSizeConfig(MAX_HEAP_PERCENTAGE_SECOND_LEVEL_CACHE, MaxSizeConfig.MaxSizePolicy.USED_HEAP_PERCENTAGE)));

        logger.info("Configured map for hibernate second level cache: {}", mapConfigs.get(1));
        return mapConfigs;
    }

    public HazelcastInstance getHazelcastInstance() {
        return hazelcast;
    }
}
