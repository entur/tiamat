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

package org.rutebanken.tiamat.rest.netex.publicationdelivery;

import jakarta.xml.bind.JAXBElement;
import org.junit.Test;
import org.rutebanken.netex.model.FareFrame;
import org.rutebanken.netex.model.FareZone;
import org.rutebanken.netex.model.FareZonesInFrame_RelStructure;
import org.rutebanken.netex.model.MultilingualString;
import org.rutebanken.netex.model.ObjectFactory;
import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.rutebanken.netex.model.SiteFrame;
import org.rutebanken.netex.model.TariffZonesInFrame_RelStructure;
import org.rutebanken.netex.model.ValidBetween;
import org.rutebanken.netex.model.Zone_VersionStructure;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.importer.FareZoneFrameSource;
import org.rutebanken.tiamat.importer.ImportParams;
import org.rutebanken.tiamat.importer.ImportType;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for importing fare zones from FareFrame.
 */
public class FareZoneFareFrameImportTest extends TiamatIntegrationTest {

    @Autowired
    private PublicationDeliveryTestHelper publicationDeliveryTestHelper;

    @Test
    public void importSingleFareZoneFromFareFrame() throws Exception {
        // GIVEN: FareFrame with one FareZone
        LocalDateTime validFrom = LocalDateTime.now().minusDays(3);

        FareZone fareZone = new FareZone()
                .withName(new MultilingualString().withValue("Zone A"))
                .withVersion("1")
                .withValidBetween(new ValidBetween().withFromDate(validFrom))
                .withId("RUT:FareZone:A01");

        FareFrame fareFrame = publicationDeliveryTestHelper.fareFrame();
        fareFrame.setFareZones(new FareZonesInFrame_RelStructure());
        fareFrame.getFareZones().getFareZone().add(fareZone);

        PublicationDeliveryStructure publicationDelivery =
                publicationDeliveryTestHelper.publicationDelivery(fareFrame);

        // WHEN: Import with FARE_FRAME source
        ImportParams importParams = new ImportParams();
        importParams.fareZoneFrameSource = FareZoneFrameSource.FARE_FRAME;

        PublicationDeliveryStructure response =
                publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDelivery, importParams);

        // THEN: Response contains FareFrame with imported zone
        FareFrame responseFareFrame = publicationDeliveryTestHelper.findFareFrame(response);
        assertThat(responseFareFrame).isNotNull();
        assertThat(responseFareFrame.getFareZones()).isNotNull();
        assertThat(responseFareFrame.getFareZones().getFareZone()).hasSize(1);

