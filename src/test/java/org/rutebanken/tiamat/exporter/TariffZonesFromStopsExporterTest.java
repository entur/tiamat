package org.rutebanken.tiamat.exporter;

import org.junit.Test;
import org.rutebanken.netex.model.SiteFrame;
import org.rutebanken.netex.model.StopPlace;
import org.rutebanken.netex.model.TariffZoneRef;
import org.rutebanken.netex.model.TariffZoneRefs_RelStructure;
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

}