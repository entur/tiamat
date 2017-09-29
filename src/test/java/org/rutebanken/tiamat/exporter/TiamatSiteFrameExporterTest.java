/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

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