package org.rutebanken.tiamat.rest.graphql;

import org.junit.Test;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
public class MultiModalStopPlaceEditorTest extends TiamatIntegrationTest {

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Autowired
    private MultiModalStopPlaceEditor multiModalStopPlaceEditor;

    @Test
    public void testCreateMultiModalParentStopPlace() {

        List<StopPlace> children = new ArrayList<>();
        children.add(createStopPlace("StopPlace - 1"));
        children.add(createStopPlace("StopPlace - 2"));
        children.add(createStopPlace("StopPlace - 3"));
        children.add(createStopPlace("StopPlace - 4"));

        List<StopPlace> savedChildren = stopPlaceRepository.save(children);

        List<String> childIds = new ArrayList<>();
        savedChildren.forEach(sp -> {
            childIds.add(sp.getNetexId());
        });

        String parentStopPlaceName = "Super StopPlace";
        StopPlace result = multiModalStopPlaceEditor.createMultiModalParentStopPlace(childIds, new EmbeddableMultilingualString(parentStopPlaceName));

        childIds.forEach(id -> {
            StopPlace child = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(id);
            assertThat(child.getParentSiteRef()).isNotNull();
            assertThat(child.getParentSiteRef().getRef()).isNotNull();
            assertThat(child.getParentSiteRef().getRef()).isEqualTo(result.getNetexId());
        });

    }

    private StopPlace createStopPlace(String name) {
        return new StopPlace(new EmbeddableMultilingualString(name));
    }
}