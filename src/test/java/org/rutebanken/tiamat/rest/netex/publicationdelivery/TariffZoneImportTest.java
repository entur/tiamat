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

import org.junit.Test;
import org.rutebanken.netex.model.*;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.importer.ImportType;
import org.rutebanken.tiamat.importer.ImportParams;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
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

    @Test
    public void publicationDeliveryWithTariffZoneAndStopPlace() throws Exception {
        TariffZone tariffZone = new TariffZone()
                .withName(new MultilingualString().withValue("V02"))
                .withVersion("1")
                .withId("RUT:TariffZone:05");

        StopPlace stopPlace = new StopPlace();
        stopPlace.withId("XYZ:StopPlace:32111");
        stopPlace.setVersion("1");
        stopPlace.setTariffZones(new TariffZoneRefs_RelStructure()
                    .withTariffZoneRef(new TariffZoneRef()
                            .withVersion(tariffZone.getVersion())
                            .withRef(tariffZone.getId())));

        SiteFrame siteFrame = publicationDeliveryTestHelper.siteFrame()
                .withTariffZones(new TariffZonesInFrame_RelStructure()
                        .withTariffZone(tariffZone))
                .withStopPlaces(new StopPlacesInFrame_RelStructure().withStopPlace(stopPlace));

        PublicationDeliveryStructure publicationDeliveryStructure = publicationDeliveryTestHelper.publicationDelivery(siteFrame);

        ImportParams importParams = new ImportParams();
        importParams.importType = ImportType.INITIAL;

        // First
        publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDeliveryStructure, importParams);

        PublicationDeliveryStructure response = publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDeliveryStructure, importParams);

        List<TariffZone> actualZones = publicationDeliveryTestHelper.findSiteFrame(response)
                .getTariffZones().getTariffZone();

        assertThat(actualZones).isNotEmpty();
        assertThat(actualZones.get(0).getName().getValue()).isEqualTo(tariffZone.getName().getValue());
        // Versions for tariff zones are not incremented.
        assertThat(actualZones.get(0).getVersion()).isEqualTo("1");
    }

    @Test
    public void publicationDeliveryWithTariffZoneAndStopPlaceMergeZonesImportTypeMatch() throws Exception {


        SimplePoint_VersionStructure point = new SimplePoint_VersionStructure()
                .withLocation(new LocationStructure()
                                .withLatitude(new BigDecimal("9.6"))
                                .withLongitude(new BigDecimal("76")));

        TariffZone tariffZone1 = new TariffZone()
                .withName(new MultilingualString().withValue("V02"))
                .withVersion("1")
                .withId("RUT:TariffZone:01");

        StopPlace stopPlace = new StopPlace()
                .withId("RUT:StopPlace:321")
                .withName(new MultilingualString().withValue("name"))
                .withCentroid(point)
                .withVersion("1")
                .withStopPlaceType(StopTypeEnumeration.ONSTREET_BUS)
                .withTariffZones(new TariffZoneRefs_RelStructure()
                    .withTariffZoneRef(new TariffZoneRef()
                        .withVersion(tariffZone1.getVersion())
                        .withRef(tariffZone1.getId())));

        SiteFrame siteFrame = publicationDeliveryTestHelper.siteFrame()
                .withTariffZones(new TariffZonesInFrame_RelStructure()
                        .withTariffZone(tariffZone1))
                .withStopPlaces(new StopPlacesInFrame_RelStructure().withStopPlace(stopPlace));

        PublicationDeliveryStructure publicationDeliveryStructure = publicationDeliveryTestHelper.publicationDelivery(siteFrame);

        ImportParams importParams = new ImportParams();
        importParams.importType = ImportType.INITIAL;

        // First
        publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDeliveryStructure, importParams);

        TariffZone tariffZone2 = new TariffZone()
                .withName(new MultilingualString().withValue("X09"))
                .withVersion("1")
                .withId("BRA:TariffZone:02");

        stopPlace
                .withId("BRA:Stopplace:3")
                .withTariffZones(new TariffZoneRefs_RelStructure()
                    .withTariffZoneRef(new TariffZoneRef()
                        .withVersion(tariffZone2.getVersion())
                        .withRef(tariffZone2.getId())));


        SiteFrame siteFrame2 = publicationDeliveryTestHelper.siteFrame()
                .withTariffZones(new TariffZonesInFrame_RelStructure()
                        .withTariffZone(tariffZone2))
                .withStopPlaces(new StopPlacesInFrame_RelStructure().withStopPlace(stopPlace));

        PublicationDeliveryStructure publicationDeliveryStructure2 = publicationDeliveryTestHelper.publicationDelivery(siteFrame2);


        // Second import should match and merge tariffzones
        importParams.importType = ImportType.MATCH;
        PublicationDeliveryStructure response = publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDeliveryStructure2, importParams);

        List<TariffZone> actualZones = publicationDeliveryTestHelper.findSiteFrame(response)
                .getTariffZones().getTariffZone();

        assertThat(actualZones).isNotEmpty();
        assertThat(actualZones).hasSize(2);

    }

    @Test
    public void mergeTariffZonesForStopPlace() throws Exception {


        SimplePoint_VersionStructure point = new SimplePoint_VersionStructure()
                .withLocation(new LocationStructure()
                        .withLatitude(new BigDecimal("77"))
                        .withLongitude(new BigDecimal("9.7")));

        TariffZone tariffZone1 = new TariffZone()
                .withName(new MultilingualString().withValue("V03"))
                .withVersion("1")
                .withId("ATB:TariffZone:01");

        StopPlace stopPlace = new StopPlace()
                .withId("ATB:StopPlace:322")
                .withName(new MultilingualString().withValue("name"))
                .withCentroid(point)
                .withVersion("1")
                .withStopPlaceType(StopTypeEnumeration.ONSTREET_BUS)
                .withTariffZones(new TariffZoneRefs_RelStructure()
                        .withTariffZoneRef(new TariffZoneRef()
                                .withVersion(tariffZone1.getVersion())
                                .withRef(tariffZone1.getId())));

        SiteFrame siteFrame = publicationDeliveryTestHelper.siteFrame()
                .withTariffZones(new TariffZonesInFrame_RelStructure()
                        .withTariffZone(tariffZone1))
                .withStopPlaces(new StopPlacesInFrame_RelStructure().withStopPlace(stopPlace));

        PublicationDeliveryStructure publicationDeliveryStructure = publicationDeliveryTestHelper.publicationDelivery(siteFrame);

        ImportParams importParams = new ImportParams();
        importParams.importType = ImportType.INITIAL;

        // First
        publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDeliveryStructure, importParams);

        TariffZone tariffZone2 = new TariffZone()
                .withName(new MultilingualString().withValue("X08"))
                .withVersion("1")
                .withId("NTR:TariffZone:03");

        StopPlace stopPlace2 = new StopPlace()
                .withId("NTR:StopPlace:322")
                .withVersion("2")
                .withStopPlaceType(StopTypeEnumeration.ONSTREET_BUS)
                .withTariffZones(new TariffZoneRefs_RelStructure()
                        .withTariffZoneRef(new TariffZoneRef()
                                .withVersion(tariffZone2.getVersion())
                                .withRef(tariffZone2.getId())));


        SiteFrame siteFrame2 = publicationDeliveryTestHelper.siteFrame()
                .withTariffZones(new TariffZonesInFrame_RelStructure()
                        .withTariffZone(tariffZone2))
                .withStopPlaces(new StopPlacesInFrame_RelStructure().withStopPlace(stopPlace2));

        PublicationDeliveryStructure publicationDeliveryStructure2 = publicationDeliveryTestHelper.publicationDelivery(siteFrame2);


        // Second import should match and merge tariffzones
        importParams.importType = ImportType.MATCH;
        PublicationDeliveryStructure matchReponse = publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDeliveryStructure2, importParams);

        List<TariffZone> actualZones = publicationDeliveryTestHelper.findSiteFrame(matchReponse)
                .getTariffZones().getTariffZone();

        assertThat(actualZones).isNotEmpty();
        assertThat(actualZones).hasSize(2);

        List<StopPlace> actualIdMatchedStopPlaces = publicationDeliveryTestHelper.extractStopPlaces(matchReponse);
        assertThat(actualIdMatchedStopPlaces).hasSize(1);
        assertThat(actualIdMatchedStopPlaces.get(0).getTariffZones().getTariffZoneRef()).as("number of tariff zone refs").hasSize(2);

        importParams.importType = ImportType.ID_MATCH;

        PublicationDeliveryStructure idMatchResponse = publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDeliveryStructure2, importParams);

        actualZones = publicationDeliveryTestHelper.findSiteFrame(idMatchResponse)
                .getTariffZones().getTariffZone();
        assertThat(actualZones).isNotEmpty();
        assertThat(actualZones).hasSize(2);

        actualIdMatchedStopPlaces = publicationDeliveryTestHelper.extractStopPlaces(matchReponse);
        assertThat(actualIdMatchedStopPlaces).hasSize(1);
        assertThat(actualIdMatchedStopPlaces.get(0).getTariffZones().getTariffZoneRef()).as("number of tariff zone refs").hasSize(2);

    }
}
