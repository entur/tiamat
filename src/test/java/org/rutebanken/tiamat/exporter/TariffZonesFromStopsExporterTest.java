package org.rutebanken.tiamat.exporter;

import org.junit.Test;
import org.rutebanken.netex.model.*;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.model.TariffZone;
import org.rutebanken.tiamat.repository.TariffZoneRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class TariffZonesFromStopsExporterTest extends TiamatIntegrationTest {

    @Autowired
    private TariffZoneRepository tariffZoneRepository;

    @Autowired
    private TariffZonesFromStopsExporter tariffZonesFromStopsExporter;

    @Test
    public void avoidDuplicateTariffZones() {

        TariffZone tariffZone = new TariffZone();
        tariffZone.setNetexId("VKT:TariffZone:201");
        tariffZone.setVersion(1L);
        tariffZoneRepository.save(tariffZone);

        // Two stops with reference to the same tariffzone
        StopPlace netexStopPlace = new StopPlace();
        netexStopPlace.setId("NSR:StopPlace:1");
        netexStopPlace.withTariffZones(new TariffZoneRefs_RelStructure().withTariffZoneRef(new TariffZoneRef().withRef(tariffZone.getNetexId()).withVersion("1")));

        StopPlace netexStopPlace2 = new StopPlace();
        netexStopPlace2.setId("NSR:StopPlace:2");
        netexStopPlace2.withTariffZones(new TariffZoneRefs_RelStructure().withTariffZoneRef(new TariffZoneRef().withRef(tariffZone.getNetexId()).withVersion("1")));

        SiteFrame siteFrame = new SiteFrame();
        tariffZonesFromStopsExporter.resolveTariffZones(Arrays.asList(netexStopPlace, netexStopPlace2), siteFrame);

        assertThat(siteFrame.getTariffZones().getTariffZone()).as("Number of tariffzones returned").hasSize(1);

    }

    @Test
    public void handleUnresolvableTariffZoneRef() {

        TariffZone tariffZone = new TariffZone();
        tariffZone.setNetexId("VKT:TariffZone:201");
        tariffZone.setVersion(1L);
        tariffZoneRepository.save(tariffZone);

        StopPlace netexStopPlace = new StopPlace();
        netexStopPlace.setId("NSR:StopPlace:1");
        netexStopPlace.withTariffZones(new TariffZoneRefs_RelStructure().withTariffZoneRef(new TariffZoneRef().withRef("NSR:TariffZone:1")));

        SiteFrame siteFrame = new SiteFrame();
        tariffZonesFromStopsExporter.resolveTariffZones(Arrays.asList(netexStopPlace), siteFrame);

        assertThat(siteFrame.getTariffZones().getTariffZone()).as("Number of tariffzones returned").hasSize(0);

    }

    @Test
    public void keepExistingTariffZones() {

        TariffZone tariffZone = new TariffZone();
        tariffZone.setNetexId("VKT:TariffZone:201");
        tariffZone.setVersion(1L);
        tariffZoneRepository.save(tariffZone);

        org.rutebanken.netex.model.TariffZone alreadyAddedTariffZone = new org.rutebanken.netex.model.TariffZone()
                .withId("VKT:TariffZone:123")
                .withVersion("2");

        StopPlace netexStopPlace = new StopPlace();
        netexStopPlace.setId("NSR:StopPlace:1");
        netexStopPlace.withTariffZones(new TariffZoneRefs_RelStructure().withTariffZoneRef(new TariffZoneRef().withRef(tariffZone.getNetexId()).withVersion("1")));

        SiteFrame siteFrame = new SiteFrame();
        siteFrame.withTariffZones(new TariffZonesInFrame_RelStructure().withTariffZone(alreadyAddedTariffZone));
        tariffZonesFromStopsExporter.resolveTariffZones(Arrays.asList(netexStopPlace), siteFrame);

        assertThat(siteFrame.getTariffZones().getTariffZone()).as("Number of tariffzones returned").hasSize(2);

    }

}