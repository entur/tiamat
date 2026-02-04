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
import net.opengis.gml._3.MultiSurfaceType;
import net.opengis.gml._3.ObjectFactory;
import net.opengis.gml._3.PolygonType;
import net.opengis.gml._3.SurfacePropertyType;
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

public class TopographicPlaceRestImportIntegrationTest extends TiamatIntegrationTest {

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
        TopographicPlace actualTopographicPlace = result.getFirst();

        assertThat(actualTopographicPlace.getPolygon())
                .as("polygon must not be null")
                .isNotNull();

        List<Double> actualExteriorValues = polygonConverter.extractValues(topographicPlace.getPolygon().getExterior());

        assertThat(actualExteriorValues).isEqualTo(values);
        assertThat(actualTopographicPlace.getId()).isEqualTo(topographicPlace.getId());
    }

    @Test
    public void publicationDeliveryWithTopographicPlaceAndPolygonWithHole() throws Exception {

        // Exterior ring (outer boundary) - larger polygon
        List<Double> exteriorValues = new ArrayList<>();
        exteriorValues.add(9.0);
        exteriorValues.add(59.0);
        exteriorValues.add(10.0);
        exteriorValues.add(59.0);
        exteriorValues.add(10.0);
        exteriorValues.add(60.0);
        exteriorValues.add(9.0);
        exteriorValues.add(60.0);
        exteriorValues.add(exteriorValues.get(0));
        exteriorValues.add(exteriorValues.get(1));

        // Interior ring (hole) - smaller polygon inside the exterior
        List<Double> interiorValues = new ArrayList<>();
        interiorValues.add(9.3);
        interiorValues.add(59.3);
        interiorValues.add(9.7);
        interiorValues.add(59.3);
        interiorValues.add(9.7);
        interiorValues.add(59.7);
        interiorValues.add(9.3);
        interiorValues.add(59.7);
        interiorValues.add(interiorValues.get(0));
        interiorValues.add(interiorValues.get(1));

        PolygonType polygonType = new PolygonType()
                .withId("KVE-HOLE-07")
                .withExterior(new AbstractRingPropertyType()
                        .withAbstractRing(openGisObjectFactory.createLinearRing(
                                new LinearRingType()
                                        .withPosList(new DirectPositionListType().withValue(exteriorValues)))))
                .withInterior(new AbstractRingPropertyType()
                        .withAbstractRing(openGisObjectFactory.createLinearRing(
                                new LinearRingType()
                                        .withPosList(new DirectPositionListType().withValue(interiorValues)))));

        MultilingualString nameDescriptor = new MultilingualString().withValue("Vestfold med hull").withLang("nb");

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

        // Verify exterior ring
        List<Double> actualExteriorValues = polygonConverter.extractValues(actualTopographicPlace.getPolygon().getExterior());
        assertThat(actualExteriorValues).as("exterior ring values").isEqualTo(exteriorValues);

        // Verify interior ring (hole)
        assertThat(actualTopographicPlace.getPolygon().getInterior())
                .as("interior rings list")
                .isNotNull()
                .hasSize(1);

        List<Double> actualInteriorValues = polygonConverter.extractValues(actualTopographicPlace.getPolygon().getInterior().getFirst());
        assertThat(actualInteriorValues).as("interior ring values").isEqualTo(interiorValues);

        assertThat(actualTopographicPlace.getId()).isEqualTo(topographicPlace.getId());
    }

    @Test
    public void publicationDeliveryWithTopographicPlaceAndMultiSurface() throws Exception {

        // First polygon coordinates
        List<Double> polygon1Values = new ArrayList<>();
        polygon1Values.add(9.8468);
        polygon1Values.add(59.2649);
        polygon1Values.add(9.8456);
        polygon1Values.add(59.2654);
        polygon1Values.add(9.8457);
        polygon1Values.add(59.2655);
        polygon1Values.add(9.8443);
        polygon1Values.add(59.2663);
        polygon1Values.add(polygon1Values.get(0));
        polygon1Values.add(polygon1Values.get(1));

        // Second polygon coordinates (offset from first)
        List<Double> polygon2Values = new ArrayList<>();
        polygon2Values.add(10.8468);
        polygon2Values.add(60.2649);
        polygon2Values.add(10.8456);
        polygon2Values.add(60.2654);
        polygon2Values.add(10.8457);
        polygon2Values.add(60.2655);
        polygon2Values.add(10.8443);
        polygon2Values.add(60.2663);
        polygon2Values.add(polygon2Values.get(0));
        polygon2Values.add(polygon2Values.get(1));

        // Create first polygon
        PolygonType polygonType1 = new PolygonType()
                .withId("KVE-07-1")
                .withExterior(new AbstractRingPropertyType()
                        .withAbstractRing(openGisObjectFactory.createLinearRing(
                                new LinearRingType()
                                        .withPosList(new DirectPositionListType().withValue(polygon1Values)))));

        // Create second polygon
        PolygonType polygonType2 = new PolygonType()
                .withId("KVE-07-2")
                .withExterior(new AbstractRingPropertyType()
                        .withAbstractRing(openGisObjectFactory.createLinearRing(
                                new LinearRingType()
                                        .withPosList(new DirectPositionListType().withValue(polygon2Values)))));

        // Create MultiSurface with two polygon surface members
        MultiSurfaceType multiSurfaceType = new MultiSurfaceType()
                .withId("KVE-MultiSurface-07")
                .withSurfaceMember(
                        new SurfacePropertyType().withAbstractSurface(openGisObjectFactory.createPolygon(polygonType1)),
                        new SurfacePropertyType().withAbstractSurface(openGisObjectFactory.createPolygon(polygonType2))
                );

        MultilingualString nameDescriptor = new MultilingualString().withValue("Vestfold").withLang("nb");

        TopographicPlace topographicPlace = new TopographicPlace()
                .withId("KVE:TopographicPlace:07")
                .withName(nameDescriptor)
                .withVersion("1")
                .withDescriptor(new TopographicPlaceDescriptor_VersionedChildStructure().withName(nameDescriptor))
                .withTopographicPlaceType(TopographicPlaceTypeEnumeration.COUNTY)
                .withCountryRef(new CountryRef().withValue("NO"))
                .withMultiSurface(multiSurfaceType);

        PublicationDeliveryStructure publicationDelivery = publicationDeliveryTestHelper.createPublicationDeliveryTopographicPlace(topographicPlace);

        PublicationDeliveryStructure response = publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDelivery);

        List<TopographicPlace> result = publicationDeliveryTestHelper.extractTopographicPlace(response);
        assertThat(result).as("Expecting topographic place in return").hasSize(1);
        TopographicPlace actualTopographicPlace = result.getFirst();

        // Assert that either multiSurface or polygon is returned
        // The import should handle multiSurface and convert/store it appropriately
        assertThat(actualTopographicPlace.getMultiSurface() != null || actualTopographicPlace.getPolygon() != null)
                .as("Either multiSurface or polygon must not be null")
                .isTrue();

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
                .withTopographicPlaceType(TopographicPlaceTypeEnumeration.MUNICIPALITY)
                .withParentTopographicPlaceRef(new TopographicPlaceRefStructure().withRef(county.getId()).withVersion(county.getVersion()));

        PublicationDeliveryStructure publicationDelivery = publicationDeliveryTestHelper.createPublicationDeliveryTopographicPlace(municipality, county);

        PublicationDeliveryStructure response = publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDelivery);

        List<TopographicPlace> result = publicationDeliveryTestHelper.extractTopographicPlace(response);
        assertThat(result).as("Expecting topographic place in return").hasSize(2);
    }

    @Test
    public void publicationDeliveryWithParentTopographicPlaceValidReferences() throws Exception {
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
        assertThat(result.getFirst().getVersion()).isEqualTo("1");

        response = publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDelivery);
        result = publicationDeliveryTestHelper.extractTopographicPlace(response);

        assertThat(result).as("Expecting topographic place in return").hasSize(1);
        assertThat(result.getFirst().getVersion()).isEqualTo("2");
    }

    @Test(expected = Exception.class)
    public void publicationDeliveryWithInvalidParentTopographicPlaceRef() throws Exception {

        MultilingualString municipalityName = new MultilingualString().withValue("Larvik").withLang("nb");

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
