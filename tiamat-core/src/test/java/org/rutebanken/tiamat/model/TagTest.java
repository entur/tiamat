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

package org.rutebanken.tiamat.model;

import org.junit.Test;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.model.tag.Tag;
import org.rutebanken.tiamat.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
public class TagTest extends TiamatIntegrationTest {

    @Autowired
    private TagRepository tagRepository;

    @Test
    public void testSave() {

        StopPlace stopPlace = new StopPlace();
        stopPlace.setVersion(1L);
        stopPlaceRepository.save(stopPlace);

        StopPlace stopPlace2 = new StopPlace();
        stopPlace2.setVersion(2L);
        stopPlaceRepository.save(stopPlace2);

        Tag tag = new Tag();
        tag.setName("fix-coordinates");
        tag.setIdreference(stopPlace.getNetexId());
        tagRepository.save(tag);

        Tag tag2 = new Tag();
        tag2.setName("something-else");
        tag2.setIdreference(stopPlace.getNetexId());
        tagRepository.save(tag2);

        Set<Tag> actual = tagRepository.findByIdReference(stopPlace.getNetexId());

        assertThat(actual).isNotNull();
        assertThat(actual).hasSize(2);
    }
}