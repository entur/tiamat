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
        tag.setType("fix-coordinates");
        tag.setNetexReference(stopPlace.getNetexId());
        tagRepository.save(tag);

        Tag tag2 = new Tag();
        tag2.setType("something-else");
        tag2.setNetexReference(stopPlace.getNetexId());
        tagRepository.save(tag2);

        Set<Tag> actual = tagRepository.findByNetexReference(stopPlace.getNetexId());

        assertThat(actual).isNotNull();
        assertThat(actual).hasSize(2);
    }
}