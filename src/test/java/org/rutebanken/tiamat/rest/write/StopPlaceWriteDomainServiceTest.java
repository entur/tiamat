package org.rutebanken.tiamat.rest.write;

import ma.glasnost.orika.MapperFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rutebanken.netex.model.MultilingualString;
import org.rutebanken.tiamat.diff.TiamatObjectDiffer;
import org.rutebanken.tiamat.diff.generic.GenericObjectDiffer;
import org.rutebanken.tiamat.lock.MutateLock;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.StopTypeEnumeration;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.rest.validation.StopPlaceMutationValidator;
import org.rutebanken.tiamat.service.stopplace.StopPlaceTerminator;
import org.rutebanken.tiamat.versioning.VersionCreator;
import org.rutebanken.tiamat.versioning.save.StopPlaceVersionedSaverService;

import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
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

    @Mock
    private VersionCreator versionCreator;

    @Mock
    private NetexMapper netexMapper;

    @Mock
    private MapperFacade mapperFacade;

    @Mock
    private NetexIdMapper netexIdMapper;

    @BeforeEach
    void setup() {
        lenient().when(netexMapper.getFacade()).thenReturn(mapperFacade);
        TiamatObjectDiffer differ = new TiamatObjectDiffer(new GenericObjectDiffer());
        domainService = new StopPlaceWriteDomainService(
            validator,
            stopPlaceVersionedSaverService,
            stopPlaceTerminator,
            stopPlaceRepository,
            differ,
            mutateLock,
            versionCreator,
            netexMapper,
            netexIdMapper
        );
    }

    @Test
    void updateStopPlace_NoDifferencesDetected_ThrowsException() {
        String stopPlaceId = "NSR:StopPlace:100";
        StopPlace existingStopPlace = createTiamatStopPlace(stopPlaceId, "Test Stop", 1L);
        StopPlace updatedTiamatStopPlace = createTiamatStopPlace(stopPlaceId, "Test Stop", 1L);
        org.rutebanken.netex.model.StopPlace updatedNetexStopPlace = createNetexStopPlace(stopPlaceId, "Test Stop", 1L);

        when(validator.validateStopPlaceUpdate(stopPlaceId, false)).thenReturn(existingStopPlace);
        when(versionCreator.createCopy(existingStopPlace, StopPlace.class)).thenReturn(updatedTiamatStopPlace);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> domainService.updateStopPlace(updatedNetexStopPlace)
        );

        assertEquals(
            "No changes detected for StopPlace with id NSR:StopPlace:100",
            exception.getMessage()
        );

        verify(validator).validateStopPlaceUpdate(stopPlaceId, false);
        verify(validator).validateStopPlaceName(updatedTiamatStopPlace);
        verify(stopPlaceVersionedSaverService, never()).saveNewVersion(any(), any());
    }

    @Test
    void updateStopPlace_DifferencesDetected_UpdatesSuccessfully() throws Exception {
        String stopPlaceId = "NSR:StopPlace:100";
        StopPlace existingStopPlace = createTiamatStopPlace(stopPlaceId, "Old Name", 1L);
        StopPlace updatedTiamatStopPlace = createTiamatStopPlace(stopPlaceId, "New Name", 1L);
        org.rutebanken.netex.model.StopPlace updatedNetexStopPlace = createNetexStopPlace(stopPlaceId, "New Name", 1L);
        StopPlace savedStopPlace = createTiamatStopPlace(stopPlaceId, "New Name", 2L);

        when(validator.validateStopPlaceUpdate(stopPlaceId, false)).thenReturn(existingStopPlace);
        when(versionCreator.createCopy(existingStopPlace, StopPlace.class)).thenReturn(updatedTiamatStopPlace);
        when(stopPlaceVersionedSaverService.saveNewVersion(eq(existingStopPlace), eq(updatedTiamatStopPlace), any(java.util.Set.class)))
            .thenReturn(savedStopPlace);

        StopPlace result = domainService.updateStopPlace(updatedNetexStopPlace);

        assertNotNull(result);
        assertEquals("New Name", result.getName().getValue());
        verify(validator).validateStopPlaceUpdate(stopPlaceId, false);
        verify(validator).validateStopPlaceName(updatedTiamatStopPlace);
        verify(stopPlaceVersionedSaverService).saveNewVersion(eq(existingStopPlace), eq(updatedTiamatStopPlace), any(java.util.Set.class));
    }

    @Test
    void createStopPlace_Success() {
        StopPlace newTiamatStopPlace = createTiamatStopPlace(null, "New Stop", 1L);
        org.rutebanken.netex.model.StopPlace newNetexStopPlace = createNetexStopPlace(null, "New Stop", 1L);
        StopPlace savedStopPlace = createTiamatStopPlace("NSR:StopPlace:200", "New Stop", 1L);

        when(versionCreator.createCopy(any(StopPlace.class), eq(StopPlace.class))).thenReturn(newTiamatStopPlace);
        when(stopPlaceVersionedSaverService.saveNewVersion(newTiamatStopPlace)).thenReturn(savedStopPlace);

        StopPlace result = domainService.createStopPlace(newNetexStopPlace);

        assertNotNull(result);
        assertEquals("NSR:StopPlace:200", result.getNetexId());
        assertEquals("New Stop", result.getName().getValue());
        verify(validator).validateStopPlaceName(newTiamatStopPlace);
        verify(stopPlaceVersionedSaverService).saveNewVersion(newTiamatStopPlace);
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
        org.rutebanken.netex.model.StopPlace updatedStopPlace = createNetexStopPlace(stopPlaceId, "Invalid Stop", 1L);

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
        StopPlace existingStopPlace = createTiamatStopPlace(stopPlaceId, "Old Name", 1L);
        StopPlace updatedTiamatStopPlace = createTiamatStopPlace(stopPlaceId, "", 1L);
        org.rutebanken.netex.model.StopPlace updatedNetexStopPlace = createNetexStopPlace(stopPlaceId, "", 1L);

        when(validator.validateStopPlaceUpdate(stopPlaceId, false)).thenReturn(existingStopPlace);
        when(versionCreator.createCopy(existingStopPlace, StopPlace.class)).thenReturn(updatedTiamatStopPlace);
        doThrow(new IllegalArgumentException("Stop place name is required"))
            .when(validator)
            .validateStopPlaceName(updatedTiamatStopPlace);

        assertThrows(IllegalArgumentException.class, () ->
            domainService.updateStopPlace(updatedNetexStopPlace)
        );

        verify(validator).validateStopPlaceUpdate(stopPlaceId, false);
        verify(validator).validateStopPlaceName(updatedTiamatStopPlace);
        verify(stopPlaceVersionedSaverService, never()).saveNewVersion(any(), any());
    }

    private StopPlace createTiamatStopPlace(String netexId, String name, Long version) {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setNetexId(netexId);
        stopPlace.setStopPlaceType(StopTypeEnumeration.BUS_STATION);

        EmbeddableMultilingualString multilingualName = new EmbeddableMultilingualString();
        multilingualName.setValue(name);
        stopPlace.setName(multilingualName);

        stopPlace.setVersion(version);

        return stopPlace;
    }

    private org.rutebanken.netex.model.StopPlace createNetexStopPlace(String netexId, String name, Long version) {
        org.rutebanken.netex.model.StopPlace stopPlace = new org.rutebanken.netex.model.StopPlace();
        stopPlace.setId(netexId);
        stopPlace.setName(
                new MultilingualString().withValue(name)
        );
        stopPlace.setVersion(version.toString());
        return stopPlace;
    }
}
