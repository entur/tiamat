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

package org.rutebanken.tiamat.changelog;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import org.rutebanken.tiamat.model.EntityInVersionStructure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
@Profile("google-pubsub")
public class EntityChangedEventPubSubPublisher extends EntityChangedEventPublisher implements EntityChangedListener {

    @Autowired
    private PubSubTemplate pubSubTemplate;

    @Value("${changelog.topic.name:ror.tiamat.changelog}")
    private String pubSubTopic;

    @Value("${changelog.gcp.publish.enabled:true}")
    private boolean pubSubPublish;

    @Override
    public void onChange(EntityInVersionStructure entity) {
        if (pubSubPublish && isLoggedEntity(entity)) {
            pubSubTemplate.publish(pubSubTopic,toEntityChangedEvent(entity, false).toString());
        }

    }

    @Override
    public void onDelete(EntityInVersionStructure entity) {

        if (pubSubPublish && isLoggedEntity(entity)) {
            pubSubTemplate.publish(pubSubTopic,toEntityChangedEvent(entity, true).toString());
        }
    }
}
