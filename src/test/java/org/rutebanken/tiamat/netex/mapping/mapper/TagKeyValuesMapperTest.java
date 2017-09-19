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
import org.junit.Test;
import org.rutebanken.netex.model.KeyListStructure;
import org.rutebanken.netex.model.KeyValueStructure;
import org.rutebanken.netex.model.StopPlace;
import org.rutebanken.tiamat.model.tag.Tag;
import org.rutebanken.tiamat.repository.TagRepository;

import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class TagKeyValuesMapperTest {

    private TagRepository tagRepository = mock(TagRepository.class);
    private TagKeyValuesMapper tagKeyValuesMapper = new TagKeyValuesMapper(tagRepository);

    @Test
    public void mapTagsToProperties() throws Exception {

        Tag tag = new Tag();
        tag.setCreated(Instant.now());
        tag.setName("name");
        tag.setCreatedBy("also me");
        tag.setIdreference("NSR:StopPlace:1");
        tag.setRemovedBy("me");
        tag.setRemoved(Instant.now());
        tag.setComment("comment");

        Set<Tag> tags = Sets.newHashSet(tag);
        when(tagRepository.findByIdReference("NSR:StopPlace:1")).thenReturn(tags);

        org.rutebanken.tiamat.model.StopPlace tiamatStopPlace = new org.rutebanken.tiamat.model.StopPlace();
        tiamatStopPlace.setNetexId("NSR:StopPlace:1");

        StopPlace stopPlace = new StopPlace();
        stopPlace.withKeyList(new KeyListStructure());
        tagKeyValuesMapper.mapTagsToProperties(tiamatStopPlace, stopPlace);



        Map<String, String> flattened = stopPlace.getKeyList().getKeyValue().stream().collect(Collectors.toMap(KeyValueStructure::getKey, KeyValueStructure::getValue));

        assertThat(flattened).containsKeys(
                "TAG-0-name",
                "TAG-0-createdBy",
                "TAG-0-created",
                "TAG-0-removed",
                "TAG-0-removedBy",
                "TAG-0-comment",
                "TAG-0-idReference");

        assertThat(flattened.get("TAG-0-idReference")).isEqualTo(tag.getIdReference());
        assertThat(flattened.get("TAG-0-name")).isEqualTo(tag.getName());
    }


    @Test
    public void mapPropertiesToTag() throws Exception {

        KeyListStructure keyListStructure = new KeyListStructure();
        keyListStructure.getKeyValue().add(new KeyValueStructure().withKey("TAG-0-name").withValue("name"));
        keyListStructure.getKeyValue().add(new KeyValueStructure().withKey("TAG-0-created").withValue(String.valueOf(Instant.now().toEpochMilli())));
        keyListStructure.getKeyValue().add(new KeyValueStructure().withKey("TAG-0-idReference").withValue("NSR:StopPlace:1"));
        keyListStructure.getKeyValue().add(new KeyValueStructure().withKey("TAG-1-name").withValue("name 2"));
        keyListStructure.getKeyValue().add(new KeyValueStructure().withKey("TAG-1-idReference").withValue("NSR:StopPlace:2"));

        Set<Tag> tags = tagKeyValuesMapper.mapPropertiesToTag(keyListStructure);

        assertThat(tags).hasSize(2);

    }

    @Test
    public void mapForthAndBack() throws Exception {
        String netexReference = "NSR:StopPlace:2";
        Tag tag = new Tag();
        tag.setCreated(Instant.now());
        tag.setName("name");
        tag.setCreatedBy("also me");
        tag.setIdreference(netexReference);
        tag.setRemovedBy("me");
        tag.setRemoved(Instant.now());
        tag.setComment("comment");

        Set<Tag> tags = Sets.newHashSet(tag);
        when(tagRepository.findByIdReference(netexReference)).thenReturn(tags);

        org.rutebanken.tiamat.model.StopPlace tiamatStopPlace = new org.rutebanken.tiamat.model.StopPlace();
        tiamatStopPlace.setNetexId(netexReference);

        StopPlace stopPlace = new StopPlace();
        stopPlace.withKeyList(new KeyListStructure());
        tagKeyValuesMapper.mapTagsToProperties(tiamatStopPlace, stopPlace);


        Set<Tag> actual = tagKeyValuesMapper.mapPropertiesToTag(stopPlace.getKeyList());

        assertThat(actual.iterator().next()).isEqualTo(tags.iterator().next());
    }

}