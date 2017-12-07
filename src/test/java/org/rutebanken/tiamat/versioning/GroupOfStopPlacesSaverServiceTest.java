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

package org.rutebanken.tiamat.versioning;

import org.junit.Test;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.model.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.assertThatThrownBy;

public class GroupOfStopPlacesSaverServiceTest extends TiamatIntegrationTest {

    @Autowired
    private GroupOfStopPlacesSaverService groupOfStopPlacesSaverService;

    @Test
    public void saveNewVersion() throws Exception {

        StopPlace stopPlace = new StopPlace();
        stopPlaceRepository.save(stopPlace);

        GroupOfStopPlaces groupOfStopPlaces = new GroupOfStopPlaces();
        groupOfStopPlaces.setName(new EmbeddableMultilingualString("name"));
        groupOfStopPlaces.getMembers().add(new StopPlaceReference(stopPlace.getNetexId()));

        GroupOfStopPlaces saved = groupOfStopPlacesSaverService.saveNewVersion(groupOfStopPlaces);
        assertThat(saved.getVersion()).isOne();
        assertThat(saved.getMembers()).hasSize(1);

        saved.setName(new EmbeddableMultilingualString("name changed"));

        saved = groupOfStopPlacesSaverService.saveNewVersion(groupOfStopPlaces);

        List<GroupOfStopPlaces> all = groupOfStopPlacesRepository.findAll();
        assertThat(all).hasSize(1);
        GroupOfStopPlaces actual = all.get(0);
        assertThat(actual.getVersion()).isEqualTo(2L);
        assertThat(actual.getName().getValue()).isEqualTo("name changed");
        assertThat(actual.getMembers()).hasSize(1);

    }

    @Test
    public void saveNewVersionDoesNotAcceptInvalidStopPlaceRefs() throws Exception {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setNetexId("NetexIdDoesNotExist");

        GroupOfStopPlaces groupOfStopPlaces = new GroupOfStopPlaces();
        groupOfStopPlaces.setName(new EmbeddableMultilingualString("name"));
        groupOfStopPlaces.getMembers().add(new StopPlaceReference(stopPlace.getNetexId()));


        assertThatThrownBy(() -> groupOfStopPlacesSaverService.saveNewVersion(groupOfStopPlaces)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void saveNewVersionDoesNotAcceptChildStopPlaceRefs() throws Exception {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setParentSiteRef(new SiteRefStructure("NSR:StopPlace:99999"));
        stopPlaceRepository.save(stopPlace);


        GroupOfStopPlaces groupOfStopPlaces = new GroupOfStopPlaces();
        groupOfStopPlaces.setName(new EmbeddableMultilingualString("name"));
        groupOfStopPlaces.getMembers().add(new StopPlaceReference(stopPlace.getNetexId()));


        assertThatThrownBy(() -> groupOfStopPlacesSaverService.saveNewVersion(groupOfStopPlaces)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void addMembers() throws Exception {

        StopPlace stopPlace = new StopPlace();
        stopPlaceRepository.save(stopPlace);

        GroupOfStopPlaces groupOfStopPlaces = new GroupOfStopPlaces();
        groupOfStopPlaces.setName(new EmbeddableMultilingualString("name"));
        groupOfStopPlaces.getMembers().add(new StopPlaceReference(stopPlace.getNetexId()));

        GroupOfStopPlaces saved = groupOfStopPlacesSaverService.saveNewVersion(groupOfStopPlaces);

        StopPlace stopPlace2 = new StopPlace();
        stopPlaceRepository.save(stopPlace2);

        GroupOfStopPlaces changed = new GroupOfStopPlaces();
        changed.setNetexId(saved.getNetexId());
        changed.setName(new EmbeddableMultilingualString("name"));
        changed.getMembers().add(new StopPlaceReference(stopPlace.getNetexId()));
        changed.getMembers().add(new StopPlaceReference(stopPlace2.getNetexId()));


        groupOfStopPlacesSaverService.saveNewVersion(changed);

        List<GroupOfStopPlaces> all = groupOfStopPlacesRepository.findAll();
        assertThat(all).hasSize(1);
        GroupOfStopPlaces actual = all.get(0);
        assertThat(actual.getMembers())
                .hasSize(2)
                .extracting(StopPlaceReference::getRef).contains(stopPlace.getNetexId(), stopPlace2.getNetexId());

    }


}