package org.rutebanken.tiamat.rest.netex.publicationdelivery;

import net.opengis.gml._3.DirectPositionListType;
import net.opengis.gml._3.LineStringType;
import org.junit.Test;
import org.rutebanken.netex.model.*;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.config.GeometryFactoryConfig;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TariffZoneImportTest extends TiamatIntegrationTest {

    @Autowired
    private PublicationDeliveryTestHelper publicationDeliveryTestHelper;

    @Test
    public void publicationDeliveryWithTariffZone() throws Exception {



        TariffZone tariffZone = new TariffZone()
                .withName(new MultilingualString().withValue("V02"))
                .withVersion("1")
                .withId("RUT:TariffZone:01");



        SiteFrame siteFrame = publicationDeliveryTestHelper.siteFrame()
                .withTariffZones(new TariffZonesInFrame_RelStructure()
                .withTariffZone(tariffZone));

        PublicationDeliveryStructure publicationDeliveryStructure = publicationDeliveryTestHelper.publicationDelivery(siteFrame);

        PublicationDeliveryStructure response = publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDeliveryStructure);

        List<TariffZone> actualZones = publicationDeliveryTestHelper.findSiteFrame(response)
                .getTariffZones().getTariffZone();

        assertThat(actualZones).isNotEmpty();
        assertThat(actualZones.get(0).getName().getValue()).isEqualTo(tariffZone.getName().getValue());

    }
}
