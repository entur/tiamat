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

package org.rutebanken.tiamat.service.stopplace;

import com.vividsolutions.jts.geom.Coordinate;
import org.junit.Test;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.service.stopplace.StopPlaceQuayDeleter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class StopPlaceQuayDeleterTest extends TiamatIntegrationTest {

    @Autowired
    StopPlaceQuayDeleter stopPlaceQuayDeleter;

    @Autowired
    private MultiModalStopPlaceEditor multiModalStopPlaceEditor;

    @Autowired
    private ChildFromParentResolver childFromParentResolver;

    @Transactional
    @Test
    public void testDeleteQuay() {

        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new EmbeddableMultilingualString("Name"));
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(11.1, 60.1)));
        stopPlace.getOriginalIds().add("TEST:StopPlace:1234");
        stopPlace.getOriginalIds().add("TEST:StopPlace:5678");

        Quay quay = new Quay();
        quay.setName(new EmbeddableMultilingualString("testQuay"));
        stopPlace.getQuays().add(quay);

        Quay quay2 = new Quay();
        quay2.setName(new EmbeddableMultilingualString("testQuay2"));
        stopPlace.getQuays().add(quay2);

        StopPlace savedStopPlace = stopPlaceVersionedSaverService.saveNewVersion(stopPlace);

        String stopPlaceNetexId = savedStopPlace.getNetexId();
        String quayNetexId  = stopPlace.getQuays().iterator().next().getNetexId();

        StopPlace fetchedStopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(stopPlaceNetexId);

        assertThat(fetchedStopPlace).isNotNull();
        assertThat(fetchedStopPlace.getQuays()).isNotNull();
        assertThat(fetchedStopPlace.getQuays()).hasSize(2);

        Quay fetchedQuay = quayRepository.findFirstByNetexIdOrderByVersionDesc(quayNetexId);
        assertThat(fetchedQuay).isNotNull();

        String versionComment = "Deleting quay";
        StopPlace updated = stopPlaceQuayDeleter.deleteQuay(stopPlaceNetexId, quayNetexId, versionComment);

        assertThat(updated).isNotNull();
        assertThat(updated.getQuays()).isNotNull();
        assertThat(updated.getQuays()).hasSize(1);
        assertThat(updated.getVersionComment()).isEqualTo(versionComment);

    }

    @Transactional
    @Test
    public void testDeleteQuayOfChildStop() {
        StopPlace child = new StopPlace();
        Quay quay = new Quay();
        child.getQuays().add(quay);

        stopPlaceRepository.save(child);

        multiModalStopPlaceEditor.createMultiModalParentStopPlace(Arrays.asList(child.getNetexId()), new EmbeddableMultilingualString("die")).getChildren().iterator().next();

        StopPlace parentStopPlace = stopPlaceQuayDeleter.deleteQuay(child.getNetexId(), quay.getNetexId(), "Delete quay");

        StopPlace actualChild = childFromParentResolver.resolveChildFromParent(parentStopPlace, child.getNetexId(), 0);


        assertThat(actualChild.getQuays()).isEmpty();
        assertThat(actualChild.getVersion()).isEqualTo(child.getVersion()+2);



    }

}
