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

import net.opengis.gml._3.AbstractRingPropertyType;
import net.opengis.gml._3.DirectPositionListType;
import net.opengis.gml._3.LinearRingType;
import net.opengis.gml._3.ObjectFactory;
import net.opengis.gml._3.PolygonType;
import org.junit.Test;
import org.rutebanken.netex.model.CountryRef;
import org.rutebanken.netex.model.MultilingualString;
import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.rutebanken.netex.model.TopographicPlace;
import org.rutebanken.netex.model.TopographicPlaceDescriptor_VersionedChildStructure;
import org.rutebanken.netex.model.TopographicPlaceRefStructure;
import org.rutebanken.netex.model.TopographicPlaceTypeEnumeration;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.netex.mapping.converter.PolygonConverter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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

        MultilingualString nameDescriptor = new MultilingualString().withContent("Vestfold").withLang("nb");

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
        TopographicPlace actualTopographicPlace = result.getFirst();

        assertThat(actualTopographicPlace.getPolygon())
                .as("polygon must not be null")
                .isNotNull();

        List<Double> actualExteriorValues = polygonConverter.extractValues(topographicPlace.getPolygon().getExterior());

        assertThat(actualExteriorValues).isEqualTo(values);
        assertThat(actualTopographicPlace.getId()).isEqualTo(topographicPlace.getId());
    }

    @Test
    public void publicationDeliveryWithParentTopographicPlace() throws Exception {
        MultilingualString countyName = new MultilingualString().withContent("Vestfold").withLang("nb");

        TopographicPlace county = new TopographicPlace()
                .withId("KVE:TopographicPlace:07")
                .withName(countyName)
                .withVersion("1")
                .withDescriptor(new TopographicPlaceDescriptor_VersionedChildStructure().withName(countyName))
                .withTopographicPlaceType(TopographicPlaceTypeEnumeration.COUNTY)
                .withCountryRef(new CountryRef().withValue("NO"));

        MultilingualString municipalityName = new MultilingualString().withContent("Larvik").withLang("nb");

        TopographicPlace municipality = new TopographicPlace()
                .withId("KVE:TopographicPlace:08")
                .withName(municipalityName)
                .withVersion("1")
                .withDescriptor(new TopographicPlaceDescriptor_VersionedChildStructure().withName(municipalityName))
                .withTopographicPlaceType(TopographicPlaceTypeEnumeration.MUNICIPALITY)
                .withParentTopographicPlaceRef(new TopographicPlaceRefStructure().withRef(county.getId()).withVersion(county.getVersion()));

        PublicationDeliveryStructure publicationDelivery = publicationDeliveryTestHelper.createPublicationDeliveryTopographicPlace(municipality, county);

        PublicationDeliveryStructure response = publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDelivery);

        List<TopographicPlace> result = publicationDeliveryTestHelper.extractTopographicPlace(response);
        assertThat(result).as("Expecting topographic place in return").hasSize(2);
    }

    @Test
    public void publicationDeliveryWithParentTopographicPlaceValidReferences() throws Exception {
        MultilingualString countyName = new MultilingualString().withContent("Vestfold").withLang("nb");

        TopographicPlace county = new TopographicPlace()
                .withId("KVE:TopographicPlace:07")
                .withName(countyName)
                .withVersion("1")
                .withDescriptor(new TopographicPlaceDescriptor_VersionedChildStructure().withName(countyName))
                .withTopographicPlaceType(TopographicPlaceTypeEnumeration.COUNTY)
                .withCountryRef(new CountryRef().withValue("NO"));

        MultilingualString municipalityName = new MultilingualString().withContent("Larvik").withLang("nb");
        TopographicPlace municipality = new TopographicPlace()
                .withId("KVE:TopographicPlace:08")
                .withName(municipalityName)
                .withVersion("1")
                .withDescriptor(new TopographicPlaceDescriptor_VersionedChildStructure().withName(municipalityName))
                .withTopographicPlaceType(TopographicPlaceTypeEnumeration.MUNICIPALITY)
                .withParentTopographicPlaceRef(new TopographicPlaceRefStructure().withRef(county.getId()).withVersion(county.getVersion()));


        PublicationDeliveryStructure publicationDelivery = publicationDeliveryTestHelper.createPublicationDeliveryTopographicPlace(municipality, county);

        publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDelivery);

        PublicationDeliveryStructure response = publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDelivery);

        List<TopographicPlace> result = publicationDeliveryTestHelper.extractTopographicPlace(response);
        assertThat(result).as("Expecting topographic place in return").hasSize(2);

        for(TopographicPlace topographicPlace : result) {
            if(topographicPlace.getParentTopographicPlaceRef() != null) {
                assertThat(topographicPlace.getParentTopographicPlaceRef().getVersion()).isEqualTo("2");
            } else {
                assertThat(topographicPlace.getVersion()).isEqualTo("2");
            }

        }
    }

    @Test
    public void reimportTopographicPlaceAndExpectVersionIncremented() throws Exception {
        MultilingualString countyName = new MultilingualString().withContent("Vestfold").withLang("nb");

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
        assertThat(result.getFirst().getVersion()).isEqualTo("1");

        response = publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDelivery);
        result = publicationDeliveryTestHelper.extractTopographicPlace(response);

        assertThat(result).as("Expecting topographic place in return").hasSize(1);
        assertThat(result.getFirst().getVersion()).isEqualTo("2");
    }

    @Test(expected = Exception.class)
    public void publicationDeliveryWithInvalidParentTopographicPlaceRef() throws Exception {

        MultilingualString municipalityName = new MultilingualString().withContent("Larvik").withLang("nb");

        TopographicPlace municipality = new TopographicPlace()
                .withId("KVE:TopographicPlace:08")
                .withName(municipalityName)
                .withVersion("1")
                .withDescriptor(new TopographicPlaceDescriptor_VersionedChildStructure().withName(municipalityName))
                .withTopographicPlaceType(TopographicPlaceTypeEnumeration.MUNICIPALITY)
                .withParentTopographicPlaceRef(new TopographicPlaceRefStructure().withRef("KVE:TopographicPlace:1").withVersion("1"));

        PublicationDeliveryStructure publicationDelivery = publicationDeliveryTestHelper.createPublicationDeliveryTopographicPlace(municipality);

        publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDelivery);
    }
}