        FareZone importedZone = responseFareFrame.getFareZones().getFareZone().get(0);
        assertThat(importedZone.getName().getValue()).isEqualTo("Zone A");
        assertThat(importedZone.getId()).startsWith("RUT:FareZone:");
    }

    @Test
    public void importMultipleFareZonesFromFareFrame() throws Exception {
        // GIVEN: FareFrame with multiple FareZones
        LocalDateTime validFrom = LocalDateTime.now().minusDays(3);

        FareZone fareZone1 = new FareZone()
                .withName(new MultilingualString().withValue("Zone A"))
                .withVersion("1")
                .withValidBetween(new ValidBetween().withFromDate(validFrom))
                .withId("RUT:FareZone:A01");

        FareZone fareZone2 = new FareZone()
                .withName(new MultilingualString().withValue("Zone B"))
                .withVersion("1")
                .withValidBetween(new ValidBetween().withFromDate(validFrom))
                .withId("RUT:FareZone:B02");

        FareZone fareZone3 = new FareZone()
                .withName(new MultilingualString().withValue("Zone C"))
                .withVersion("1")
                .withValidBetween(new ValidBetween().withFromDate(validFrom))
                .withId("RUT:FareZone:C03");

        FareFrame fareFrame = publicationDeliveryTestHelper.fareFrame();
        fareFrame.setFareZones(new FareZonesInFrame_RelStructure());
        fareFrame.getFareZones().getFareZone().add(fareZone1);
        fareFrame.getFareZones().getFareZone().add(fareZone2);
        fareFrame.getFareZones().getFareZone().add(fareZone3);

        PublicationDeliveryStructure publicationDelivery =
                publicationDeliveryTestHelper.publicationDelivery(fareFrame);

        // WHEN: Import with FARE_FRAME source
        ImportParams importParams = new ImportParams();
        importParams.fareZoneFrameSource = FareZoneFrameSource.FARE_FRAME;

        PublicationDeliveryStructure response =
                publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDelivery, importParams);

        // THEN: Response contains FareFrame with all imported zones
        FareFrame responseFareFrame = publicationDeliveryTestHelper.findFareFrame(response);
        assertThat(responseFareFrame).isNotNull();
        assertThat(responseFareFrame.getFareZones()).isNotNull();
        assertThat(responseFareFrame.getFareZones().getFareZone()).hasSize(3);

        List<String> zoneNames = responseFareFrame.getFareZones().getFareZone().stream()
                .map(zone -> zone.getName().getValue())
                .toList();
        assertThat(zoneNames).containsExactlyInAnyOrder("Zone A", "Zone B", "Zone C");
    }

    @Test
    public void importFareZoneFromBothFrames() throws Exception {
        // GIVEN: Both SiteFrame and FareFrame with different zones
        LocalDateTime validFrom = LocalDateTime.now().minusDays(3);

        List<JAXBElement<? extends Zone_VersionStructure>> tariffZones = new ArrayList<>();

        FareZone fareZone = new FareZone()
                .withName(new MultilingualString().withValue("V02"))
                .withVersion("1")
                .withValidBetween(new ValidBetween().withFromDate(validFrom))
                .withId("RUT:FareZone:05");

        tariffZones.add(new ObjectFactory().createFareZone(fareZone));

        SiteFrame siteFrame = publicationDeliveryTestHelper.siteFrame()
                .withTariffZones(new TariffZonesInFrame_RelStructure()
                        .withTariffZone(tariffZones));

        // FareFrame with fare zone
        FareZone fareFrameFareZone = new FareZone()
                .withName(new MultilingualString().withValue("Fare Zone 1"))
                .withVersion("1")
                .withValidBetween(new ValidBetween().withFromDate(validFrom))
                .withId("RUT:FareZone:Fare01");

        FareFrame fareFrame = publicationDeliveryTestHelper.fareFrame();
        fareFrame.setFareZones(new FareZonesInFrame_RelStructure());
        fareFrame.getFareZones().getFareZone().add(fareFrameFareZone);

        // Create publication delivery with both frames
        PublicationDeliveryStructure publicationDelivery = new PublicationDeliveryStructure()
                .withPublicationTimestamp(LocalDateTime.now())
                .withVersion("1")
                .withParticipantRef("test")
                .withDataObjects(new PublicationDeliveryStructure.DataObjects()
                        .withCompositeFrameOrCommonFrame(
                                new org.rutebanken.netex.model.ObjectFactory().createSiteFrame(siteFrame),
                                new org.rutebanken.netex.model.ObjectFactory().createFareFrame(fareFrame)
                        ));

        // WHEN: Import with BOTH mode
        ImportParams importParams = new ImportParams();
        importParams.fareZoneFrameSource = FareZoneFrameSource.BOTH;
        importParams.importType = ImportType.INITIAL;

        PublicationDeliveryStructure response =
                publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDelivery, importParams);

        // THEN: Both sets of zones are imported
        // Response should contain both SiteFrame and FareFrame
        SiteFrame responseSiteFrame = publicationDeliveryTestHelper.findSiteFrame(response);
        FareFrame responseFareFrame = publicationDeliveryTestHelper.findFareFrame(response);

        assertThat(responseSiteFrame).isNotNull();
        assertThat(responseFareFrame).isNotNull();

        // SiteFrame should have the site frame zone
        assertThat(responseSiteFrame.getTariffZones()).isNotNull();
        assertThat(responseSiteFrame.getTariffZones().getTariffZone()).hasSize(1);

        // FareFrame should have the fare frame zone
        assertThat(responseFareFrame.getFareZones()).isNotNull();
        assertThat(responseFareFrame.getFareZones().getFareZone()).hasSize(1);
        assertThat(responseFareFrame.getFareZones().getFareZone().get(0).getName().getValue())
                .isEqualTo("Fare Zone 1");
    }

    @Test
    public void importFareZoneWithVersionIncrement() throws Exception {
        // GIVEN: FareZone imported twice
        LocalDateTime validFrom = LocalDateTime.now().minusDays(3);

        FareZone fareZone = new FareZone()
                .withName(new MultilingualString().withValue("Zone Version Test"))
                .withVersion("1")
                .withValidBetween(new ValidBetween().withFromDate(validFrom))
                .withId("RUT:FareZone:Ver01");

        FareFrame fareFrame = publicationDeliveryTestHelper.fareFrame();
        fareFrame.setFareZones(new FareZonesInFrame_RelStructure());
        fareFrame.getFareZones().getFareZone().add(fareZone);

        PublicationDeliveryStructure publicationDelivery =
                publicationDeliveryTestHelper.publicationDelivery(fareFrame);

        ImportParams importParams = new ImportParams();
        importParams.fareZoneFrameSource = FareZoneFrameSource.FARE_FRAME;
        importParams.importType = ImportType.INITIAL;

        // WHEN: Import first time
        PublicationDeliveryStructure response1 =
                publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDelivery, importParams);

        FareFrame responseFareFrame1 = publicationDeliveryTestHelper.findFareFrame(response1);
        assertThat(responseFareFrame1.getFareZones().getFareZone()).hasSize(1);
        String firstVersion = responseFareFrame1.getFareZones().getFareZone().get(0).getVersion();

        // WHEN: Import second time (should create new version)
        PublicationDeliveryStructure response2 =
                publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDelivery, importParams);

        // THEN: Version should be incremented
        FareFrame responseFareFrame2 = publicationDeliveryTestHelper.findFareFrame(response2);
        assertThat(responseFareFrame2.getFareZones().getFareZone()).hasSize(1);
        String secondVersion = responseFareFrame2.getFareZones().getFareZone().get(0).getVersion();

        assertThat(Integer.parseInt(secondVersion)).isGreaterThan(Integer.parseInt(firstVersion));
    }

    @Test
    public void importFareFrameWithNoFareZones() throws Exception {
        // GIVEN: Empty FareFrame
        FareFrame fareFrame = publicationDeliveryTestHelper.fareFrame();
        // No fare zones set

        PublicationDeliveryStructure publicationDelivery =
                publicationDeliveryTestHelper.publicationDelivery(fareFrame);

        // WHEN: Import with FARE_FRAME source
        ImportParams importParams = new ImportParams();
        importParams.fareZoneFrameSource = FareZoneFrameSource.FARE_FRAME;

        PublicationDeliveryStructure response =
                publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDelivery, importParams);

        // THEN: Should handle gracefully with empty response
        assertThat(response).isNotNull();
        FareFrame responseFareFrame = publicationDeliveryTestHelper.findFareFrame(response);
        if (responseFareFrame != null && responseFareFrame.getFareZones() != null) {
            assertThat(responseFareFrame.getFareZones().getFareZone()).isEmpty();
        }
    }

    @Test
    public void fareFrameResponseHasCorrectStructure() throws Exception {
        // GIVEN: FareFrame with fare zone
        FareZone fareZone = new FareZone()
                .withName(new MultilingualString().withValue("Structure Test Zone"))
                .withVersion("1")
                .withId("RUT:FareZone:Struct01");

        FareFrame fareFrame = publicationDeliveryTestHelper.fareFrame();
        fareFrame.setFareZones(new FareZonesInFrame_RelStructure());
        fareFrame.getFareZones().getFareZone().add(fareZone);

        PublicationDeliveryStructure publicationDelivery =
                publicationDeliveryTestHelper.publicationDelivery(fareFrame);

        // WHEN: Import with FARE_FRAME source
        ImportParams importParams = new ImportParams();
        importParams.fareZoneFrameSource = FareZoneFrameSource.FARE_FRAME;

        PublicationDeliveryStructure response =
                publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDelivery, importParams);

        // THEN: Response structure is correct
        assertThat(response).isNotNull();
        assertThat(response.getDataObjects()).isNotNull();
        assertThat(response.getDataObjects().getCompositeFrameOrCommonFrame()).isNotEmpty();

        FareFrame responseFareFrame = publicationDeliveryTestHelper.findFareFrame(response);
        assertThat(responseFareFrame).isNotNull();
        assertThat(responseFareFrame.getId()).contains("-response");
        assertThat(responseFareFrame.getVersion()).isEqualTo("1");
    }
}