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

import com.hazelcast.config.*;
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
     * Evict cache when free heap percentage is below this value
     */
    private static final int EVICT_WHEN_FREE_HEAP_PERCENTAGE_BELOW = 50;

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
                                new MaxSizeConfig(EVICT_WHEN_FREE_HEAP_PERCENTAGE_BELOW, MaxSizeConfig.MaxSizePolicy.FREE_HEAP_PERCENTAGE)));

        logger.info("Configured map for hibernate second level cache: {}", mapConfigs.get(1));
        return mapConfigs;
    }

    public HazelcastInstance getHazelcastInstance() {
        return hazelcast;
    }
}
