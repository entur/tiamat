package org.rutebanken.tiamat.rest.validation;

import org.junit.Test;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.ModificationEnumeration;
import org.rutebanken.tiamat.model.SiteRefStructure;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.repository.StopPlaceRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StopPlaceMutationValidatorTest {

    private final StopPlaceRepository stopPlaceRepository = mock(StopPlaceRepository.class);

    private final StopPlaceMutationValidator stopPlaceMutationValidator = new StopPlaceMutationValidator(stopPlaceRepository);

    @Test
    public void givenStopPlaceWithName_whenValidateStopPlaceName_thenPassValidation() {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new EmbeddableMultilingualString("name", "en"));

        assertThatCode(() -> stopPlaceMutationValidator.validateStopPlaceName(stopPlace))
                .doesNotThrowAnyException();
    }

    @Test
    public void givenStopPlaceWithoutName_whenValidateStopPlaceName_thenThrowException() {
        StopPlace stopPlace = new StopPlace();
        assertThatThrownBy(() -> stopPlaceMutationValidator.validateStopPlaceName(stopPlace))
                .hasMessageContaining("Stop place must have name set");
    }

    @Test
    public void givenNullStopPlace_whenVerifyStopPlaceNotNull_thenThrowException() {
        assertThatThrownBy(() -> stopPlaceMutationValidator.verifyStopPlaceNotNull(null, "test-id"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Attempting to update StopPlace [id = test-id], but StopPlace does not exist.");
    }

    @Test
    public void givenNonNullStopPlace_whenVerifyStopPlaceNBotNull_thenPassValidation() {
        assertThatCode(() -> stopPlaceMutationValidator.verifyStopPlaceNotNull(new StopPlace(), "test-id"))
                .doesNotThrowAnyException();
    }

    @Test
    public void givenValidParentChildRelation_whenValidateChildBelongsToParent_thenPassValidation() {
        StopPlace parent = new StopPlace();
        parent.setNetexId("parent-id");
        parent.setVersion(1L);

        StopPlace child = new StopPlace();
        child.setNetexId("child-id");
        child.setParentSiteRef(new SiteRefStructure(parent.getNetexId(), String.valueOf(parent.getVersion())));

        assertThatCode(() -> stopPlaceMutationValidator.validateChildBelongsToParent(child, parent))
                .doesNotThrowAnyException();
    }

    @Test
    public void givenChildStopDoesNotHaveParentSiteRef_whenValidateChildBelongsToParent_thenThrowException() {
        StopPlace parent = new StopPlace();
        parent.setNetexId("parent-id");
        StopPlace child = new StopPlace();
        child.setNetexId("child-id");

        assertThatThrownBy(() -> stopPlaceMutationValidator.validateChildBelongsToParent(child, parent))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Child stop [id = child-id] does not belong to any parent");
    }

    @Test
    public void givenChildStopDoesNotReferenceCorrectParent_whenValidateChildBelongsToParent_thenThrowException() {
        StopPlace parent = new StopPlace();
        parent.setNetexId("parent-id");
        StopPlace child = new StopPlace();
        child.setNetexId("child-id");
        child.setParentSiteRef(new SiteRefStructure("another-parent", "1"));

        assertThatThrownBy(() -> stopPlaceMutationValidator.validateChildBelongsToParent(child, parent))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Child stop [id = child-id] does not belong to parent parent-id");
    }

    @Test
    public void givenChildStopParentReferenceVersionDoesNotMatchParent_whenValidateChildBelongsToParent_thenThrowException() {
        StopPlace parent = new StopPlace();
        parent.setNetexId("parent-id");
        parent.setVersion(2L);
        StopPlace child = new StopPlace();
        child.setNetexId("child-id");
        child.setParentSiteRef(new SiteRefStructure(parent.getNetexId(), "1"));

        assertThatThrownBy(() -> stopPlaceMutationValidator.validateChildBelongsToParent(child, parent))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Child stop [id = child-id] does not refer to parent parent-id in correct version: 2");
    }

    @Test
    public void givenValidStopPlaceUpdate_whenValidateStopPlaceUpdate_thenPassValidationAndReturnExistingStopPlace() {
        StopPlace existingStopPlace = new StopPlace();
        existingStopPlace.setNetexId("id");
        existingStopPlace.setModificationEnumeration(ModificationEnumeration.NEW);
        existingStopPlace.setParentStopPlace(false);

        when(stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc("id")).thenReturn(existingStopPlace);

        StopPlace result = stopPlaceMutationValidator.validateStopPlaceUpdate(existingStopPlace.getNetexId(), false);
        assertThat(result).isNotNull();
    }

    @Test
    public void givenValidParentStopPlaceUpdate_whenValidateStopPlaceUpdate_thenPassValidationAndReturnExistingStopPlace() {
        StopPlace existingStopPlace = new StopPlace();
        existingStopPlace.setNetexId("id");
        existingStopPlace.setModificationEnumeration(ModificationEnumeration.NEW);
        existingStopPlace.setParentStopPlace(true);

        when(stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc("id")).thenReturn(existingStopPlace);

        StopPlace result = stopPlaceMutationValidator.validateStopPlaceUpdate(existingStopPlace.getNetexId(), true);
        assertThat(result).isNotNull();
    }

    @Test
    public void givenNonExistentStopPlace_whenValidateStopPlaceUpdate_thenThrowException() {
        assertThatThrownBy(() -> stopPlaceMutationValidator.validateStopPlaceUpdate("non-existent", false))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Stop place [id = non-existent] does not exist");
    }

    @Test
    public void givenDeletedStopPlace_whenValidateStopPlaceUpdate_thenThrowException() {
        StopPlace existingStopPlace = new StopPlace();
        existingStopPlace.setNetexId("id");
        existingStopPlace.setModificationEnumeration(ModificationEnumeration.DELETE);

        when(stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc("id")).thenReturn(existingStopPlace);

        assertThatThrownBy(() -> stopPlaceMutationValidator.validateStopPlaceUpdate(existingStopPlace.getNetexId(), false))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cannot update/reactivate terminated stop place");
    }

    @Test
    public void givenIsParentStopPlaceFalseForParentMutationTrue_whenValidateStopPlaceUpdate_thenThrowException() {
        StopPlace existingStopPlace = new StopPlace();
        existingStopPlace.setNetexId("id");
        existingStopPlace.setModificationEnumeration(ModificationEnumeration.NEW);
        existingStopPlace.setParentStopPlace(false);

        when(stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc("id")).thenReturn(existingStopPlace);

        assertThatThrownBy(() -> stopPlaceMutationValidator.validateStopPlaceUpdate(existingStopPlace.getNetexId(), true))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Stop place [id = id] is not a parent");
    }

    @Test
    public void givenIsParentStopPlaceTrueForParentMutationFalse_whenValidateStopPlaceUpdate_thenThrowException() {
        StopPlace existingStopPlace = new StopPlace();
        existingStopPlace.setNetexId("id");
        existingStopPlace.setModificationEnumeration(ModificationEnumeration.NEW);
        existingStopPlace.setParentStopPlace(true);

        when(stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc("id")).thenReturn(existingStopPlace);

        assertThatThrownBy(() -> stopPlaceMutationValidator.validateStopPlaceUpdate(existingStopPlace.getNetexId(), false))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cannot update parent stop place [id = id] with this mutation");
    }

    @Test
    public void givenParentSiteReference_whenValidateStopPlaceUpdate_thenThrowException() {
        StopPlace existingStopPlace = new StopPlace();
        existingStopPlace.setNetexId("id");
        existingStopPlace.setModificationEnumeration(ModificationEnumeration.NEW);
        existingStopPlace.setParentStopPlace(false);
        existingStopPlace.setParentSiteRef(new SiteRefStructure("parent-ref", "1"));

        when(stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc("id")).thenReturn(existingStopPlace);

        assertThatThrownBy(() -> stopPlaceMutationValidator.validateStopPlaceUpdate(existingStopPlace.getNetexId(), false))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cannot update stop place [id = id] which has parent. Edit parent instead");
    }

}