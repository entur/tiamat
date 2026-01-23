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
import org.rutebanken.netex.model.LocationStructure;
import org.rutebanken.netex.model.MultilingualString;
import org.rutebanken.netex.model.ObjectFactory;
import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.rutebanken.netex.model.Quay;
import org.rutebanken.netex.model.Quays_RelStructure;
import org.rutebanken.netex.model.SimplePoint_VersionStructure;
import org.rutebanken.netex.model.SiteFrame;
import org.rutebanken.netex.model.StopPlace;
import org.rutebanken.netex.model.StopPlacesInFrame_RelStructure;
import org.rutebanken.netex.model.StopTypeEnumeration;
import org.rutebanken.netex.model.TariffZoneRef;
import org.rutebanken.netex.model.TariffZoneRefs_RelStructure;
import org.rutebanken.netex.model.TariffZonesInFrame_RelStructure;
import org.rutebanken.netex.model.ValidBetween;
import org.rutebanken.netex.model.Zone_VersionStructure;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.importer.ImportParams;
import org.rutebanken.tiamat.importer.ImportType;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class FareZoneImportTest extends TiamatIntegrationTest {

    private static final ObjectFactory netexObjectFactory = new ObjectFactory();

    @Autowired
    private PublicationDeliveryTestHelper publicationDeliveryTestHelper;

    @Test
    public void publicationDeliveryWithFareZone() throws Exception {
        LocalDateTime validFrom = LocalDateTime.now().minusDays(3);

        List<JAXBElement<? extends Zone_VersionStructure>> tariffZones = new ArrayList<>();

        FareZone fareZone = new FareZone()
                .withName(new MultilingualString().withValue("V02"))
                .withVersion("1")
                .withValidBetween(new ValidBetween().withFromDate(validFrom))
                .withId("RUT:FareZone:01");

        tariffZones.add(netexObjectFactory.createFareZone(fareZone));
        SiteFrame siteFrame = publicationDeliveryTestHelper.siteFrame()
                .withTariffZones(new TariffZonesInFrame_RelStructure()
                        .withTariffZone(tariffZones));


        PublicationDeliveryStructure publicationDeliveryStructure = publicationDeliveryTestHelper.publicationDelivery(siteFrame);

        PublicationDeliveryStructure response = publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDeliveryStructure);


        final List<JAXBElement<? extends Zone_VersionStructure>> actualZones = publicationDeliveryTestHelper.findSiteFrame(response)
                .getTariffZones().getTariffZone();

        assertThat(actualZones).isNotEmpty();
        assertThat(actualZones.getFirst().getValue().getName().getValue()).isEqualTo(fareZone.getName().getValue());
    }

    @Test
    public void publicationDeliveryWithFareZoneAndStopPlace() throws Exception {
        LocalDateTime validFrom = LocalDateTime.now().minusDays(3);
        List<JAXBElement<? extends Zone_VersionStructure>> tariffZones = new ArrayList<>();

        FareZone fareZone = new FareZone()
                .withName(new MultilingualString().withValue("V02"))
                .withVersion("1")
                .withValidBetween(new ValidBetween().withFromDate(validFrom))
                .withId("RUT:FareZone:05");

        tariffZones.add(netexObjectFactory.createFareZone(fareZone));

        StopPlace stopPlace = new StopPlace();
        stopPlace.withId("XYZ:StopPlace:32111");
        stopPlace.setVersion("1");
        stopPlace.setTariffZones(new TariffZoneRefs_RelStructure()
                .withTariffZoneRef_(netexObjectFactory.createTariffZoneRef(new TariffZoneRef()
                        .withVersion(fareZone.getVersion())
                        .withRef(fareZone.getId()))));
        stopPlace.setQuays(
                new Quays_RelStructure()
                        .withQuayRefOrQuay(netexObjectFactory.createQuay(new Quay()
                                .withId("XYZ:01:02")
                                .withVersion("1")
                                .withName(new MultilingualString().withValue("B"))
                                .withCentroid(new SimplePoint_VersionStructure()
                                        .withLocation(new LocationStructure()
                                                .withLatitude(new BigDecimal("9.6"))
                                                .withLongitude(new BigDecimal("76")))))));

        SiteFrame siteFrame = publicationDeliveryTestHelper.siteFrame()
                .withTariffZones(new TariffZonesInFrame_RelStructure()
                        .withTariffZone(tariffZones))
                .withStopPlaces(new StopPlacesInFrame_RelStructure().withStopPlace_(netexObjectFactory.createStopPlace(stopPlace)));

        PublicationDeliveryStructure publicationDeliveryStructure = publicationDeliveryTestHelper.publicationDelivery(siteFrame);

        ImportParams importParams = new ImportParams();
        importParams.importType = ImportType.INITIAL;

        // First
        publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDeliveryStructure, importParams);

        PublicationDeliveryStructure response = publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDeliveryStructure, importParams);

        List<JAXBElement<? extends Zone_VersionStructure>> actualZones = publicationDeliveryTestHelper.findSiteFrame(response)
                .getTariffZones().getTariffZone();

        assertThat(actualZones).isNotEmpty();

        assertThat(actualZones.getFirst().getValue().getName().getValue()).isEqualTo(fareZone.getName().getValue());
        // Versions for tariff zones are incremented.
        assertThat(actualZones.getFirst().getValue().getVersion()).isEqualTo("2");

    }

    @Test
    public void publicationDeliveryWithFareZoneAndStopPlaceMergeZonesImportTypeMatch() throws Exception {


        SimplePoint_VersionStructure point = new SimplePoint_VersionStructure()
                .withLocation(new LocationStructure()
                        .withLatitude(new BigDecimal("9.6"))
                        .withLongitude(new BigDecimal("76")));

        List<JAXBElement<? extends Zone_VersionStructure>> tariffZones1 = new ArrayList<>();

        FareZone fareZone1 = new FareZone()
                .withName(new MultilingualString().withValue("V02"))
                .withVersion("1")
                .withId("RUT:FareZone:01");
        tariffZones1.add(netexObjectFactory.createFareZone(fareZone1));
        StopPlace stopPlace = new StopPlace()
                .withId("RUT:StopPlace:321")
                .withName(new MultilingualString().withValue("name"))
                .withCentroid(point)
                .withVersion("1")
                .withStopPlaceType(StopTypeEnumeration.ONSTREET_BUS)
                .withQuays(new Quays_RelStructure()
                        .withQuayRefOrQuay(netexObjectFactory.createQuay(new Quay()
                                .withId("XYZ:01:02")
                                .withVersion("1")
                                .withName(new MultilingualString().withValue("B"))
                                .withCentroid(new SimplePoint_VersionStructure()
                                        .withLocation(new LocationStructure()
                                                .withLatitude(new BigDecimal("9.6"))
                                                .withLongitude(new BigDecimal("76")))))))
                .withTariffZones(new TariffZoneRefs_RelStructure()
                        .withTariffZoneRef_(netexObjectFactory.createTariffZoneRef(new TariffZoneRef()
                                .withVersion(fareZone1.getVersion())
                                .withRef(fareZone1.getId()))));

        SiteFrame siteFrame = publicationDeliveryTestHelper.siteFrame()
                .withTariffZones(new TariffZonesInFrame_RelStructure()
                        .withTariffZone(tariffZones1))
                .withStopPlaces(new StopPlacesInFrame_RelStructure().withStopPlace_(netexObjectFactory.createStopPlace(stopPlace)));

        PublicationDeliveryStructure publicationDeliveryStructure = publicationDeliveryTestHelper.publicationDelivery(siteFrame);

        ImportParams importParams = new ImportParams();
        importParams.importType = ImportType.INITIAL;

        // First
        publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDeliveryStructure, importParams);

        List<JAXBElement<? extends Zone_VersionStructure>> tariffZones2 = new ArrayList<>();
        FareZone fareZone2 = new FareZone()
                .withName(new MultilingualString().withValue("X09"))
                .withVersion("1")
                .withId("BRA:FareZone:02");
        tariffZones2.add(netexObjectFactory.createFareZone(fareZone2));
        stopPlace
                .withId("BRA:Stopplace:3")
                .withTariffZones(new TariffZoneRefs_RelStructure()
                        .withTariffZoneRef_(netexObjectFactory.createTariffZoneRef(new TariffZoneRef()
                                .withVersion(fareZone2.getVersion())
                                .withRef(fareZone2.getId()))));


        SiteFrame siteFrame2 = publicationDeliveryTestHelper.siteFrame()
                .withTariffZones(new TariffZonesInFrame_RelStructure()
                        .withTariffZone(tariffZones2))
                .withStopPlaces(new StopPlacesInFrame_RelStructure().withStopPlace_(netexObjectFactory.createStopPlace(stopPlace)));

        PublicationDeliveryStructure publicationDeliveryStructure2 = publicationDeliveryTestHelper.publicationDelivery(siteFrame2);


        // Second import should match and merge farezones
        importParams.importType = ImportType.MATCH;
        PublicationDeliveryStructure response = publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDeliveryStructure2, importParams);

        List<JAXBElement<? extends Zone_VersionStructure>> actualZones = publicationDeliveryTestHelper.findSiteFrame(response)
                .getTariffZones().getTariffZone();

        assertThat(actualZones).isNotEmpty();
        assertThat(actualZones).hasSize(2);

    }

    @Test
    public void mergeFareZonesForStopPlace() throws Exception {


        SimplePoint_VersionStructure point = new SimplePoint_VersionStructure()
                .withLocation(new LocationStructure()
                        .withLatitude(new BigDecimal("77"))
                        .withLongitude(new BigDecimal("9.7")));

        List<JAXBElement<? extends Zone_VersionStructure>> tariffZones1 = new ArrayList<>();
        FareZone fareZone1 = new FareZone()
                .withName(new MultilingualString().withValue("V03"))
                .withVersion("1")
                .withId("ATB:FareZone:01");
        tariffZones1.add(netexObjectFactory.createFareZone(fareZone1));
        StopPlace stopPlace = new StopPlace()
                .withId("ATB:StopPlace:322")
                .withName(new MultilingualString().withValue("name"))
                .withCentroid(point)
                .withVersion("1")
                .withStopPlaceType(StopTypeEnumeration.ONSTREET_BUS)
                .withQuays(new Quays_RelStructure()
                        .withQuayRefOrQuay(netexObjectFactory.createQuay(new Quay()
                                .withId("XYZ:01:02")
                                .withVersion("1")
                                .withName(new MultilingualString().withValue("B"))
                                .withCentroid(new SimplePoint_VersionStructure()
                                        .withLocation(new LocationStructure()
                                                .withLatitude(new BigDecimal("77"))
                                                .withLongitude(new BigDecimal("9.7")))))))
                .withTariffZones(new TariffZoneRefs_RelStructure()
                        .withTariffZoneRef_(netexObjectFactory.createTariffZoneRef(new TariffZoneRef()
                                .withVersion(fareZone1.getVersion())
                                .withRef(fareZone1.getId()))));

        SiteFrame siteFrame = publicationDeliveryTestHelper.siteFrame()
                .withTariffZones(new TariffZonesInFrame_RelStructure()
                        .withTariffZone(tariffZones1))
                .withStopPlaces(new StopPlacesInFrame_RelStructure().withStopPlace_(netexObjectFactory.createStopPlace(stopPlace)));

        PublicationDeliveryStructure publicationDeliveryStructure = publicationDeliveryTestHelper.publicationDelivery(siteFrame);

        ImportParams importParams = new ImportParams();
        importParams.importType = ImportType.INITIAL;

        // First
        publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDeliveryStructure, importParams);

        List<JAXBElement<? extends Zone_VersionStructure>> tariffZones2 = new ArrayList<>();

        FareZone fareZone2 = new FareZone()
                .withName(new MultilingualString().withValue("X08"))
                .withVersion("1")
                .withId("NTR:FareZone:03");
        tariffZones2.add(netexObjectFactory.createFareZone(fareZone2));
        StopPlace stopPlace2 = new StopPlace()
                .withId("NTR:StopPlace:322")
                .withVersion("2")
                .withStopPlaceType(StopTypeEnumeration.ONSTREET_BUS)
                .withQuays(new Quays_RelStructure()
                        .withQuayRefOrQuay(netexObjectFactory.createQuay(new Quay()
                                .withId("XYZ:01:02")
                                .withVersion("1")
                                .withName(new MultilingualString().withValue("B"))
                                .withCentroid(new SimplePoint_VersionStructure()
                                        .withLocation(new LocationStructure()
                                                .withLatitude(new BigDecimal("77"))
                                                .withLongitude(new BigDecimal("9.7")))))))
                .withTariffZones(new TariffZoneRefs_RelStructure()
                        .withTariffZoneRef_(netexObjectFactory.createTariffZoneRef(new TariffZoneRef()
                                .withVersion(fareZone2.getVersion())
                                .withRef(fareZone2.getId()))));


        SiteFrame siteFrame2 = publicationDeliveryTestHelper.siteFrame()
                .withTariffZones(new TariffZonesInFrame_RelStructure()
                        .withTariffZone(tariffZones2))
                .withStopPlaces(new StopPlacesInFrame_RelStructure().withStopPlace_(netexObjectFactory.createStopPlace(stopPlace2)));

        PublicationDeliveryStructure publicationDeliveryStructure2 = publicationDeliveryTestHelper.publicationDelivery(siteFrame2);


        // Second import should match and merge tariffzones
        importParams.importType = ImportType.MATCH;
        PublicationDeliveryStructure matchReponse = publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDeliveryStructure2, importParams);

        List<JAXBElement<? extends Zone_VersionStructure>> actualZones = publicationDeliveryTestHelper.findSiteFrame(matchReponse)
                .getTariffZones().getTariffZone();

        assertThat(actualZones).isNotEmpty();
        assertThat(actualZones).hasSize(2);

        List<StopPlace> actualIdMatchedStopPlaces = publicationDeliveryTestHelper.extractStopPlaces(matchReponse);
        assertThat(actualIdMatchedStopPlaces).hasSize(1);
        assertThat(actualIdMatchedStopPlaces.getFirst().getTariffZones().getTariffZoneRef_()).as("number of tariff zone refs").hasSize(2);

        importParams.importType = ImportType.ID_MATCH;

        PublicationDeliveryStructure idMatchResponse = publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDeliveryStructure2, importParams);

        actualZones = publicationDeliveryTestHelper.findSiteFrame(idMatchResponse)
                .getTariffZones().getTariffZone();
        assertThat(actualZones).isNotEmpty();
        assertThat(actualZones).hasSize(2);

        actualIdMatchedStopPlaces = publicationDeliveryTestHelper.extractStopPlaces(matchReponse);
        assertThat(actualIdMatchedStopPlaces).hasSize(1);
        assertThat(actualIdMatchedStopPlaces.getFirst().getTariffZones().getTariffZoneRef_()).as("number of tariff zone refs").hasSize(2);

    }

    @Test
    public void backwardCompatibilityDefaultToSiteFrame() throws Exception {
        // GIVEN: PublicationDelivery with SiteFrame containing FareZone
        LocalDateTime validFrom = LocalDateTime.now().minusDays(3);
        List<JAXBElement<? extends Zone_VersionStructure>> tariffZones = new ArrayList<>();

        FareZone fareZone = new FareZone()
                .withName(new MultilingualString().withValue("Backward Compat Zone"))
                .withVersion("1")
                .withValidBetween(new ValidBetween().withFromDate(validFrom))
                .withId("RUT:FareZone:BC01");

        tariffZones.add(netexObjectFactory.createFareZone(fareZone));
        SiteFrame siteFrame = publicationDeliveryTestHelper.siteFrame()
                .withTariffZones(new TariffZonesInFrame_RelStructure()
                        .withTariffZone(tariffZones));

        PublicationDeliveryStructure publicationDeliveryStructure = publicationDeliveryTestHelper.publicationDelivery(siteFrame);

        // WHEN: Import without fareZoneFrameSource parameter (should default to SITE_FRAME)
        ImportParams importParams = new ImportParams();
        // Note: fareZoneFrameSource not set, should default to SITE_FRAME

        PublicationDeliveryStructure response = publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDeliveryStructure, importParams);

        // THEN: Should use SiteFrame (default behavior)
        final List<JAXBElement<? extends Zone_VersionStructure>> actualZones = publicationDeliveryTestHelper.findSiteFrame(response)
                .getTariffZones().getTariffZone();

        assertThat(actualZones).isNotEmpty();
        assertThat(actualZones.getFirst().getValue().getName().getValue()).isEqualTo(fareZone.getName().getValue());

        // Verify no FareFrame in response (backward compatible behavior)
        FareFrame responseFareFrame = publicationDeliveryTestHelper.findFareFrame(response);
        assertThat(responseFareFrame).isNull();
    }

    @Test
    public void backwardCompatibilityImportOnlyTariffZonesStillWorks() throws Exception {
        // GIVEN: PublicationDelivery with SiteFrame containing FareZone and TariffZone
        LocalDateTime validFrom = LocalDateTime.now().minusDays(3);
        List<JAXBElement<? extends Zone_VersionStructure>> tariffZones = new ArrayList<>();

        FareZone fareZone = new FareZone()
                .withName(new MultilingualString().withValue("Legacy Import Zone"))
                .withVersion("1")
                .withValidBetween(new ValidBetween().withFromDate(validFrom))
                .withId("RUT:FareZone:Legacy01");

        tariffZones.add(netexObjectFactory.createFareZone(fareZone));
        SiteFrame siteFrame = publicationDeliveryTestHelper.siteFrame()
                .withTariffZones(new TariffZonesInFrame_RelStructure()
                        .withTariffZone(tariffZones));

        PublicationDeliveryStructure publicationDeliveryStructure = publicationDeliveryTestHelper.publicationDelivery(siteFrame);

        // WHEN: Import with importOnlyTariffZones=true (legacy parameter)
        ImportParams importParams = new ImportParams();
        importParams.importOnlyTariffZones = true;

        PublicationDeliveryStructure response = publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDeliveryStructure, importParams);

        // THEN: Should use PublicationDeliveryTariffZoneImporter (legacy behavior)
        final List<JAXBElement<? extends Zone_VersionStructure>> actualZones = publicationDeliveryTestHelper.findSiteFrame(response)
                .getTariffZones().getTariffZone();

        assertThat(actualZones).isNotEmpty();
        assertThat(actualZones.getFirst().getValue().getName().getValue()).isEqualTo(fareZone.getName().getValue());
    }

    @Test
    public void explicitSiteFrameSourceMatchesDefaultBehavior() throws Exception {
        // GIVEN: PublicationDelivery with SiteFrame containing FareZone
        LocalDateTime validFrom = LocalDateTime.now().minusDays(3);
        List<JAXBElement<? extends Zone_VersionStructure>> tariffZones = new ArrayList<>();

        FareZone fareZone = new FareZone()
                .withName(new MultilingualString().withValue("Explicit Site Zone"))
                .withVersion("1")
                .withValidBetween(new ValidBetween().withFromDate(validFrom))
                .withId("RUT:FareZone:Exp01");

        tariffZones.add(netexObjectFactory.createFareZone(fareZone));
        SiteFrame siteFrame = publicationDeliveryTestHelper.siteFrame()
                .withTariffZones(new TariffZonesInFrame_RelStructure()
                        .withTariffZone(tariffZones));

        PublicationDeliveryStructure publicationDeliveryStructure = publicationDeliveryTestHelper.publicationDelivery(siteFrame);

        // WHEN: Import with explicit SITE_FRAME parameter
        ImportParams importParams = new ImportParams();
        importParams.fareZoneFrameSource = org.rutebanken.tiamat.importer.FareZoneFrameSource.SITE_FRAME;

        PublicationDeliveryStructure response = publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDeliveryStructure, importParams);

        // THEN: Should behave identically to default
        final List<JAXBElement<? extends Zone_VersionStructure>> actualZones = publicationDeliveryTestHelper.findSiteFrame(response)
                .getTariffZones().getTariffZone();

        assertThat(actualZones).isNotEmpty();
        assertThat(actualZones.getFirst().getValue().getName().getValue()).isEqualTo(fareZone.getName().getValue());
    }
}