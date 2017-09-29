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

import com.hazelcast.core.HazelcastInstance;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HazelcastConfiguration {

    /**
     * Hazelcast is set up before spring context beacuse of using second level cache with hazelcast.
     * To be able to use hazelcast instance in other parts of the application, expose it as spring bean here.
     */
    @Bean
    public HazelcastInstance getHazelcastInstanceFromTiamatHazelcastCacheRegionFactory() {
        return TiamatHazelcastCacheRegionFactory.getHazelCastInstance();
    }

}
