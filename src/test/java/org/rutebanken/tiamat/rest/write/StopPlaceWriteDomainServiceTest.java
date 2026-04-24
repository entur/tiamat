package org.rutebanken.tiamat.rest.write;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rutebanken.tiamat.diff.TiamatObjectDiffer;
import org.rutebanken.tiamat.diff.generic.GenericObjectDiffer;
import org.rutebanken.tiamat.lock.MutateLock;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.StopTypeEnumeration;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.rest.validation.StopPlaceMutationValidator;
import org.rutebanken.tiamat.service.stopplace.StopPlaceTerminator;
import org.rutebanken.tiamat.versioning.save.StopPlaceVersionedSaverService;

import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StopPlaceWriteDomainServiceTest {

    @Mock
    private StopPlaceMutationValidator validator;

    @Mock
    private StopPlaceVersionedSaverService stopPlaceVersionedSaverService;

    @Mock
    private StopPlaceTerminator stopPlaceTerminator;

    @Mock
    private StopPlaceRepository stopPlaceRepository;

    private StopPlaceWriteDomainService domainService;

    private final MutateLock mutateLock = new MutateLock(null) {
        @Override
        public <T> T executeInLock(Supplier<T> supplier) {
            return supplier.get();
        }
    };

    @BeforeEach
    void setup() {
        TiamatObjectDiffer differ = new TiamatObjectDiffer(new GenericObjectDiffer());
        domainService = new StopPlaceWriteDomainService(
            validator,
            stopPlaceVersionedSaverService,
            stopPlaceTerminator,
            stopPlaceRepository,
            differ,
            mutateLock
        );
    }

    @Test
    void updateStopPlace_NoDifferencesDetected_ThrowsException() {
        String stopPlaceId = "NSR:StopPlace:100";
        // Same version so version check passes
        StopPlace existingStopPlace = createStopPlace(stopPlaceId, "Test Stop", 1L);
        StopPlace updatedStopPlace = createStopPlace(stopPlaceId, "Test Stop", 1L);

        when(validator.validateStopPlaceUpdate(stopPlaceId, false)).thenReturn(existingStopPlace);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> domainService.updateStopPlace(updatedStopPlace)
        );

        assertEquals(
            "No changes detected for StopPlace with id NSR:StopPlace:100",
            exception.getMessage()
        );

        verify(validator).validateStopPlaceUpdate(stopPlaceId, false);
        verify(validator).validateStopPlaceName(updatedStopPlace);
        verify(stopPlaceVersionedSaverService, never()).saveNewVersion(any(), any());
    }

    @Test
    void updateStopPlace_DifferencesDetected_UpdatesSuccessfully() throws Exception {
        String stopPlaceId = "NSR:StopPlace:100";
        // Same version so version check passes
        StopPlace existingStopPlace = createStopPlace(stopPlaceId, "Old Name", 1L);
        StopPlace updatedStopPlace = createStopPlace(stopPlaceId, "New Name", 1L);
        StopPlace savedStopPlace = createStopPlace(stopPlaceId, "New Name", 2L);

        when(validator.validateStopPlaceUpdate(stopPlaceId, false)).thenReturn(existingStopPlace);
        when(stopPlaceVersionedSaverService.saveNewVersion(existingStopPlace, updatedStopPlace))
            .thenReturn(savedStopPlace);

        StopPlace result = domainService.updateStopPlace(updatedStopPlace);

        assertNotNull(result);
        assertEquals("New Name", result.getName().getValue());
        verify(validator).validateStopPlaceUpdate(stopPlaceId, false);
        verify(validator).validateStopPlaceName(updatedStopPlace);
        verify(stopPlaceVersionedSaverService).saveNewVersion(existingStopPlace, updatedStopPlace);
    }

    @Test
    void updateStopPlace_VersionMismatch_ThrowsException() {
        String stopPlaceId = "NSR:StopPlace:100";
        StopPlace existingStopPlace = createStopPlace(stopPlaceId, "Test Stop", 1L);
        StopPlace updatedStopPlace = createStopPlace(stopPlaceId, "Test Stop", 2L);

        when(validator.validateStopPlaceUpdate(stopPlaceId, false)).thenReturn(existingStopPlace);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> domainService.updateStopPlace(updatedStopPlace)
        );

        assertEquals(
            "Version mismatch for StopPlace with id NSR:StopPlace:100: expected version 1, but got 2",
            exception.getMessage()
        );

        verify(validator).validateStopPlaceUpdate(stopPlaceId, false);
        verify(validator, never()).validateStopPlaceName(any());
        verify(stopPlaceVersionedSaverService, never()).saveNewVersion(any(), any());
    }

    @Test
    void createStopPlace_Success() {
        StopPlace newStopPlace = createStopPlace(null, "New Stop", 1L);
        StopPlace savedStopPlace = createStopPlace("NSR:StopPlace:200", "New Stop", 1L);

        when(stopPlaceVersionedSaverService.saveNewVersion(newStopPlace)).thenReturn(savedStopPlace);

        StopPlace result = domainService.createStopPlace(newStopPlace);

        assertNotNull(result);
        assertEquals("NSR:StopPlace:200", result.getNetexId());
        assertEquals("New Stop", result.getName().getValue());
        verify(validator).validateStopPlaceName(newStopPlace);
        verify(stopPlaceVersionedSaverService).saveNewVersion(newStopPlace);
    }

    @Test
    void deleteStopPlace_Success() {
        String stopPlaceId = "NSR:StopPlace:300";

        domainService.deleteStopPlace(stopPlaceId);

        verify(stopPlaceTerminator).terminateStopPlace(
            any(String.class),
            any(),
            any(String.class),
            any()
        );
    }

    @Test
    void updateStopPlace_ValidationFails_ThrowsException() {
        String stopPlaceId = "NSR:StopPlace:100";
        StopPlace updatedStopPlace = createStopPlace(stopPlaceId, "Invalid Stop", 1L);

        when(validator.validateStopPlaceUpdate(stopPlaceId, false)).thenThrow(
            new IllegalArgumentException("Stop place not found")
        );

        assertThrows(IllegalArgumentException.class, () ->
            domainService.updateStopPlace(updatedStopPlace)
        );

        verify(validator).validateStopPlaceUpdate(stopPlaceId, false);
        verify(validator, never()).validateStopPlaceName(any());
        verify(stopPlaceVersionedSaverService, never()).saveNewVersion(any(), any());
    }

    @Test
    void updateStopPlace_NameValidationFails_ThrowsException() {
        String stopPlaceId = "NSR:StopPlace:100";
        StopPlace existingStopPlace = createStopPlace(stopPlaceId, "Old Name", 1L);
        StopPlace updatedStopPlace = createStopPlace(stopPlaceId, "", 1L);

        when(validator.validateStopPlaceUpdate(stopPlaceId, false)).thenReturn(existingStopPlace);
        doThrow(new IllegalArgumentException("Stop place name is required"))
            .when(validator)
            .validateStopPlaceName(updatedStopPlace);

        assertThrows(IllegalArgumentException.class, () ->
            domainService.updateStopPlace(updatedStopPlace)
        );

        verify(validator).validateStopPlaceUpdate(stopPlaceId, false);
        verify(validator).validateStopPlaceName(updatedStopPlace);
        verify(stopPlaceVersionedSaverService, never()).saveNewVersion(any(), any());
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
