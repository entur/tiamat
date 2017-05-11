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

    /**
     * From Hazelcast documentation:
     *
     * USED_HEAP_PERCENTAGE: Maximum used heap size percentage for each JVM. If, for example,
     * JVM is configured to have 1000 MB and this value is 10, then the map entries will be evicted when used heap size exceeds 100 MB.
     *
     * We have about 22 maps for the second level cache and max heap 5 GB, at the time of writing.
     * With this value set to 2, this means that each map will have their map entries evicted when the used heap size (of the map itselv) exeeds 100MB.
     * 100 MB per map is 2.2GB used heap in total.
     *
     */
    private static final int MAX_HEAP_PERCENTAGE_SECOND_LEVEL_CACHE = 2;

    public ExtendedHazelcastService(KubernetesService kubernetesService, String hazelcastManagementUrl) {
        super(kubernetesService, hazelcastManagementUrl);
    }

    /**
     * See <a href="http://docs.hazelcast.org/docs/3.5/manual/html/map-eviction.html">Map eviction</a>
     * @return
     */
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
                        // No sync backup for hibernate cache
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
