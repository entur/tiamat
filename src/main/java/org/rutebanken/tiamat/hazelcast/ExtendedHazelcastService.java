/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package org.rutebanken.tiamat.hazelcast;

import com.hazelcast.config.EvictionConfig;
import com.hazelcast.config.EvictionPolicy;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MaxSizePolicy;
import com.hazelcast.core.HazelcastInstance;
import org.rutebanken.hazelcasthelper.service.HazelCastService;
import org.rutebanken.hazelcasthelper.service.KubernetesService;
import org.rutebanken.tiamat.model.StopPlace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.rutebanken.tiamat.netex.id.GeneratedIdState.LAST_IDS_FOR_ENTITY;

public class ExtendedHazelcastService extends HazelCastService {

    private static final Logger logger = LoggerFactory.getLogger(ExtendedHazelcastService.class);

    private static final String MAP_CONFIG_NAME_SECOND_LEVEL_CACHE = StopPlace.class.getPackage().getName() + ".*";

    private static final int DEFAULT_BACKUP_COUNT = 1;

    /**
     * From Hazelcast documentation:
     * <p>
     * USED_HEAP_PERCENTAGE: Maximum used heap size percentage for each JVM. If, for example,
     * JVM is configured to have 1000 MB and this value is 10, then the map entries will be evicted when used heap size exceeds 100 MB.
     * <p>
     */
    private static final int MAX_HEAP_PERCENTAGE_SECOND_LEVEL_CACHE = 2;


    public ExtendedHazelcastService(KubernetesService kubernetesService) {
        super(kubernetesService);
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
                        .setEvictionConfig(new EvictionConfig().setEvictionPolicy(EvictionPolicy.NONE)));

        logger.info("Configured map for last ids for entities: {}", mapConfigs.get(0));

        mapConfigs.add(
                // Configure map for hibernate second level cache
                new MapConfig()
                        .setName(MAP_CONFIG_NAME_SECOND_LEVEL_CACHE)
                        // No sync backup for hibernate cache
                        .setBackupCount(0)
                        .setAsyncBackupCount(2)
                        .setEvictionConfig(new EvictionConfig().
                                setEvictionPolicy(EvictionPolicy.LFU).
                                setMaxSizePolicy(MaxSizePolicy.FREE_HEAP_PERCENTAGE)
                                .setSize(MAX_HEAP_PERCENTAGE_SECOND_LEVEL_CACHE))
                        .setTimeToLiveSeconds(604800));

        logger.info("Configured map for hibernate second level cache: {}", mapConfigs.get(1));
        return mapConfigs;
    }

    public HazelcastInstance getHazelcastInstance() {
        return hazelcast;
    }
}
