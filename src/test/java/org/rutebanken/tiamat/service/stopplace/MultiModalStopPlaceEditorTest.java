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

import com.google.common.collect.Sets;
import org.junit.Test;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.ValidBetween;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
public class MultiModalStopPlaceEditorTest extends TiamatIntegrationTest {

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Autowired
    private MultiModalStopPlaceEditor multiModalStopPlaceEditor;

    private Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);

    @Test
    public void testCreateMultiModalParentStopPlaceDoNotAllowEmptyListOfStopPlace() {
        List<String> childIds = new ArrayList<>();
        assertThatThrownBy(() -> multiModalStopPlaceEditor.createMultiModalParentStopPlace(childIds, new EmbeddableMultilingualString("name")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testAddToMultiModalParentStopPlaceDoNotAllowEmptyListOfStopPlace() {

        StopPlace parent = new StopPlace();
        parent.setParentStopPlace(true);
        stopPlaceRepository.save(parent);
        List<String> childIds = new ArrayList<>();
        assertThatThrownBy(() -> multiModalStopPlaceEditor.addToMultiModalParentStopPlace(parent.getNetexId(), childIds))
                .isInstanceOf(IllegalArgumentException.class);

    }

    @Test
    public void testCreateMultiModalParentStopPlace() {

        List<StopPlace> children = new ArrayList<>();
        children.add(createStopPlace("StopPlace - 1"));
        children.add(createStopPlace("StopPlace - 2"));
        children.add(createStopPlace("StopPlace - 3"));
        children.add(createStopPlace("StopPlace - 4"));

        List<StopPlace> savedChildren = stopPlaceRepository.saveAll(children);

        List<String> childIds = new ArrayList<>();
        savedChildren.forEach(sp -> {
            childIds.add(sp.getNetexId());
        });

        String parentStopPlaceName = "Super StopPlace";
        StopPlace result = multiModalStopPlaceEditor.createMultiModalParentStopPlace(childIds, new EmbeddableMultilingualString(parentStopPlaceName));
        assertThat(result.getName().getValue()).isEqualTo(parentStopPlaceName);
        assertThat(result.getVersion()).isEqualTo(1L);
        assertThat(result.getValidBetween()).isNotNull();
        assertThat(result.getNetexId()).isNotNull();

        assertThatChildsAreReferencingParent(childIds, result);
        verifyChildValidBetween(result);
    }

    @Test
    public void testCreateMultiModalParentStopPlaceOnlyRemoveChildStopPlaceNameIfEqual() {

        String equalStopPlaceName = "StopPlaceName";
        String equalStopPlaceNameLowerCase = equalStopPlaceName.toLowerCase();
        String differentChildStopPlaceName = "StopPlaceNameChild";

        String nb = "nb";
        String en = "en";

        List<StopPlace> children = new ArrayList<>();
        children.add(createStopPlace(equalStopPlaceNameLowerCase, nb));
        children.add(createStopPlace(differentChildStopPlaceName, nb));
        children.add(createStopPlace(equalStopPlaceName, en));

        List<StopPlace> savedChildren = stopPlaceRepository.saveAll(children);

        List<String> childIds = new ArrayList<>();
        savedChildren.forEach(sp -> {
            childIds.add(sp.getNetexId());
        });


        StopPlace result = multiModalStopPlaceEditor.createMultiModalParentStopPlace(childIds, new EmbeddableMultilingualString(equalStopPlaceName, nb));
        assertThat(result.getName().getValue()).isEqualTo(equalStopPlaceName);
        assertThat(result.getVersion()).isEqualTo(1L);
        assertThat(result.getValidBetween()).isNotNull();
        assertThat(result.getNetexId()).isNotNull();

        assertThatChildsAreReferencingParent(childIds, result);
        assertThat(result.getChildren())
                .extracting(StopPlace::getName)
                .containsExactlyInAnyOrder(
                        new EmbeddableMultilingualString(differentChildStopPlaceName, nb),
                        // Same name but different language should not have been removed:
                        new EmbeddableMultilingualString(equalStopPlaceName, en),
                        null)
                .doesNotContain(new EmbeddableMultilingualString(equalStopPlaceName, nb));

        verifyChildValidBetween(result);
    }

    @Test
    public void testCreateMultiModalParentStopPlaceWithFutureValidBetween() {

        StopPlace child = stopPlaceRepository.save(createStopPlace("StopPlace - 1"));

        Instant futureTime = now.plusSeconds(600);


        String parentStopPlaceName = "Super Duper StopPlace";
        String versionComment = "version comment";
        StopPlace result = multiModalStopPlaceEditor.createMultiModalParentStopPlace(Arrays.asList(child.getNetexId()),
                new EmbeddableMultilingualString(parentStopPlaceName), new ValidBetween(futureTime), versionComment, null);

        assertThat(result.getValidBetween().getFromDate()).isEqualTo(futureTime);

        assertThatChildsAreReferencingParent(Arrays.asList(child.getNetexId()), result);
        verifyChildValidBetween(result);
    }

    @Test
    public void testAddToMultiModalParentStopPlaceWithFutureValidBetween() {

        StopPlace child = stopPlaceRepository.save(createStopPlace("StopPlace - 1"));

        Instant futureTime = now.plusSeconds(600);


        String parentStopPlaceName = "Super Duper StopPlace +1";
        StopPlace parent = new StopPlace(new EmbeddableMultilingualString(parentStopPlaceName));
        parent.setParentStopPlace(true);

        parent = stopPlaceRepository.save(parent);

        StopPlace result = multiModalStopPlaceEditor.addToMultiModalParentStopPlace(parent.getNetexId(), Arrays.asList(child.getNetexId()),
                new ValidBetween(futureTime), null);

        assertThatChildsAreReferencingParent(Arrays.asList(child.getNetexId()), result);
        verifyChildValidBetween(result);
    }


    @Test
    public void testCreateMultiModalParentStopPlaceFromVersionedChild() {

        StopPlace firstStopPlace = createStopPlace("first");
        firstStopPlace.setVersion(1L);
        firstStopPlace = stopPlaceRepository.save(firstStopPlace);

        StopPlace firstStopPlaceVersion2 = createStopPlace("first");
        firstStopPlaceVersion2.setVersion(2L);
        firstStopPlaceVersion2.setNetexId(firstStopPlace.getNetexId());

        firstStopPlaceVersion2 = stopPlaceRepository.save(firstStopPlaceVersion2);

        StopPlace secondStopPlace = createStopPlace("second");
        secondStopPlace.setVersion(1L);
        secondStopPlace = stopPlaceRepository.save(secondStopPlace);

        List<String> childIds = Stream.of(firstStopPlaceVersion2, secondStopPlace).map(sp -> sp.getNetexId()).toList();

        String parentStopPlaceName = "Super duper StopPlace";
        StopPlace superDuperStopPlace = multiModalStopPlaceEditor.createMultiModalParentStopPlace(childIds, new EmbeddableMultilingualString(parentStopPlaceName));

        assertThatChildsAreReferencingParent(childIds, superDuperStopPlace);

        StopPlace acutalFirstStopPlace = stopPlaceRepository.findFirstByNetexIdAndVersion(firstStopPlace.getNetexId(), firstStopPlace.getVersion());
        assertThat(acutalFirstStopPlace).as("First version of first stop place should not have it's version changed").isNotNull();

        StopPlace acutalNewVersionOfFirstStopPlaceVersion2 = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(firstStopPlace.getNetexId());
        assertThat(acutalNewVersionOfFirstStopPlaceVersion2.getVersion()).as("Previous version 2 of stop place should now be version 3").isEqualTo(3L);

        StopPlace acutalSecondStopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(secondStopPlace.getNetexId());
        assertThat(acutalSecondStopPlace.getVersion()).as("Second stop place version should have version 2 after joining parent stop place").isEqualTo(2L);
    }

    @Test
    public void testAddToMultiModalParentStopPlace() {

        StopPlace existingChild = createStopPlace("existingChild");
        existingChild.setVersion(1L);
        existingChild = stopPlaceRepository.save(existingChild);

        String parentStopPlaceName = "Super duper StopPlace";
        StopPlace parent = multiModalStopPlaceEditor.createMultiModalParentStopPlace(Arrays.asList(existingChild.getNetexId()), new EmbeddableMultilingualString(parentStopPlaceName));

        StopPlace newChild = createStopPlace("new child");
        newChild.setVersion(1L);
        newChild.setValidBetween(new ValidBetween(now.minusSeconds(1000)));
        newChild = stopPlaceRepository.save(newChild);

        parent = multiModalStopPlaceEditor.addToMultiModalParentStopPlace(parent.getNetexId(), Arrays.asList(newChild.getNetexId()));

        assertThat(parent.getChildren()).hasSize(2);
        assertThat(parent.getChildren()).extracting(StopPlace::getNetexId)
                .contains(existingChild.getNetexId())
                .contains(newChild.getNetexId());

        verifyChildValidBetween(parent, Sets.newHashSet(newChild.getNetexId()));
    }

    @Test
    public void testRemovingStopPlaceFromMultiModalParentStopPlace() {

        StopPlace childToKeep = createStopPlace("existingChild");
        childToKeep.setVersion(1L);
        childToKeep.setCreated(now.minus(1, ChronoUnit.DAYS));
        childToKeep = stopPlaceRepository.save(childToKeep);

        StopPlace childToRemove = createStopPlace("existingChild 2");
        childToRemove.setVersion(1L);
        childToRemove.setCreated(now);
        childToRemove = stopPlaceRepository.save(childToRemove);

        String parentStopPlaceName = "Parent StopPlace about to lose a child";
        StopPlace parent = multiModalStopPlaceEditor.createMultiModalParentStopPlace(Arrays.asList(childToKeep.getNetexId(), childToRemove.getNetexId()), new EmbeddableMultilingualString(parentStopPlaceName));
        stopPlaceRepository.flush();

        parent = multiModalStopPlaceEditor.removeFromMultiModalStopPlace(parent.getNetexId(), Arrays.asList(childToRemove.getNetexId()));

        assertThat(parent.getChildren()).hasSize(1);
        assertThat(parent.getChildren()).extracting(StopPlace::getNetexId)
                .doesNotContain(childToRemove.getNetexId())
                .contains(childToKeep.getNetexId());
    }

    @Test
    public void testRemovingStopPlaceFromMultiModalParentStopPlaceKeepName() {
        String parentStopPlaceName = "Parent StopPlace name";

        String lang = "en";
        StopPlace childToRemove = createStopPlace(parentStopPlaceName, lang);
        childToRemove.setVersion(1L);
        childToRemove.setCreated(now);
        childToRemove = stopPlaceRepository.save(childToRemove);


        StopPlace parent = multiModalStopPlaceEditor.createMultiModalParentStopPlace(Arrays.asList(childToRemove.getNetexId()), new EmbeddableMultilingualString(parentStopPlaceName, lang));
        stopPlaceRepository.flush();

        parent = multiModalStopPlaceEditor.removeFromMultiModalStopPlace(parent.getNetexId(), Arrays.asList(childToRemove.getNetexId()));

        assertThat(parent.getChildren()).isEmpty();

        StopPlace actualRemovedChild = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(childToRemove.getNetexId());
        assertThat(actualRemovedChild.getName())
                .isNotNull()
                .isEqualTo(new EmbeddableMultilingualString(parentStopPlaceName, lang));
    }

    @Test(expected = Exception.class)
    public void testNotAllowsChildStopWithFutureVersion() {

        StopPlace child = createStopPlace("child candidate");
        child.setValidBetween(new ValidBetween(now.plus(10, ChronoUnit.DAYS)));
        child = stopPlaceRepository.save(child);

        String parentStopPlaceName = "Super StopPlace";
        multiModalStopPlaceEditor.createMultiModalParentStopPlace(Arrays.asList(child.getNetexId()), new EmbeddableMultilingualString(parentStopPlaceName));
    }

    private void verifyChildValidBetween(StopPlace parentStopPlace) {
        verifyChildValidBetween(parentStopPlace, Sets.newHashSet());
    }

    private void verifyChildValidBetween(StopPlace parentStopPlace, Set<String> filter) {
        for(StopPlace actualChild : parentStopPlace.getChildren()) {

            if(!filter.isEmpty() && !filter.contains(actualChild)) {
                continue;
            }
            assertThat(actualChild.getValidBetween()).as("child valid between must be null").isNull();

            StopPlace previousChildVersion = stopPlaceRepository.findFirstByNetexIdAndVersion(actualChild.getNetexId(), 1);

            String childIdAndVersion = previousChildVersion.getNetexId() + " " + previousChildVersion.getVersion();

            // NRP-2234
            assertThat(previousChildVersion.getValidBetween())
                    .as("previous version of child stop " + childIdAndVersion + " valid between")
                    .isNotNull();


            Instant expectedToDate = parentStopPlace.getValidBetween().getFromDate().minusMillis(1);

            assertThat(previousChildVersion.getValidBetween().getToDate())
                    .as("previous version of child stop " + childIdAndVersion + " to date equal to parent stop place from date plus one milli second")
                    .isNotNull()
                    .isEqualTo(expectedToDate);
        }
    }

    private void assertThatChildsAreReferencingParent(List<String> childIds, StopPlace parent) {
        childIds.forEach(id -> {
            StopPlace child = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(id);
            assertThat(child.getParentSiteRef()).as("child stop " + id + " must have parent site ref").isNotNull();
            assertThat(child.getParentSiteRef().getRef()).as("child stop " + id + " must have parent site ref matching matching parent's netex id").isEqualTo(parent.getNetexId());
            assertThat(child.getParentSiteRef().getVersion()).as("child stop " + id + " must have parent site ref version").isEqualTo(String.valueOf(parent.getVersion()));
        });
    }

    private StopPlace createStopPlace(String name, String lang) {
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString(name, lang));
        stopPlace.setValidBetween(new ValidBetween(now.minusSeconds(120)));
        stopPlace.setVersion(1L);
        return stopPlace;
    }

    private StopPlace createStopPlace(String name) {
        return createStopPlace(name, null);
    }
}