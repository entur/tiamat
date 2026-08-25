package org.rutebanken.tiamat.writer;

import ma.glasnost.orika.MapperFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rutebanken.netex.model.MultilingualString;
import org.rutebanken.tiamat.lock.MutateLock;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.StopTypeEnumeration;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.netex.mapping.NetexMappingContext;
import org.rutebanken.tiamat.netex.mapping.NetexMappingContextThreadLocal;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.rest.validation.StopPlaceMutationValidator;
import org.rutebanken.tiamat.writer.mapper.CreateStopPlaceMapper;
import org.rutebanken.tiamat.service.stopplace.StopPlaceTerminator;
import org.rutebanken.tiamat.versioning.VersionCreator;
import org.rutebanken.tiamat.versioning.save.StopPlaceVersionedSaverService;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StopPlaceWriterTest {

    @Mock
    private StopPlaceMutationValidator validator;

    @Mock
    private StopPlaceVersionedSaverService stopPlaceVersionedSaverService;

    @Mock
    private StopPlaceTerminator stopPlaceTerminator;

    @Mock
    private StopPlaceRepository stopPlaceRepository;

    private StopPlaceWriter domainService;

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
    private SubmittedStopPlaceUpdater submittedStopPlaceUpdater;

    @Mock
    private CreateStopPlaceMapper createStopPlaceMapper;

    @BeforeEach
    void setup() {
        lenient().when(netexMapper.getFacade()).thenReturn(mapperFacade);
        domainService = new StopPlaceWriter(
            validator,
            stopPlaceVersionedSaverService,
            stopPlaceTerminator,
            stopPlaceRepository,
            mutateLock,
            versionCreator,
            netexMapper,
            createStopPlaceMapper,
            submittedStopPlaceUpdater
        );
    }

    @Test
    void updateStopPlace_UpdatesSuccessfully() {
        String stopPlaceId = "NSR:StopPlace:100";
        StopPlace existingStopPlace = createTiamatStopPlace(stopPlaceId, "Old Name", 1L);
        StopPlace updatedTiamatStopPlace = createTiamatStopPlace(stopPlaceId, "New Name", 1L);
        org.rutebanken.netex.model.StopPlace updatedNetexStopPlace = createNetexStopPlace(stopPlaceId, "New Name", 1L);
        StopPlace savedStopPlace = createTiamatStopPlace(stopPlaceId, "New Name", 2L);

        when(validator.validateStopPlaceUpdate(stopPlaceId, false)).thenReturn(existingStopPlace);
        when(versionCreator.createCopy(existingStopPlace, StopPlace.class)).thenReturn(updatedTiamatStopPlace);
        when(stopPlaceVersionedSaverService.saveNewVersion(eq(existingStopPlace), eq(updatedTiamatStopPlace), anySet()))
            .thenReturn(savedStopPlace);

        StopPlace result = domainService.updateStopPlace(updatedNetexStopPlace);

        assertThat(result).isSameAs(savedStopPlace);
        verify(validator).validateStopPlaceUpdate(stopPlaceId, false);
        verify(validator).validateStopPlaceMutation(updatedTiamatStopPlace);
        verify(stopPlaceVersionedSaverService).saveNewVersion(eq(existingStopPlace), eq(updatedTiamatStopPlace), anySet());
    }

    @Test
    void createStopPlace_Success() {
        StopPlace newTiamatStopPlace = createTiamatStopPlace(null, "New Stop", 1L);
        org.rutebanken.netex.model.StopPlace newNetexStopPlace = createNetexStopPlace(null, "New Stop", 1L);
        StopPlace savedStopPlace = createTiamatStopPlace("NSR:StopPlace:200", "New Stop", 1L);

        StopPlace mappedStopPlace = createTiamatStopPlace(null, "New Stop", 1L);
        when(netexMapper.mapToTiamatModel(newNetexStopPlace)).thenReturn(mappedStopPlace);
        when(createStopPlaceMapper.createCopy(eq(mappedStopPlace), eq(StopPlace.class))).thenReturn(newTiamatStopPlace);
        when(stopPlaceVersionedSaverService.saveNewVersion(newTiamatStopPlace)).thenReturn(savedStopPlace);

        StopPlace result = domainService.createStopPlace(newNetexStopPlace);

        assertThat(result).isSameAs(savedStopPlace);
        verify(validator).validateStopPlaceMutation(newTiamatStopPlace);
        verify(stopPlaceVersionedSaverService).saveNewVersion(newTiamatStopPlace);
    }

    @Test
    void deleteStopPlace_Success() {
        String stopPlaceId = "NSR:StopPlace:300";

        domainService.deleteStopPlace(stopPlaceId);

        verify(stopPlaceTerminator).terminateStopPlace(
            eq(stopPlaceId),
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
            .validateStopPlaceMutation(updatedTiamatStopPlace);

        assertThrows(IllegalArgumentException.class, () ->
            domainService.updateStopPlace(updatedNetexStopPlace)
        );

        verify(validator).validateStopPlaceUpdate(stopPlaceId, false);
        verify(validator).validateStopPlaceMutation(updatedTiamatStopPlace);
        verify(stopPlaceVersionedSaverService, never()).saveNewVersion(any(), any());
    }

    /**
     * The mapping runs on the async worker thread, not the HTTP request thread, so the
     * processing unit must establish its own NetexMappingContext. Otherwise
     * ValidBetweenConverter dereferences a null context and every payload containing
     * ValidBetween fails.
     */
    @Test
    void createStopPlace_EstablishesNetexMappingContextBeforeMapping() {
        NetexMappingContextThreadLocal.set(null);

        org.rutebanken.netex.model.StopPlace newNetexStopPlace = createNetexStopPlace(null, "New Stop", 1L);
        StopPlace mappedStopPlace = createTiamatStopPlace(null, "New Stop", 1L);
        StopPlace cleanStopPlace = createTiamatStopPlace(null, "New Stop", 1L);
        StopPlace savedStopPlace = createTiamatStopPlace("NSR:StopPlace:200", "New Stop", 1L);

        AtomicReference<NetexMappingContext> contextDuringMapping = new AtomicReference<>();
        when(netexMapper.mapToTiamatModel(newNetexStopPlace)).thenAnswer(invocation -> {
            contextDuringMapping.set(NetexMappingContextThreadLocal.get());
            return mappedStopPlace;
        });
        when(createStopPlaceMapper.createCopy(eq(mappedStopPlace), eq(StopPlace.class))).thenReturn(cleanStopPlace);
        when(stopPlaceVersionedSaverService.saveNewVersion(cleanStopPlace)).thenReturn(savedStopPlace);

        domainService.createStopPlace(newNetexStopPlace);

        assertNotNull(
            contextDuringMapping.get(),
            "NetexMappingContext must be set on the thread performing the mapping"
        );
        assertNotNull(contextDuringMapping.get().defaultTimeZone);
    }

    @Test
    void updateStopPlace_EstablishesNetexMappingContextBeforeMapping() {
        NetexMappingContextThreadLocal.set(null);

        String stopPlaceId = "NSR:StopPlace:100";
        StopPlace existingStopPlace = createTiamatStopPlace(stopPlaceId, "Old Name", 1L);
        StopPlace updatedTiamatStopPlace = createTiamatStopPlace(stopPlaceId, "New Name", 1L);
        org.rutebanken.netex.model.StopPlace updatedNetexStopPlace = createNetexStopPlace(stopPlaceId, "New Name", 1L);
        StopPlace savedStopPlace = createTiamatStopPlace(stopPlaceId, "New Name", 2L);

        AtomicReference<NetexMappingContext> contextDuringMapping = new AtomicReference<>();
        when(validator.validateStopPlaceUpdate(stopPlaceId, false)).thenReturn(existingStopPlace);
        when(netexMapper.mapToTiamatModel(updatedNetexStopPlace)).thenAnswer(invocation -> {
            contextDuringMapping.set(NetexMappingContextThreadLocal.get());
            return updatedTiamatStopPlace;
        });
        when(versionCreator.createCopy(existingStopPlace, StopPlace.class)).thenReturn(updatedTiamatStopPlace);
        when(stopPlaceVersionedSaverService.saveNewVersion(eq(existingStopPlace), eq(updatedTiamatStopPlace), anySet()))
            .thenReturn(savedStopPlace);

        domainService.updateStopPlace(updatedNetexStopPlace);

        assertNotNull(
            contextDuringMapping.get(),
            "NetexMappingContext must be set on the thread performing the mapping"
        );
        assertNotNull(contextDuringMapping.get().defaultTimeZone);
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
