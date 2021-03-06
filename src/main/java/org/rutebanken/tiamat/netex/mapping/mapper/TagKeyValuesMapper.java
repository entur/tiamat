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

package org.rutebanken.tiamat.netex.mapping.mapper;

import com.google.common.collect.Sets;
import com.google.common.primitives.Longs;
import org.rutebanken.netex.model.DataManagedObjectStructure;
import org.rutebanken.netex.model.KeyListStructure;
import org.rutebanken.netex.model.KeyValueStructure;
import org.rutebanken.tiamat.model.tag.Tag;
import org.rutebanken.tiamat.repository.TagRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.rutebanken.tiamat.service.TagCreator.SUPPORTED_TAGGABLE_TYPES;

@Component
public class TagKeyValuesMapper {

    public static final String TAG_PREFIX = "TAG";

    public static final String KEY_SEPARATOR = "-";

    private static final Logger logger = LoggerFactory.getLogger(TagKeyValuesMapper.class);
    public static final String ID_REFERENCE = "idReference";
    public static final String NAME = "name";
    public static final String CREATED_BY = "createdBy";
    public static final String CREATED = "created";
    public static final String REMOVED = "removed";
    public static final String REMOVED_BY = "removedBy";
    public static final String COMMENT = "comment";

    private final TagRepository tagRepository;

    @Autowired
    public TagKeyValuesMapper(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }


    public void mapTagsToProperties(org.rutebanken.tiamat.model.DataManagedObjectStructure tiamatEntity, DataManagedObjectStructure netexEntity) {

        if(tiamatEntity.getNetexId() == null) {
            logger.trace("ID reference is null. Cannot continue mapping tags");
            return;
        }

        if(!SUPPORTED_TAGGABLE_TYPES.contains(tiamatEntity.getClass())) {
            logger.trace("Ignoring tag mapping for class {} as it's not supported", tiamatEntity.getClass());
            return;
        }

        Set<Tag> tags = tagRepository.findByIdReference(tiamatEntity.getNetexId());

        if (tags == null || tags.isEmpty()) {
            return;
        }

        int index = 0;

        for (Tag tag : tags) {
            addTagKeysToNetexKeyValue(netexEntity, TAG_PREFIX + KEY_SEPARATOR + index, tag);
            index++;
        }
    }

    /**
     * Could have used reflection...
     *
     */
    public void addTagKeysToNetexKeyValue(DataManagedObjectStructure netexEntity, String prefix, Tag tag) {
        setKey(netexEntity, prefix, ID_REFERENCE, tag.getIdReference());
        setKey(netexEntity, prefix, NAME, tag.getName());
        setKey(netexEntity, prefix, CREATED_BY, tag.getCreatedBy());
        setKey(netexEntity, prefix, COMMENT, tag.getComment());
        if (tag.getCreated() != null) {
            setKey(netexEntity, prefix, CREATED, String.valueOf(tag.getCreated().toEpochMilli()));
        }
        setKey(netexEntity, prefix, REMOVED_BY, tag.getRemovedBy());
        if (tag.getRemoved() != null) {
            setKey(netexEntity, prefix, REMOVED, String.valueOf(tag.getRemoved().toEpochMilli()));
        }
    }

    private void setKey(DataManagedObjectStructure netexEntity, String prefix, String name, String value) {
        if (value == null) return;

        String key = prefix + KEY_SEPARATOR + name;
        logger.debug("Adding key: {}", key);
        netexEntity.getKeyList()
                .withKeyValue(new KeyValueStructure()
                        .withKey(key)
                        .withValue(value));
    }

    public Set<Tag> mapPropertiesToTag(KeyListStructure keyListStructure) {

        Map<String, Tag> tagsByNumber = new HashMap<>();

        // This could have been done with reflection for reusability.

        for(KeyValueStructure keyValueStructure : keyListStructure.getKeyValue()) {

            if(keyValueStructure.getKey().startsWith(TAG_PREFIX)) {
                String value = keyValueStructure.getValue();
                String[] keyParts = keyValueStructure.getKey().split(KEY_SEPARATOR);
                if(keyParts.length < 3) {
                    logger.warn("Expected more than three key parts separated by {} for key {}", KEY_SEPARATOR, keyValueStructure.getKey());
                    continue;
                }
                String number = keyParts[1];

                tagsByNumber.putIfAbsent(number, new Tag());
                Tag tag = tagsByNumber.get(number);

                switch (keyParts[2]) {
                    case ID_REFERENCE:
                        tag.setIdreference(value);
                        break;
                    case NAME:
                        tag.setName(value);
                        break;
                    case CREATED_BY:
                        tag.setCreatedBy(value);
                        break;
                    case CREATED:
                        tag.setCreated(Instant.ofEpochMilli(Longs.tryParse(value)));
                        break;
                    case REMOVED:
                        tag.setRemoved(Instant.ofEpochMilli(Longs.tryParse(value)));
                        break;
                    case REMOVED_BY:
                        tag.setRemovedBy(value);
                        break;
                    case COMMENT:
                        tag.setComment(value);
                        break;
                }
            }
        }
        return Sets.newHashSet(tagsByNumber.values());
    }
}
