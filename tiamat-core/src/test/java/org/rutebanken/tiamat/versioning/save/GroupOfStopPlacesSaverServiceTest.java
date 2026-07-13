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

package org.rutebanken.tiamat.versioning.save;

import org.junit.Test;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.GroupOfStopPlaces;
import org.rutebanken.tiamat.model.PurposeOfGrouping;
import org.rutebanken.tiamat.model.SiteRefStructure;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.StopPlaceReference;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class GroupOfStopPlacesSaverServiceTest extends TiamatIntegrationTest {

    @Autowired
    private GroupOfStopPlacesSaverService groupOfStopPlacesSaverService;

    @Autowired
    private PurposeOfGroupingSaverService purposeOfGroupingSaverService;

    @Test
    public void saveNewVersion() throws Exception {

        StopPlace stopPlace = new StopPlace();
        stopPlaceRepository.save(stopPlace);

        GroupOfStopPlaces groupOfStopPlaces = new GroupOfStopPlaces();
        groupOfStopPlaces.setName(new EmbeddableMultilingualString("name"));
        groupOfStopPlaces.getMembers().add(new StopPlaceReference(stopPlace.getNetexId()));
        groupOfStopPlaces.setPurposeOfGrouping(purposeOfGrouping());

        GroupOfStopPlaces saved = groupOfStopPlacesSaverService.saveNewVersion(groupOfStopPlaces);
        assertThat(saved.getVersion()).isOne();
        assertThat(saved.getMembers()).hasSize(1);
        assertThat(saved.getPurposeOfGrouping()).isNotNull();
        assertThat(saved.getPurposeOfGrouping().getNetexId()).isEqualTo("NSR:PurposeOfGrouping:1");
        assertThat(saved.getPurposeOfGrouping().getName().getValue()).isEqualTo("generalization");


        saved.setName(new EmbeddableMultilingualString("name changed"));

        saved = groupOfStopPlacesSaverService.saveNewVersion(groupOfStopPlaces);

        List<GroupOfStopPlaces> all = groupOfStopPlacesRepository.findAll();
        assertThat(all).hasSize(1);
        GroupOfStopPlaces actual = all.getFirst();
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
        groupOfStopPlaces.setPurposeOfGrouping(purposeOfGrouping());

        GroupOfStopPlaces saved = groupOfStopPlacesSaverService.saveNewVersion(groupOfStopPlaces);

        StopPlace stopPlace2 = new StopPlace();
        stopPlaceRepository.save(stopPlace2);

        GroupOfStopPlaces changed = new GroupOfStopPlaces();
        changed.setNetexId(saved.getNetexId());
        changed.setName(new EmbeddableMultilingualString("name"));
        changed.setPurposeOfGrouping(saved.getPurposeOfGrouping());
        changed.getMembers().add(new StopPlaceReference(stopPlace.getNetexId()));
        changed.getMembers().add(new StopPlaceReference(stopPlace2.getNetexId()));


        groupOfStopPlacesSaverService.saveNewVersion(changed);

        List<GroupOfStopPlaces> all = groupOfStopPlacesRepository.findAll();
        assertThat(all).hasSize(1);
        GroupOfStopPlaces actual = all.getFirst();
        assertThat(actual.getMembers())
                .hasSize(2)
                .extracting(StopPlaceReference::getRef).contains(stopPlace.getNetexId(), stopPlace2.getNetexId());

    }

    @Test
    public void shouldRejectNewGOSPWithNoMembers() {
        // Verify that creating a new group without members is rejected
        GroupOfStopPlaces groupOfStopPlaces = new GroupOfStopPlaces();
        groupOfStopPlaces.setName(new EmbeddableMultilingualString("empty group"));
        groupOfStopPlaces.setPurposeOfGrouping(purposeOfGrouping());
        // Deliberately not adding any members

        assertThatThrownBy(() -> groupOfStopPlacesSaverService.saveNewVersion(groupOfStopPlaces))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cannot create a new GroupOfStopPlaces without members");
    }

    @Test
    public void shouldAllowRemovingAllMembersFromExistingGroup() {
        // Create a group with a member first
        StopPlace stopPlace = new StopPlace();
        stopPlaceRepository.save(stopPlace);

        GroupOfStopPlaces groupOfStopPlaces = new GroupOfStopPlaces();
        groupOfStopPlaces.setName(new EmbeddableMultilingualString("group with member"));
        groupOfStopPlaces.getMembers().add(new StopPlaceReference(stopPlace.getNetexId()));
        groupOfStopPlaces.setPurposeOfGrouping(purposeOfGrouping());

        GroupOfStopPlaces saved = groupOfStopPlacesSaverService.saveNewVersion(groupOfStopPlaces);
        assertThat(saved.getMembers()).hasSize(1);

        // Now remove all members - this should be allowed if user has authorization
        GroupOfStopPlaces updated = new GroupOfStopPlaces();
        updated.setNetexId(saved.getNetexId());
        updated.setName(new EmbeddableMultilingualString("group with no members"));
        updated.setPurposeOfGrouping(saved.getPurposeOfGrouping());
        // Not adding any members - removing all

        GroupOfStopPlaces result = groupOfStopPlacesSaverService.saveNewVersion(updated);
        assertThat(result.getMembers()).isEmpty();
        assertThat(result.getVersion()).isEqualTo(2L);
    }

    @Test
    public void shouldIncrementVersionWhenRemovingSomeMembers() {
        // Create a group with multiple members
        StopPlace stopPlace1 = new StopPlace();
        stopPlaceRepository.save(stopPlace1);

        StopPlace stopPlace2 = new StopPlace();
        stopPlaceRepository.save(stopPlace2);

        StopPlace stopPlace3 = new StopPlace();
        stopPlaceRepository.save(stopPlace3);

        GroupOfStopPlaces groupOfStopPlaces = new GroupOfStopPlaces();
        groupOfStopPlaces.setName(new EmbeddableMultilingualString("group with three members"));
        groupOfStopPlaces.getMembers().add(new StopPlaceReference(stopPlace1.getNetexId()));
        groupOfStopPlaces.getMembers().add(new StopPlaceReference(stopPlace2.getNetexId()));
        groupOfStopPlaces.getMembers().add(new StopPlaceReference(stopPlace3.getNetexId()));
        groupOfStopPlaces.setPurposeOfGrouping(purposeOfGrouping());

        GroupOfStopPlaces saved = groupOfStopPlacesSaverService.saveNewVersion(groupOfStopPlaces);
        assertThat(saved.getMembers()).hasSize(3);
        assertThat(saved.getVersion()).isEqualTo(1L);

        // Remove one member, keep two
        GroupOfStopPlaces updated = new GroupOfStopPlaces();
        updated.setNetexId(saved.getNetexId());
        updated.setName(new EmbeddableMultilingualString("group with two members"));
        updated.getMembers().add(new StopPlaceReference(stopPlace1.getNetexId()));
        updated.getMembers().add(new StopPlaceReference(stopPlace2.getNetexId()));
        updated.setPurposeOfGrouping(saved.getPurposeOfGrouping());

        GroupOfStopPlaces result = groupOfStopPlacesSaverService.saveNewVersion(updated);
        assertThat(result.getMembers()).hasSize(2);
        assertThat(result.getVersion()).isEqualTo(2L);
        assertThat(result.getMembers())
                .extracting(StopPlaceReference::getRef)
                .contains(stopPlace1.getNetexId(), stopPlace2.getNetexId())
                .doesNotContain(stopPlace3.getNetexId());
    }

    private PurposeOfGrouping purposeOfGrouping() {
        PurposeOfGrouping purposeOfGrouping = new PurposeOfGrouping();
        purposeOfGrouping.setName(new EmbeddableMultilingualString("generalization"));
        purposeOfGroupingSaverService.saveNewVersion(purposeOfGrouping);
        return purposeOfGrouping;
    }


}