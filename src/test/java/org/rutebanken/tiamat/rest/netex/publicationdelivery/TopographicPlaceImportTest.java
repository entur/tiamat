package org.rutebanken.tiamat.rest.netex.publicationdelivery;

import com.vividsolutions.jts.geom.Coordinate;
import net.opengis.gml._3.*;
import net.opengis.gml._3.ObjectFactory;
import org.junit.Test;
import org.rutebanken.netex.model.*;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.netex.mapping.converter.PolygonConverter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.rutebanken.tiamat.model.VersionOfObjectRefStructure.ANY_VERSION;

public class TopographicPlaceImportTest extends TiamatIntegrationTest {

    private static final net.opengis.gml._3.ObjectFactory openGisObjectFactory = new ObjectFactory();

    @Autowired
    private PublicationDeliveryTestHelper publicationDeliveryTestHelper;

    @Autowired
    private PolygonConverter polygonConverter;

    @Test
    public void publicationDeliveryWithTopographicPlaceAndPolygon() throws Exception {

        List<Double> values = new ArrayList<>();
        values.add(9.8468);
        values.add(59.2649);
        values.add(9.8456);
        values.add(59.2654);
        values.add(9.8457);
        values.add(59.2655);
        values.add(9.8443);
        values.add(59.2663);
        values.add(values.get(0));
        values.add(values.get(1));

        DirectPositionListType positionList = new DirectPositionListType().withValue(values);

        LinearRingType linearRing = new LinearRingType()
                .withPosList(positionList);

        PolygonType polygonType = new PolygonType()
                .withId("KVE-07")
                .withExterior(new AbstractRingPropertyType()
                    .withAbstractRing(openGisObjectFactory.createLinearRing(linearRing)));

        MultilingualString nameDescriptor = new MultilingualString().withValue("Vestfold").withLang("nb");

        TopographicPlace topographicPlace = new TopographicPlace()
                .withId("KVE:TopographicPlace:07")
                .withName(nameDescriptor)
                .withVersion("1")
                .withDescriptor(new TopographicPlaceDescriptor_VersionedChildStructure().withName(nameDescriptor))
                .withTopographicPlaceType(TopographicPlaceTypeEnumeration.COUNTY)
                .withCountryRef(new CountryRef().withValue("NO"))
                .withPolygon(polygonType);

        PublicationDeliveryStructure publicationDelivery = publicationDeliveryTestHelper.createPublicationDeliveryTopographicPlace(topographicPlace);

        PublicationDeliveryStructure response = publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDelivery);

        List<TopographicPlace> result = publicationDeliveryTestHelper.extractTopographicPlace(response);
        assertThat(result).as("Expecting topographic place in return").hasSize(1);
        TopographicPlace actualTopographicPlace = result.get(0);

        assertThat(actualTopographicPlace.getPolygon())
                .as("polygon must not be null")
                .isNotNull();

        List<Double> actualExteriorValues = polygonConverter.extractValues(topographicPlace.getPolygon().getExterior());

        assertThat(actualExteriorValues).isEqualTo(values);
        assertThat(actualTopographicPlace.getId()).isEqualTo(topographicPlace.getId());
    }

    @Test
    public void publicationDeliveryWithParentTopographicPlace() throws Exception {
        MultilingualString countyName = new MultilingualString().withValue("Vestfold").withLang("nb");

        TopographicPlace county = new TopographicPlace()
                .withId("KVE:TopographicPlace:07")
                .withName(countyName)
                .withVersion("1")
                .withDescriptor(new TopographicPlaceDescriptor_VersionedChildStructure().withName(countyName))
                .withTopographicPlaceType(TopographicPlaceTypeEnumeration.COUNTY)
                .withCountryRef(new CountryRef().withValue("NO"));

        MultilingualString municipalityName = new MultilingualString().withValue("Larvik").withLang("nb");

        TopographicPlace municipality = new TopographicPlace()
                .withId("KVE:TopographicPlace:08")
                .withName(municipalityName)
                .withVersion("1")
                .withDescriptor(new TopographicPlaceDescriptor_VersionedChildStructure().withName(municipalityName))
                .withTopographicPlaceType(TopographicPlaceTypeEnumeration.TOWN)
                .withParentTopographicPlaceRef(new TopographicPlaceRefStructure().withRef(county.getId()).withVersion(county.getVersion()));

        PublicationDeliveryStructure publicationDelivery = publicationDeliveryTestHelper.createPublicationDeliveryTopographicPlace(municipality, county);

        PublicationDeliveryStructure response = publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDelivery);

        List<TopographicPlace> result = publicationDeliveryTestHelper.extractTopographicPlace(response);
        assertThat(result).as("Expecting topographic place in return").hasSize(2);
    }

    @Test
    public void reimportTopographicPlaceAndExpectVersionIncremented() throws Exception {
        MultilingualString countyName = new MultilingualString().withValue("Vestfold").withLang("nb");

        TopographicPlace county = new TopographicPlace()
                .withId("KVE:TopographicPlace:07")
                .withName(countyName)
                .withVersion("1")
                .withDescriptor(new TopographicPlaceDescriptor_VersionedChildStructure().withName(countyName))
                .withTopographicPlaceType(TopographicPlaceTypeEnumeration.COUNTY)
                .withCountryRef(new CountryRef().withValue("NO"));

        PublicationDeliveryStructure publicationDelivery = publicationDeliveryTestHelper.createPublicationDeliveryTopographicPlace(county);

        PublicationDeliveryStructure response = publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDelivery);

        List<TopographicPlace> result = publicationDeliveryTestHelper.extractTopographicPlace(response);
        assertThat(result).as("Expecting topographic place in return").hasSize(1);
        assertThat(result.get(0).getVersion()).isEqualTo("1");
    
        response = publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDelivery);
        result = publicationDeliveryTestHelper.extractTopographicPlace(response);

        assertThat(result).as("Expecting topographic place in return").hasSize(1);
        assertThat(result.get(0).getVersion()).isEqualTo("2");
    }

    @Test(expected = Exception.class)
    public void publicationDeliveryWithInvalidParentTopographicPlaceRef() throws Exception {

        MultilingualString municipalityName = new MultilingualString().withValue("Larvik").withLang("nb");

        TopographicPlace municipality = new TopographicPlace()
                .withId("KVE:TopographicPlace:08")
                .withName(municipalityName)
                .withVersion("1")
                .withDescriptor(new TopographicPlaceDescriptor_VersionedChildStructure().withName(municipalityName))
                .withTopographicPlaceType(TopographicPlaceTypeEnumeration.TOWN)
                .withParentTopographicPlaceRef(new TopographicPlaceRefStructure().withRef("KVE:TopographicPlace:1").withVersion(ANY_VERSION));

        PublicationDeliveryStructure publicationDelivery = publicationDeliveryTestHelper.createPublicationDeliveryTopographicPlace(municipality);

        publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDelivery);
    }
}
