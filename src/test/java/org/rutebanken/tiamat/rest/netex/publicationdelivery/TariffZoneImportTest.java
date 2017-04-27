package org.rutebanken.tiamat.rest.netex.publicationdelivery;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;
import net.opengis.gml._3.DirectPositionListType;
import net.opengis.gml._3.LineStringType;
import org.junit.Test;
import org.rutebanken.netex.model.*;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.config.GeometryFactoryConfig;
import org.rutebanken.tiamat.importer.ImportType;
import org.rutebanken.tiamat.importer.PublicationDeliveryParams;
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

    @Test
    public void publicationDeliveryWithTariffZoneAndStopPlace() throws Exception {
        TariffZone tariffZone = new TariffZone()
                .withName(new MultilingualString().withValue("V02"))
                .withVersion("1")
                .withId("RUT:TariffZone:01");

        StopPlace stopPlace = new StopPlace();
        stopPlace.withId("XYZ:StopPlace:321");
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

        PublicationDeliveryParams publicationDeliveryParams = new PublicationDeliveryParams();
        publicationDeliveryParams.importType = ImportType.INITIAL;

        // First
        publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDeliveryStructure, publicationDeliveryParams);

        // Second causes tariffzone to be in second version. Then the version reference from stop place must be updated, or else the validation fails
        PublicationDeliveryStructure response = publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDeliveryStructure, publicationDeliveryParams);

        List<TariffZone> actualZones = publicationDeliveryTestHelper.findSiteFrame(response)
                .getTariffZones().getTariffZone();

        assertThat(actualZones).isNotEmpty();
        assertThat(actualZones.get(0).getName().getValue()).isEqualTo(tariffZone.getName().getValue());
    }

    @Test
    public void publicationDeliveryWithTariffZoneAndStopPlaceMergeZones() throws Exception {


        SimplePoint_VersionStructure point = new SimplePoint_VersionStructure()
                .withLocation(new LocationStructure()
                                .withLatitude(new BigDecimal("9.6"))
                                .withLongitude(new BigDecimal("76")));

        TariffZone tariffZone1 = new TariffZone()
                .withName(new MultilingualString().withValue("V02"))
                .withVersion("1")
                .withId("RUT:TariffZone:01");

        StopPlace stopPlace = new StopPlace()
                .withId("XYZ:StopPlace:321")
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

        PublicationDeliveryParams publicationDeliveryParams = new PublicationDeliveryParams();
        publicationDeliveryParams.importType = ImportType.INITIAL;

        // First
        publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDeliveryStructure, publicationDeliveryParams);

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
        publicationDeliveryParams.importType = ImportType.MATCH;
        PublicationDeliveryStructure response = publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDeliveryStructure2, publicationDeliveryParams);

        List<TariffZone> actualZones = publicationDeliveryTestHelper.findSiteFrame(response)
                .getTariffZones().getTariffZone();

        assertThat(actualZones).isNotEmpty();
        assertThat(actualZones).hasSize(2);

    }
}
