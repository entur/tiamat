package org.rutebanken.tiamat.importer;

import org.junit.Test;
import org.rutebanken.netex.model.FareFrame;
import org.rutebanken.netex.model.LocaleStructure;
import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.rutebanken.netex.model.VersionFrameDefaultsStructure;
import org.rutebanken.tiamat.config.FareZoneConfig;
import org.rutebanken.tiamat.exporter.PublicationDeliveryCreator;
import org.rutebanken.tiamat.importer.handler.TariffZoneImportHandler;
import org.rutebanken.tiamat.netex.mapping.PublicationDeliveryHelper;
import org.rutebanken.tiamat.service.batch.BackgroundJobs;
import org.rutebanken.tiamat.versioning.save.FareZoneSaverService;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PublicationDeliveryFareFrameImporterTest {

    private final PublicationDeliveryHelper helper = mock(PublicationDeliveryHelper.class);
    private final TariffZoneImportHandler handler = mock(TariffZoneImportHandler.class);
    private final BackgroundJobs backgroundJobs = mock(BackgroundJobs.class);
    private final FareZoneSaverService saver = mock(FareZoneSaverService.class);
    private final PublicationDeliveryStructure delivery = new PublicationDeliveryStructure()
            .withDataObjects(new PublicationDeliveryStructure.DataObjects());

    private final FareZoneConfig fareZoneConfig = new FareZoneConfig();
    private final PublicationDeliveryFareFrameImporter importer = new PublicationDeliveryFareFrameImporter(
            helper, mock(PublicationDeliveryCreator.class), handler, backgroundJobs, fareZoneConfig, saver);

    /** Every declared zone rejected: nothing saved, but pruning orphans still changes stop place refs. */
    @Test
    public void replicaCleanupAloneTriggersRefUpdate() {
        givenOnlyRejectedZone();
        when(saver.deleteAllExcept(Set.of("NSR:FareZone:1"), false)).thenReturn(2);
        ReflectionTestUtils.setField(fareZoneConfig, "externalVersioning", true);

        importer.importPublicationDelivery(delivery, new ImportParams());

        verify(saver).deleteAllExcept(Set.of("NSR:FareZone:1"), false);
        verify(backgroundJobs).triggerStopPlaceUpdate();
    }

    @Test
    public void registerReplicaPrunesWithoutExternalVersioning() {
        givenOnlyRejectedZone();
        when(saver.deleteAllExcept(Set.of("NSR:FareZone:1"), true)).thenReturn(1);
        ImportParams params = new ImportParams();
        params.fareZoneRegisterReplica = true;

        importer.importPublicationDelivery(delivery, params);

        verify(saver).deleteAllExcept(Set.of("NSR:FareZone:1"), true);
        verify(backgroundJobs).triggerStopPlaceUpdate();
    }

    @Test
    public void replicaCleanupThatDeletesNothingDoesNotTrigger() {
        givenOnlyRejectedZone();
        ReflectionTestUtils.setField(fareZoneConfig, "externalVersioning", true);

        importer.importPublicationDelivery(delivery, new ImportParams());

        verify(saver).deleteAllExcept(Set.of("NSR:FareZone:1"), false);
        verify(backgroundJobs, never()).triggerStopPlaceUpdate();
    }

    @Test
    public void nonReplicaImportNeitherPrunesNorTriggers() {
        givenOnlyRejectedZone();

        importer.importPublicationDelivery(delivery, new ImportParams());

        verify(saver, never()).deleteAllExcept(any(), anyBoolean());
        verify(backgroundJobs, never()).triggerStopPlaceUpdate();
    }

    private void givenOnlyRejectedZone() {
        FareFrame fareFrame = new FareFrame().withId("NSR:FareFrame:1").withFrameDefaults(
                new VersionFrameDefaultsStructure().withDefaultLocale(new LocaleStructure().withTimeZone("Europe/Oslo")));
        when(helper.findFareFrame(delivery)).thenReturn(fareFrame);
        when(handler.handleFareZonesFromFareFrame(any(), any(), any(), any())).thenReturn(
                new FareZoneImportResult(List.of(), Set.of(), Set.of("NSR:FareZone:1")));
    }
}
