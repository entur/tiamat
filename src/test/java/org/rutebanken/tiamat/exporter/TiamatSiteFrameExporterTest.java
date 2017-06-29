package org.rutebanken.tiamat.exporter;

import org.junit.Test;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.TariffZone;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

public class TiamatSiteFrameExporterTest extends TiamatIntegrationTest {

    @Autowired
    private TiamatSiteFrameExporter publicationDeliveryExporter;

    @Test
    public void exportTariffZonesInSiteFrame() {
        org.rutebanken.tiamat.model.SiteFrame siteFrame = new org.rutebanken.tiamat.model.SiteFrame();

        TariffZone tariffZone = new TariffZone();
        tariffZone.setName(new EmbeddableMultilingualString("name"));

        tariffZoneRepository.save(tariffZone);

        publicationDeliveryExporter.addAllTariffZones(siteFrame);

        assertThat(siteFrame.getTariffZones()).isNotNull();
        assertThat(siteFrame.getTariffZones().getTariffZone()).hasSize(1);
    }


}