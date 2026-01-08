package org.rutebanken.tiamat.rest.write;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.rutebanken.tiamat.model.ModificationEnumeration;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.service.stopplace.StopPlaceTerminator;
import org.rutebanken.tiamat.versioning.save.StopPlaceVersionedSaverService;

class StopPlaceServiceTest {

    private StopPlaceVersionedSaverService saverService;
    private StopPlaceTerminator terminator;
    private StopPlaceService service;

    @BeforeEach
    void setup() {
        saverService = mock(StopPlaceVersionedSaverService.class);
        terminator = mock(StopPlaceTerminator.class);
        service = new StopPlaceService(saverService, terminator);
    }

    @Test
    void testCreateStopPlace() {
        StopPlace stopPlace = new StopPlace();
        StopPlace saved = new StopPlace();

        when(saverService.saveNewVersion(stopPlace)).thenReturn(saved);

        StopPlace returned = service.createStopPlace(stopPlace);

        assertEquals(saved, returned);
        verify(saverService).saveNewVersion(stopPlace);
        verifyNoMoreInteractions(saverService);
        verifyNoInteractions(terminator);
    }

    @Test
    void testUpdateStopPlace() {
        StopPlace existing = new StopPlace();
        StopPlace updated = new StopPlace();
        StopPlace saved = new StopPlace();

        when(saverService.saveNewVersion(existing, updated)).thenReturn(saved);

        StopPlace result = service.updateStopPlace(existing, updated);

        assertEquals(saved, result);
        verify(saverService).saveNewVersion(existing, updated);
        verifyNoMoreInteractions(saverService);
        verifyNoInteractions(terminator);
    }

    @Test
    void testDeleteStopPlace() {
        String stopPlaceId = "NSR:StopPlace:100";

        service.deleteStopPlace(stopPlaceId);

        ArgumentCaptor<String> idCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Instant> timeCaptor = ArgumentCaptor.forClass(
            Instant.class
        );
        ArgumentCaptor<String> descCaptor = ArgumentCaptor.forClass(
            String.class
        );
        ArgumentCaptor<ModificationEnumeration> modCaptor =
            ArgumentCaptor.forClass(ModificationEnumeration.class);

        verify(terminator).terminateStopPlace(
            idCaptor.capture(),
            timeCaptor.capture(),
            descCaptor.capture(),
            modCaptor.capture()
        );

        assertEquals(stopPlaceId, idCaptor.getValue());
        assertEquals("Deleted via write API", descCaptor.getValue());
        assertEquals(ModificationEnumeration.DELETE, modCaptor.getValue());

        assertTrue(timeCaptor.getValue().isAfter(Instant.now()));

        verifyNoMoreInteractions(terminator);
        verifyNoInteractions(saverService);
    }
}
