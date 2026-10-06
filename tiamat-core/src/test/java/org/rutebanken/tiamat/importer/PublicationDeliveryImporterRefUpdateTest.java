package org.rutebanken.tiamat.importer;

import org.junit.Test;
import org.rutebanken.netex.model.FareFrame;
import org.rutebanken.netex.model.LocaleStructure;
import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.rutebanken.netex.model.SiteFrame;
import org.rutebanken.netex.model.VersionFrameDefaultsStructure;
import org.rutebanken.tiamat.auth.AuthorizationService;
import org.rutebanken.tiamat.config.FareZoneConfig;
import org.rutebanken.tiamat.exporter.PublicationDeliveryCreator;
import org.rutebanken.tiamat.importer.handler.GroupOfTariffZonesImportHandler;
import org.rutebanken.tiamat.importer.handler.ParkingsImportHandler;
import org.rutebanken.tiamat.importer.handler.PathLinkImportHandler;
import org.rutebanken.tiamat.importer.handler.StopPlaceImportHandler;
import org.rutebanken.tiamat.importer.handler.TariffZoneImportHandler;
import org.rutebanken.tiamat.importer.handler.TopographicPlaceImportHandler;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.netex.mapping.PublicationDeliveryHelper;
import org.rutebanken.tiamat.service.batch.BackgroundJobs;
import org.rutebanken.tiamat.versioning.save.FareZoneSaverService;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** The stop place ref rebuild is a full job, so it runs only after a FareFrame import saved or deleted zones. */
public class PublicationDeliveryImporterRefUpdateTest {

    private final PublicationDeliveryHelper helper = mock(PublicationDeliveryHelper.class);
    private final TariffZoneImportHandler tariffZoneImportHandler = mock(TariffZoneImportHandler.class);
    private final BackgroundJobs backgroundJobs = mock(BackgroundJobs.class);
    private final FareZoneSaverService saver = mock(FareZoneSaverService.class);
    private final FareZoneConfig fareZoneConfig = new FareZoneConfig();
    private final PublicationDeliveryStructure delivery = new PublicationDeliveryStructure()
            .withDataObjects(new PublicationDeliveryStructure.DataObjects());

    private final PublicationDeliveryImporter importer = new PublicationDeliveryImporter(
            helper, mock(NetexMapper.class), mock(PublicationDeliveryCreator.class),
            mock(PathLinkImportHandler.class), mock(TopographicPlaceImportHandler.class), tariffZoneImportHandler,
            mock(GroupOfTariffZonesImportHandler.class), mock(StopPlaceImportHandler.class),
            mock(ParkingsImportHandler.class), backgroundJobs, mock(AuthorizationService.class), fareZoneConfig,
            saver, mock(PlatformTransactionManager.class), false);

    @Test
    public void savedZoneTriggersRefUpdate() {
        givenFareZoneResult(Set.of("NSR:FareZone:1"));

        importer.importPublicationDelivery(delivery, new ImportParams());

        verify(backgroundJobs).triggerStopPlaceUpdate();
    }

    @Test
    public void rejectedZoneDoesNotTrigger() {
        givenFareZoneResult(Set.of());

        importer.importPublicationDelivery(delivery, new ImportParams());

        verify(backgroundJobs, never()).triggerStopPlaceUpdate();
    }

    @Test
    public void replicaCleanupThatDeletesNothingDoesNotTrigger() {
        givenFareZoneResult(Set.of());
        ReflectionTestUtils.setField(fareZoneConfig, "externalVersioning", true);

        importer.importPublicationDelivery(delivery, new ImportParams());

        verify(saver).deleteAllExcept(Set.of("NSR:FareZone:1"));
        verify(backgroundJobs, never()).triggerStopPlaceUpdate();
    }

    @Test
    public void replicaCleanupThatDeletesTriggersRefUpdate() {
        givenFareZoneResult(Set.of());
        when(saver.deleteAllExcept(Set.of("NSR:FareZone:1"))).thenReturn(1);
        ReflectionTestUtils.setField(fareZoneConfig, "externalVersioning", true);

        importer.importPublicationDelivery(delivery, new ImportParams());

        verify(backgroundJobs).triggerStopPlaceUpdate();
    }

    private void givenFareZoneResult(Set<String> savedNetexIds) {
        VersionFrameDefaultsStructure frameDefaults = new VersionFrameDefaultsStructure()
                .withDefaultLocale(new LocaleStructure().withTimeZone("Europe/Oslo"));
        when(helper.findSiteFrame(delivery)).thenReturn(new SiteFrame().withId("NSR:SiteFrame:1").withFrameDefaults(frameDefaults));
        when(helper.findFareFrame(delivery)).thenReturn(new FareFrame().withId("NSR:FareFrame:1").withFrameDefaults(frameDefaults));
        when(tariffZoneImportHandler.handleFareZonesFromFareFrame(any(), any(), any(), any())).thenReturn(
                new FareZoneImportResult(List.of(), savedNetexIds, Set.of("NSR:FareZone:1")));
    }
}
