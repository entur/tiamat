package org.rutebanken.tiamat.rest.write;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rutebanken.tiamat.diff.TiamatObjectDiffer;
import org.rutebanken.tiamat.diff.generic.Difference;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.StopTypeEnumeration;
import org.rutebanken.tiamat.rest.validation.StopPlaceMutationValidator;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StopPlaceDomainServiceTest {

    @Mock
    private StopPlaceMutationValidator validator;

    @Mock
    private StopPlaceService stopPlaceService;

    @Mock
    private TiamatObjectDiffer differ;

    private StopPlaceDomainService domainService;

    @BeforeEach
    void setup() {
        domainService = new StopPlaceDomainService(
                validator,
                stopPlaceService,
                differ
        );
    }

    @Test
    void updateStopPlace_NoDifferencesDetected_ThrowsException() throws IllegalAccessException {
        String stopPlaceId = "NSR:StopPlace:100";
        StopPlace existingStopPlace = createStopPlace(stopPlaceId, "Test Stop", 1L);
        StopPlace updatedStopPlace = createStopPlace(stopPlaceId, "Test Stop", 2L);

        when(validator.validateStopPlaceUpdate(stopPlaceId, false)).thenReturn(existingStopPlace);
        when(differ.compareObjects(existingStopPlace, updatedStopPlace)).thenReturn(Collections.emptyList());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> domainService.updateStopPlace(updatedStopPlace)
        );

        assertEquals(
                "No changes detected for StopPlace with id NSR:StopPlace:100",
                exception.getMessage()
        );

        verify(validator).validateStopPlaceUpdate(stopPlaceId, false);
        verify(differ).compareObjects(existingStopPlace, updatedStopPlace);
        verify(validator, never()).validateStopPlaceName(any());
        verify(stopPlaceService, never()).updateStopPlace(any(), any());
    }

    @Test
    void updateStopPlace_DifferencesDetected_UpdatesSuccessfully()
            throws IllegalAccessException {
        String stopPlaceId = "NSR:StopPlace:100";
        StopPlace existingStopPlace = createStopPlace(stopPlaceId, "Old Name", 1L);
        StopPlace updatedStopPlace = createStopPlace(stopPlaceId, "New Name", 2L);
        StopPlace savedStopPlace = createStopPlace(stopPlaceId, "New Name", 2L);

        when(validator.validateStopPlaceUpdate(stopPlaceId, false)).thenReturn(
                existingStopPlace
        );
        when(
                differ.compareObjects(existingStopPlace, updatedStopPlace)
        ).thenReturn(List.of(new Difference("name", "Old Name", "New Name")));
        when(
                stopPlaceService.updateStopPlace(
                        existingStopPlace,
                        updatedStopPlace
                )
        ).thenReturn(savedStopPlace);

        StopPlace result = domainService.updateStopPlace(updatedStopPlace);

        assertNotNull(result);
        assertEquals("New Name", result.getName().getValue());
        verify(validator).validateStopPlaceUpdate(stopPlaceId, false);
        verify(differ).compareObjects(existingStopPlace, updatedStopPlace);
        verify(validator).validateStopPlaceName(updatedStopPlace);
        verify(stopPlaceService).updateStopPlace(
                existingStopPlace,
                updatedStopPlace
        );
    }

    @Test
    void createStopPlace_Success() {
        StopPlace newStopPlace = createStopPlace(null, "New Stop", 1L);
        StopPlace savedStopPlace = createStopPlace(
                "NSR:StopPlace:200",
                "New Stop",
                1L
        );

        when(stopPlaceService.createStopPlace(newStopPlace)).thenReturn(
                savedStopPlace
        );

        StopPlace result = domainService.createStopPlace(newStopPlace);

        assertNotNull(result);
        assertEquals("NSR:StopPlace:200", result.getNetexId());
        assertEquals("New Stop", result.getName().getValue());
        verify(validator).validateStopPlaceName(newStopPlace);
        verify(stopPlaceService).createStopPlace(newStopPlace);
    }

    @Test
    void deleteStopPlace_Success() {
        String stopPlaceId = "NSR:StopPlace:300";

        domainService.deleteStopPlace(stopPlaceId);

        verify(stopPlaceService).deleteStopPlace(stopPlaceId);
    }

    @Test
    void updateStopPlace_ValidationFails_ThrowsException()
            throws IllegalAccessException {
        String stopPlaceId = "NSR:StopPlace:100";
        StopPlace updatedStopPlace = createStopPlace(
                stopPlaceId,
                "Invalid Stop",
                1L
        );

        when(validator.validateStopPlaceUpdate(stopPlaceId, false)).thenThrow(
                new IllegalArgumentException("Stop place not found")
        );

        assertThrows(IllegalArgumentException.class, () ->
                domainService.updateStopPlace(updatedStopPlace)
        );

        verify(validator).validateStopPlaceUpdate(stopPlaceId, false);
        verify(differ, never()).compareObjects(any(), any());
        verify(stopPlaceService, never()).updateStopPlace(any(), any());
    }

    @Test
    void updateStopPlace_NameValidationFails_ThrowsException()
            throws IllegalAccessException {
        String stopPlaceId = "NSR:StopPlace:100";
        StopPlace existingStopPlace = createStopPlace(stopPlaceId, "Old Name", 1L);
        StopPlace updatedStopPlace = createStopPlace(stopPlaceId, "", 2L);

        when(validator.validateStopPlaceUpdate(stopPlaceId, false)).thenReturn(
                existingStopPlace
        );
        when(
                differ.compareObjects(existingStopPlace, updatedStopPlace)
        ).thenReturn(List.of(new Difference("name", "Old Name", "")));
        doThrow(new IllegalArgumentException("Stop place name is required"))
                .when(validator)
                .validateStopPlaceName(updatedStopPlace);

        assertThrows(IllegalArgumentException.class, () ->
                domainService.updateStopPlace(updatedStopPlace)
        );

        verify(validator).validateStopPlaceUpdate(stopPlaceId, false);
        verify(differ).compareObjects(existingStopPlace, updatedStopPlace);
        verify(validator).validateStopPlaceName(updatedStopPlace);
        verify(stopPlaceService, never()).updateStopPlace(any(), any());
    }

    private StopPlace createStopPlace(String netexId, String name, Long version) {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setNetexId(netexId);
        stopPlace.setStopPlaceType(StopTypeEnumeration.BUS_STATION);

        EmbeddableMultilingualString multilingualName = new EmbeddableMultilingualString();
        multilingualName.setValue(name);
        stopPlace.setName(multilingualName);

        stopPlace.setVersion(version);

        return stopPlace;
    }
}
