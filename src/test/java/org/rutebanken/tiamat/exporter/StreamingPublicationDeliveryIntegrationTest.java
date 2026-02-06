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

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import org.junit.Test;
import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.rutebanken.netex.validation.NeTExValidator;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.exporter.params.ExportParams;
import org.rutebanken.tiamat.exporter.params.StopPlaceSearch;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.GroupOfStopPlaces;
import org.rutebanken.tiamat.model.PurposeOfGrouping;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.StopPlaceReference;
import org.rutebanken.tiamat.model.TariffZone;
import org.rutebanken.tiamat.model.TariffZoneRef;
import org.rutebanken.tiamat.model.TopographicPlace;
import org.rutebanken.tiamat.model.TopographicPlaceRefStructure;
import org.rutebanken.tiamat.model.TopographicPlaceTypeEnumeration;
import org.rutebanken.tiamat.model.ValidBetween;
import org.rutebanken.tiamat.netex.mapping.PublicationDeliveryHelper;
import org.rutebanken.tiamat.netex.validation.NetexReferenceValidatorException;
import org.rutebanken.tiamat.netex.validation.NetexXmlReferenceValidator;
import org.rutebanken.tiamat.rest.netex.publicationdelivery.PublicationDeliveryTestHelper;
import org.rutebanken.tiamat.rest.netex.publicationdelivery.PublicationDeliveryUnmarshaller;
import org.rutebanken.tiamat.versioning.save.GroupOfStopPlacesSaverService;
import org.rutebanken.tiamat.versioning.save.PurposeOfGroupingSaverService;
import org.rutebanken.tiamat.versioning.save.TariffZoneSaverService;
import org.rutebanken.tiamat.versioning.save.TopographicPlaceVersionedSaverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;
import org.xml.sax.SAXException;

import javax.xml.stream.XMLStreamException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static jakarta.xml.bind.JAXBContext.newInstance;
import static org.assertj.core.api.Assertions.assertThat;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.impl.CoordinateArraySequence;

@Transactional
public class StreamingPublicationDeliveryIntegrationTest extends TiamatIntegrationTest {

    @Qualifier("syncStreamingPublicationDelivery")
    @Autowired
    private StreamingPublicationDelivery streamingPublicationDelivery;

    @Autowired
    private GroupOfStopPlacesSaverService groupOfStopPlacesSaverService;

    @Autowired
    private PurposeOfGroupingSaverService purposeOfGroupingSaverService;

    @Autowired
    private TopographicPlaceVersionedSaverService topographicPlaceVersionedSaverService;

    @Autowired
    private PublicationDeliveryUnmarshaller publicationDeliveryUnmarshaller;

    @Autowired
    private PublicationDeliveryHelper publicationDeliveryHelper;

    @Autowired
    private PublicationDeliveryTestHelper publicationDeliveryTestHelper;

    private final NetexXmlReferenceValidator netexXmlReferenceValidator = new NetexXmlReferenceValidator(true);


    /**
     * Export more than default page size to check that default paging is overriden
     * @throws InterruptedException
     * @throws IOException
     * @throws XMLStreamException
     * @throws SAXException
     * @throws JAXBException
     */
    @Test
    public void exportMoreThanDefaultPageSize() throws InterruptedException, IOException, XMLStreamException, SAXException, JAXBException {

        final int numberOfStopPlaces = StopPlaceSearch.DEFAULT_PAGE_SIZE + 1;
        for(int i = 0; i < numberOfStopPlaces; i++) {
            StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("stop place numbber " + i));
            stopPlace.setVersion(1L);
            stopPlaceRepository.save(stopPlace);
        }
        stopPlaceRepository.flush();


        ExportParams exportParams = ExportParams.newExportParamsBuilder()
                .setStopPlaceSearch(
                        StopPlaceSearch
                                .newStopPlaceSearchBuilder()
                                .setVersionValidity(ExportParams.VersionValidity.ALL)
                                .build())
                .build();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        streamingPublicationDelivery.stream(exportParams, byteArrayOutputStream, true);

        PublicationDeliveryStructure publicationDeliveryStructure = publicationDeliveryTestHelper.fromString(byteArrayOutputStream.toString());
        List<org.rutebanken.netex.model.StopPlace> stopPlaces = publicationDeliveryTestHelper.extractStopPlaces(publicationDeliveryStructure);
        assertThat(stopPlaces).hasSize(numberOfStopPlaces);
    }

    @Test
    public void avoidDuplicateTopographicPlaceWhenExportModeAll() throws InterruptedException, IOException, XMLStreamException, SAXException, JAXBException, NetexReferenceValidatorException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        TopographicPlace county = new TopographicPlace(new EmbeddableMultilingualString("county"));
        county.setTopographicPlaceType(TopographicPlaceTypeEnumeration.COUNTY);
        county = topographicPlaceVersionedSaverService.saveNewVersion(county);

        TopographicPlace municipality = new TopographicPlace(new EmbeddableMultilingualString("Some municipality"));
        municipality.setTopographicPlaceType(TopographicPlaceTypeEnumeration.MUNICIPALITY);
        municipality.setParentTopographicPlaceRef(new TopographicPlaceRefStructure(county.getNetexId(), String.valueOf(county.getVersion()  )));
        municipality = topographicPlaceVersionedSaverService.saveNewVersion(municipality);

        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("stop place"));
        stopPlace.setTopographicPlace(municipality);
        stopPlaceRepository.save(stopPlace);

        stopPlaceRepository.flush();

        ExportParams exportParams = ExportParams.newExportParamsBuilder()
                .setStopPlaceSearch(
                        StopPlaceSearch.newStopPlaceSearchBuilder()
                                .setVersionValidity(ExportParams.VersionValidity.CURRENT_FUTURE)
                                .build())
                .setTopographicPlaceExportMode(ExportParams.ExportMode.ALL)
                .setTariffZoneExportMode(ExportParams.ExportMode.RELEVANT)
                .build();

        streamingPublicationDelivery.stream(exportParams, byteArrayOutputStream);

        String xml = byteArrayOutputStream.toString();

        System.out.println(xml);

        validate(xml);
        netexXmlReferenceValidator.validateNetexReferences(new ByteArrayInputStream(xml.getBytes()), "publicationDelivery");


    }

    /**
     * Set export modes to none, to see that export netex is valid
     */
    @Test
    public void handleExportModeSetToNone() throws InterruptedException, IOException, XMLStreamException, SAXException, JAXBException, NetexReferenceValidatorException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        TopographicPlace county = new TopographicPlace(new EmbeddableMultilingualString("county"));
        county.setTopographicPlaceType(TopographicPlaceTypeEnumeration.COUNTY);
        county = topographicPlaceVersionedSaverService.saveNewVersion(county);

        TopographicPlace municipality = new TopographicPlace(new EmbeddableMultilingualString("Some municipality"));
        municipality.setTopographicPlaceType(TopographicPlaceTypeEnumeration.MUNICIPALITY);
        municipality.setParentTopographicPlaceRef(new TopographicPlaceRefStructure(county.getNetexId(), String.valueOf(county.getVersion()  )));
        municipality = topographicPlaceVersionedSaverService.saveNewVersion(municipality);

        TariffZone tariffZone = new TariffZone();
        tariffZone.setVersion(1L);
        tariffZone = tariffZoneRepository.save(tariffZone);

        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("stop place"));
        stopPlace.setTopographicPlace(municipality);
        stopPlace.getTariffZones().add(new TariffZoneRef(tariffZone));
        stopPlaceRepository.save(stopPlace);

        stopPlaceRepository.flush();

        PurposeOfGrouping purposeOfGrouping = new PurposeOfGrouping();
        purposeOfGrouping.setName(new EmbeddableMultilingualString("generalization"));
        purposeOfGroupingSaverService.saveNewVersion(purposeOfGrouping);

        GroupOfStopPlaces groupOfStopPlaces = new GroupOfStopPlaces(new EmbeddableMultilingualString("group"));
        groupOfStopPlaces.setPurposeOfGrouping(purposeOfGrouping);
        groupOfStopPlaces.getMembers().add(new StopPlaceReference(stopPlace.getNetexId()));
        groupOfStopPlacesSaverService.saveNewVersion(groupOfStopPlaces);

        ExportParams exportParams = ExportParams.newExportParamsBuilder()
                .setStopPlaceSearch(
                        StopPlaceSearch.newStopPlaceSearchBuilder()
                                .setVersionValidity(ExportParams.VersionValidity.CURRENT_FUTURE)
                                .build())
                .setTopographicPlaceExportMode(ExportParams.ExportMode.NONE)
                .setTariffZoneExportMode(ExportParams.ExportMode.NONE)
                .setGroupOfStopPlacesExportMode(ExportParams.ExportMode.NONE)
                .build();

        streamingPublicationDelivery.stream(exportParams, byteArrayOutputStream);

        String xml = byteArrayOutputStream.toString();
        System.out.println(xml);

        netexXmlReferenceValidator.validateNetexReferences(new ByteArrayInputStream(xml.getBytes()), "publicationDelivery");
    }

    @Test
    public void streamStopPlacesAndRelatedEntitiesIntoPublicationDelivery() throws Exception {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        TopographicPlace topographicPlace = new TopographicPlace(new EmbeddableMultilingualString("Some municipality"));
        topographicPlace.setTopographicPlaceType(TopographicPlaceTypeEnumeration.MUNICIPALITY);
        topographicPlace = topographicPlaceVersionedSaverService.saveNewVersion(topographicPlace);

        String tariffZoneId = "CRI:TariffZone:1";

        TariffZone tariffZoneV1 = new TariffZone();
        tariffZoneV1.setNetexId(tariffZoneId);
        tariffZoneV1.setVersion(1L);
        var zonedDateTime = ZonedDateTime.of(2020, 12, 01, 00, 00, 00, 000, ZoneId.systemDefault());
        Instant fromDate = zonedDateTime.toInstant();
        tariffZoneV1.setValidBetween(new ValidBetween(fromDate,null));
        tariffZoneV1 = tariffZoneRepository.save(tariffZoneV1);

        TariffZone tariffZoneV2 = new TariffZone();
        tariffZoneV2.setNetexId(tariffZoneId);
        tariffZoneV2.setVersion(2L);
        var fromDate2 = zonedDateTime.plusDays(1L).toInstant();
        tariffZoneV2.setValidBetween(new ValidBetween(fromDate2,null));
        tariffZoneV2 = tariffZoneRepository.save(tariffZoneV2);

        TariffZone tariffZoneV3 = new TariffZone();
        tariffZoneV3.setNetexId(tariffZoneId);
        tariffZoneV3.setVersion(3L);
        var fromDate3 = zonedDateTime.plusDays(2L).toInstant();
        tariffZoneV3.setValidBetween(new ValidBetween(fromDate3,null));
        tariffZoneV3 = tariffZoneRepository.save(tariffZoneV3);

        StopPlace stopPlace1 = new StopPlace(new EmbeddableMultilingualString("stop place in publication delivery"));
        stopPlace1.getTariffZones().add(new TariffZoneRef(tariffZoneId)); // Without version, implicity v3
        stopPlace1 = stopPlaceVersionedSaverService.saveNewVersion(stopPlace1);
        final String stopPlace1NetexId = stopPlace1.getNetexId();

        StopPlace stopPlace2 = new StopPlace(new EmbeddableMultilingualString("another stop place in publication delivery"));
        // StopPlace 2 refers to tariffzone version 1. This must be included in the publication delivery, allthough v2 and v3 exists
        stopPlace2.getTariffZones().add(new TariffZoneRef(tariffZoneV1));
        stopPlace2 = stopPlaceVersionedSaverService.saveNewVersion(stopPlace2);
        final String stopPlace2NetexId = stopPlace2.getNetexId();

        PurposeOfGrouping purposeOfGrouping = new PurposeOfGrouping();
        purposeOfGrouping.setName(new EmbeddableMultilingualString("generalization"));
        purposeOfGroupingSaverService.saveNewVersion(purposeOfGrouping);

        GroupOfStopPlaces groupOfStopPlaces1 = new GroupOfStopPlaces(new EmbeddableMultilingualString("group of stop places"));
        groupOfStopPlaces1.setPurposeOfGrouping(purposeOfGrouping);
        groupOfStopPlaces1.getMembers().add(new StopPlaceReference(stopPlace1.getNetexId()));

        groupOfStopPlacesSaverService.saveNewVersion(groupOfStopPlaces1);

        GroupOfStopPlaces groupOfStopPlaces2 = new GroupOfStopPlaces(new EmbeddableMultilingualString("group of stop places number two"));
        groupOfStopPlaces2.setPurposeOfGrouping(purposeOfGrouping);
        groupOfStopPlaces2.getMembers().add(new StopPlaceReference(stopPlace1.getNetexId()));

        groupOfStopPlacesSaverService.saveNewVersion(groupOfStopPlaces2);

        groupOfStopPlacesRepository.flush();

        // Allows setting topographic place without lookup.
        // To have the lookup work, topographic place polygon must exist
        stopPlace1.setTopographicPlace(topographicPlace);
        stopPlaceRepository.save(stopPlace1);

        stopPlaceRepository.flush();
        tariffZoneRepository.flush();

        ExportParams exportParams = ExportParams.newExportParamsBuilder()
                .setStopPlaceSearch(
                        StopPlaceSearch.newStopPlaceSearchBuilder()
                                .setVersionValidity(ExportParams.VersionValidity.CURRENT_FUTURE)
                                .build())
                .setTopographicPlaceExportMode(ExportParams.ExportMode.RELEVANT)
                .setTariffZoneExportMode(ExportParams.ExportMode.RELEVANT)
                .setGroupOfStopPlacesExportMode(ExportParams.ExportMode.RELEVANT)
                .build();

        streamingPublicationDelivery.stream(exportParams, byteArrayOutputStream);

        String xml = byteArrayOutputStream.toString();

        System.out.println(xml);

        // The unmarshaller will validate as well. But only if validateAgainstSchema is true
        validate(xml);
        // Validate using own implementation of netex xml reference validator
        netexXmlReferenceValidator.validateNetexReferences(new ByteArrayInputStream(xml.getBytes()), "publicationDelivery");

        PublicationDeliveryStructure publicationDeliveryStructure = publicationDeliveryUnmarshaller.unmarshal(new ByteArrayInputStream(xml.getBytes()));

        org.rutebanken.netex.model.SiteFrame siteFrame = publicationDeliveryHelper.findSiteFrame(publicationDeliveryStructure);
        List<org.rutebanken.netex.model.StopPlace> stops = siteFrame.getStopPlaces().getStopPlace_().stream()
                .map(sp -> (org.rutebanken.netex.model.StopPlace) sp.getValue())
                .toList();

        assertThat(stops)
                .hasSize(2)
                .as("stops expected")
                .extracting(org.rutebanken.netex.model.StopPlace::getId)
                .containsOnly(stopPlace1.getNetexId(), stopPlace2.getNetexId());


        // Make sure both stops have references to tariff zones and with correct version
        org.rutebanken.netex.model.StopPlace actualStopPlace1 = stops.stream().filter(sp -> sp.getId().equals(stopPlace1NetexId)).findFirst().get();

        assertThat(actualStopPlace1.getTariffZones())
                .as("actual stop place 1 tariff zones")
                .isNotNull();

        org.rutebanken.netex.model.TariffZoneRef actualTariffZoneRefStopPlace1 = actualStopPlace1.getTariffZones().getTariffZoneRef_().stream()
                .map(tf -> (org.rutebanken.netex.model.TariffZoneRef) tf.getValue())
                .toList()
                .getFirst();

        // Stop place 1 refers to tariff zone v2 implicity beacuse the reference does not contain version value.
        assertThat(actualTariffZoneRefStopPlace1.getRef())
                .as("actual stop place 1 tariff zone ref")
                .isEqualTo(tariffZoneId);

        // Check stop place 2

        org.rutebanken.netex.model.StopPlace actualStopPlace2 = stops.stream().filter(sp -> sp.getId().equals(stopPlace2NetexId)).findFirst().get();
        org.rutebanken.netex.model.TariffZoneRef actualTariffZoneRefStopPlace2 = actualStopPlace2.getTariffZones().getTariffZoneRef_().stream()
                .map(tf -> (org.rutebanken.netex.model.TariffZoneRef) tf.getValue())
                .toList()
                .getFirst();

        assertThat(actualTariffZoneRefStopPlace2.getRef())
                .as("actual stop place 2 tariff zone ref")
                .isEqualTo(tariffZoneId);

        assertThat(actualTariffZoneRefStopPlace2.getVersion())
                .as("actual tariff zone ref for stop place 2 should point to version 1 of tariff zone")
                .isEqualTo(String.valueOf(tariffZoneV1.getVersion()));

        // Check topographic places

        assertThat(siteFrame.getTopographicPlaces()).isNotNull();
        assertThat(siteFrame.getTopographicPlaces().getTopographicPlace())
                .as("site fra topopgraphic places")
                .isNotNull()
                .hasSize(1)
                .extracting(org.rutebanken.netex.model.TopographicPlace::getId)
                .containsOnly(topographicPlace.getNetexId());

        // Check tariff zones

        assertThat(siteFrame.getTariffZones())
                .as("site fra tariff zones")
                .isNotNull();

        assertThat(siteFrame.getTariffZones().getTariffZone())
                .extracting(tariffZone -> tariffZone.getValue().getId() + "-" + tariffZone.getValue().getVersion())
                .as("Both tariff zones exists in publication delivery. But not the one not being reffered to (v2)")
               .containsOnly(tariffZoneId + "-" + 1, tariffZoneId + "-" + 3);





    }

    /**
     * Reproduce ROR-277, missing current stop place, when there is a future version.
     */
    @Test
    public void keepCurrentVersionOfStopPlaceWhenFutureVersionExist() throws InterruptedException, IOException, XMLStreamException, SAXException, JAXBException {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        StopPlace stopPlacev1 = new StopPlace(new EmbeddableMultilingualString("name"));
        stopPlacev1 = stopPlaceVersionedSaverService.saveNewVersion(stopPlacev1);
        StopPlace stopPlacev2 = versionCreator.createCopy(stopPlacev1, StopPlace.class);

        stopPlacev2.setValidBetween(new ValidBetween(Instant.now().plus(10, ChronoUnit.DAYS)));

        stopPlaceVersionedSaverService.saveNewVersion(stopPlacev1, stopPlacev2);

        stopPlaceRepository.flush();

        ExportParams exportParams = ExportParams.newExportParamsBuilder()
                .setStopPlaceSearch(
                        StopPlaceSearch.newStopPlaceSearchBuilder()
                                .setVersionValidity(ExportParams.VersionValidity.CURRENT_FUTURE)
                                .build())
                .setTopographicPlaceExportMode(ExportParams.ExportMode.NONE)
                .setTariffZoneExportMode(ExportParams.ExportMode.NONE)
                .build();

        streamingPublicationDelivery.stream(exportParams, byteArrayOutputStream);

        PublicationDeliveryStructure publicationDeliveryStructure = publicationDeliveryUnmarshaller.unmarshal(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));

        List<org.rutebanken.netex.model.StopPlace> stopPlaces = publicationDeliveryTestHelper.extractStopPlaces(publicationDeliveryStructure);
        assertThat(stopPlaces).hasSize(2);
    }

    private void validate(String xml) throws JAXBException, IOException, SAXException {
        JAXBContext publicationDeliveryContext = newInstance(PublicationDeliveryStructure.class);
        Unmarshaller unmarshaller = publicationDeliveryContext.createUnmarshaller();

        NeTExValidator neTExValidator =  NeTExValidator.getNeTExValidator();
        unmarshaller.setSchema(neTExValidator.getSchema());
        unmarshaller.unmarshal(new StringReader(xml));
    }

    /**
     * Test that a TopographicPlace with ONLY multiSurface (no polygon) exports valid NeTEx.
     *
     * IMPORTANT FINDING: The NeTEx XSD schema defines polygon and multiSurface as a CHOICE group,
     * meaning you can have ONE or the OTHER, but NOT BOTH simultaneously.
     *
     * This test verifies that multiSurface-only export produces valid NeTEx XML.
     *
     * BREAKING CHANGE IMPLICATION: Clients that only parse gml:Polygon will NOT see geometry
     * for zones that use multiSurface. This is a potential breaking change for legacy clients.
     */
    @Test
    public void exportTopographicPlaceWithMultiSurfaceOnly() throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        // Create two separate polygons for the multiSurface property (disconnected areas)
        Coordinate[] multiPolygon1Coordinates = new Coordinate[] {
                new Coordinate(11.0, 61.0),
                new Coordinate(12.0, 61.0),
                new Coordinate(12.0, 62.0),
                new Coordinate(11.0, 62.0),
                new Coordinate(11.0, 61.0)  // Close the ring
        };
        LinearRing multiPolygon1Ring = new LinearRing(new CoordinateArraySequence(multiPolygon1Coordinates), geometryFactory);
        Polygon multiPolygon1 = geometryFactory.createPolygon(multiPolygon1Ring, null);

        Coordinate[] multiPolygon2Coordinates = new Coordinate[] {
                new Coordinate(13.0, 63.0),
                new Coordinate(14.0, 63.0),
                new Coordinate(14.0, 64.0),
                new Coordinate(13.0, 64.0),
                new Coordinate(13.0, 63.0)  // Close the ring
        };
        LinearRing multiPolygon2Ring = new LinearRing(new CoordinateArraySequence(multiPolygon2Coordinates), geometryFactory);
        Polygon multiPolygon2 = geometryFactory.createPolygon(multiPolygon2Ring, null);

        MultiPolygon multiSurface = geometryFactory.createMultiPolygon(new Polygon[] { multiPolygon1, multiPolygon2 });

        // Create TopographicPlace with ONLY multiSurface (no polygon)
        TopographicPlace topographicPlace = new TopographicPlace(new EmbeddableMultilingualString("Place with multiSurface only"));
        topographicPlace.setTopographicPlaceType(TopographicPlaceTypeEnumeration.MUNICIPALITY);
        topographicPlace.setMultiSurface(multiSurface);
        // Note: polygon is NOT set - this is the key difference
        topographicPlace = topographicPlaceVersionedSaverService.saveNewVersion(topographicPlace);

        // Create a stop place referencing this topographic place
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("Stop in multi-surface place"));
        stopPlace.setTopographicPlace(topographicPlace);
        stopPlaceRepository.save(stopPlace);
        stopPlaceRepository.flush();

        // Export with topographic places - use exportMultiSurface=true to export multiSurface
        ExportParams exportParams = ExportParams.newExportParamsBuilder()
                .setStopPlaceSearch(
                        StopPlaceSearch.newStopPlaceSearchBuilder()
                                .setVersionValidity(ExportParams.VersionValidity.CURRENT_FUTURE)
                                .build())
                .setTopographicPlaceExportMode(ExportParams.ExportMode.ALL)
                .setTariffZoneExportMode(ExportParams.ExportMode.NONE)
                .setExportMultiSurface(true)
                .build();

        streamingPublicationDelivery.stream(exportParams, byteArrayOutputStream);

        String xml = byteArrayOutputStream.toString();

        // Validate against NeTEx schema - multiSurface alone should be valid
        validate(xml);

        // Validate internal NeTEx references
        netexXmlReferenceValidator.validateNetexReferences(new ByteArrayInputStream(xml.getBytes()), "publicationDelivery");

        // Parse and verify the exported structure
        PublicationDeliveryStructure publicationDeliveryStructure = publicationDeliveryUnmarshaller.unmarshal(
                new ByteArrayInputStream(xml.getBytes()));

        org.rutebanken.netex.model.SiteFrame siteFrame = publicationDeliveryHelper.findSiteFrame(publicationDeliveryStructure);

        assertThat(siteFrame.getTopographicPlaces())
                .as("TopographicPlaces should not be null")
                .isNotNull();

        assertThat(siteFrame.getTopographicPlaces().getTopographicPlace())
                .as("Should have one topographic place")
                .hasSize(1);

        org.rutebanken.netex.model.TopographicPlace exportedPlace = siteFrame.getTopographicPlaces().getTopographicPlace().getFirst();

        assertThat(exportedPlace.getId())
                .as("Exported topographic place ID")
                .isEqualTo(topographicPlace.getNetexId());

        // Verify polygon is NOT present (mutually exclusive with multiSurface)
        assertThat(exportedPlace.getPolygon())
                .as("Polygon should be null when multiSurface is used")
                .isNull();

        // Verify multiSurface is exported
        assertThat(exportedPlace.getMultiSurface())
                .as("MultiSurface should be exported")
                .isNotNull();

        assertThat(exportedPlace.getMultiSurface().getSurfaceMember())
                .as("MultiSurface should have 2 surface members (polygons)")
                .hasSize(2);

        // Verify the XML structure (namespace prefix may be gml: or ns2: depending on JAXB)
        assertThat(xml)
                .as("XML should contain MultiSurface element")
                .containsPattern("(gml:|ns2:)MultiSurface");

        assertThat(xml)
                .as("XML should contain surfaceMember elements")
                .containsPattern("(gml:|ns2:)surfaceMember");
    }

    /**
     * Test that a TopographicPlace with ONLY polygon (no multiSurface) exports valid NeTEx.
     * This is the traditional/existing behavior that should continue to work.
     */
    @Test
    public void exportTopographicPlaceWithPolygonOnly() throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        // Create a single polygon
        Coordinate[] polygonCoordinates = new Coordinate[] {
                new Coordinate(9.0, 59.0),
                new Coordinate(10.0, 59.0),
                new Coordinate(10.0, 60.0),
                new Coordinate(9.0, 60.0),
                new Coordinate(9.0, 59.0)  // Close the ring
        };
        LinearRing polygonRing = new LinearRing(new CoordinateArraySequence(polygonCoordinates), geometryFactory);
        Polygon polygon = geometryFactory.createPolygon(polygonRing, null);

        // Create TopographicPlace with ONLY polygon (no multiSurface) - traditional behavior
        TopographicPlace topographicPlace = new TopographicPlace(new EmbeddableMultilingualString("Place with polygon only"));
        topographicPlace.setTopographicPlaceType(TopographicPlaceTypeEnumeration.MUNICIPALITY);
        topographicPlace.setPolygon(polygon);
        // Note: multiSurface is NOT set
        topographicPlace = topographicPlaceVersionedSaverService.saveNewVersion(topographicPlace);

        // Create a stop place referencing this topographic place
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("Stop in polygon place"));
        stopPlace.setTopographicPlace(topographicPlace);
        stopPlaceRepository.save(stopPlace);
        stopPlaceRepository.flush();

        // Export with topographic places
        ExportParams exportParams = ExportParams.newExportParamsBuilder()
                .setStopPlaceSearch(
                        StopPlaceSearch.newStopPlaceSearchBuilder()
                                .setVersionValidity(ExportParams.VersionValidity.CURRENT_FUTURE)
                                .build())
                .setTopographicPlaceExportMode(ExportParams.ExportMode.ALL)
                .setTariffZoneExportMode(ExportParams.ExportMode.NONE)
                .build();

        streamingPublicationDelivery.stream(exportParams, byteArrayOutputStream);

        String xml = byteArrayOutputStream.toString();
        System.out.println("Exported NeTEx with polygon only:");
        System.out.println(xml);

        // Validate against NeTEx schema
        validate(xml);

        // Validate internal NeTEx references
        netexXmlReferenceValidator.validateNetexReferences(new ByteArrayInputStream(xml.getBytes()), "publicationDelivery");

        // Parse and verify the exported structure
        PublicationDeliveryStructure publicationDeliveryStructure = publicationDeliveryUnmarshaller.unmarshal(
                new ByteArrayInputStream(xml.getBytes()));

        org.rutebanken.netex.model.SiteFrame siteFrame = publicationDeliveryHelper.findSiteFrame(publicationDeliveryStructure);

        org.rutebanken.netex.model.TopographicPlace exportedPlace = siteFrame.getTopographicPlaces().getTopographicPlace().getFirst();

        // Verify polygon is present
        assertThat(exportedPlace.getPolygon())
                .as("Polygon should be exported")
                .isNotNull();

        assertThat(exportedPlace.getPolygon().getExterior())
                .as("Polygon exterior ring should be present")
                .isNotNull();

        // Verify multiSurface is NOT present
        assertThat(exportedPlace.getMultiSurface())
                .as("MultiSurface should be null when polygon is used")
                .isNull();

        // Verify the XML structure (namespace prefix may be gml: or ns2: depending on JAXB)
        assertThat(xml)
                .as("XML should contain Polygon element")
                .containsPattern("(gml:|ns2:)Polygon");
    }

    /**
     * Test that when a TopographicPlace has BOTH polygon AND multiSurface,
     * the export correctly handles this based on exportMultiSurface flag.
     *
     * NeTEx XSD defines Zone_VersionStructure geometry as a choice (mutually exclusive):
     *   <xs:choice minOccurs="0">
     *     <xs:element ref="gml:Polygon"/>
     *     <xs:element ref="gml:MultiSurface"/>
     *   </xs:choice>
     *
     * The exportMultiSurface parameter controls which is exported:
     * - exportMultiSurface=false (default): exports polygon, clears multiSurface
     * - exportMultiSurface=true: exports multiSurface, clears polygon
     */
    @Test
    public void exportTopographicPlaceWithBothPolygonAndMultiSurfaceUsesExportFlag() throws Exception {
        // Create a single polygon
        Coordinate[] polygonCoordinates = new Coordinate[] {
                new Coordinate(9.0, 59.0),
                new Coordinate(10.0, 59.0),
                new Coordinate(10.0, 60.0),
                new Coordinate(9.0, 60.0),
                new Coordinate(9.0, 59.0)
        };
        LinearRing polygonRing = new LinearRing(new CoordinateArraySequence(polygonCoordinates), geometryFactory);
        Polygon polygon = geometryFactory.createPolygon(polygonRing, null);

        // Create multiSurface
        Coordinate[] multiPolygon1Coordinates = new Coordinate[] {
                new Coordinate(11.0, 61.0),
                new Coordinate(12.0, 61.0),
                new Coordinate(12.0, 62.0),
                new Coordinate(11.0, 62.0),
                new Coordinate(11.0, 61.0)
        };
        LinearRing multiPolygon1Ring = new LinearRing(new CoordinateArraySequence(multiPolygon1Coordinates), geometryFactory);
        Polygon multiPolygon1 = geometryFactory.createPolygon(multiPolygon1Ring, null);

        MultiPolygon multiSurface = geometryFactory.createMultiPolygon(new Polygon[] { multiPolygon1 });

        // Create TopographicPlace with BOTH polygon AND multiSurface
        TopographicPlace topographicPlace = new TopographicPlace(new EmbeddableMultilingualString("Place with both geometries"));
        topographicPlace.setTopographicPlaceType(TopographicPlaceTypeEnumeration.MUNICIPALITY);
        topographicPlace.setPolygon(polygon);
        topographicPlace.setMultiSurface(multiSurface);
        topographicPlace = topographicPlaceVersionedSaverService.saveNewVersion(topographicPlace);
        final String topographicPlaceNetexId = topographicPlace.getNetexId();

        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("Stop"));
        stopPlace.setTopographicPlace(topographicPlace);
        stopPlaceRepository.save(stopPlace);
        stopPlaceRepository.flush();

        // Test 1: exportMultiSurface=false (default) - should export polygon
        ByteArrayOutputStream outputFalse = new ByteArrayOutputStream();
        ExportParams exportParamsFalse = ExportParams.newExportParamsBuilder()
                .setStopPlaceSearch(
                        StopPlaceSearch.newStopPlaceSearchBuilder()
                                .setVersionValidity(ExportParams.VersionValidity.CURRENT_FUTURE)
                                .build())
                .setTopographicPlaceExportMode(ExportParams.ExportMode.ALL)
                .setTariffZoneExportMode(ExportParams.ExportMode.NONE)
                .setExportMultiSurface(false)
                .build();

        streamingPublicationDelivery.stream(exportParamsFalse, outputFalse);
        String xmlFalse = outputFalse.toString();
        validate(xmlFalse);

        PublicationDeliveryStructure pdFalse = publicationDeliveryUnmarshaller.unmarshal(new ByteArrayInputStream(xmlFalse.getBytes()));
        org.rutebanken.netex.model.SiteFrame siteFrameFalse = publicationDeliveryHelper.findSiteFrame(pdFalse);
        org.rutebanken.netex.model.TopographicPlace exportedPlaceFalse = siteFrameFalse.getTopographicPlaces()
                .getTopographicPlace().stream()
                .filter(tp -> tp.getId().equals(topographicPlaceNetexId))
                .findFirst()
                .orElseThrow();

        assertThat(exportedPlaceFalse.getPolygon())
                .as("With exportMultiSurface=false, polygon should be exported")
                .isNotNull();
        assertThat(exportedPlaceFalse.getMultiSurface())
                .as("With exportMultiSurface=false, multiSurface should be null")
                .isNull();

        // Test 2: exportMultiSurface=true - should export multiSurface
        ByteArrayOutputStream outputTrue = new ByteArrayOutputStream();
        ExportParams exportParamsTrue = ExportParams.newExportParamsBuilder()
                .setStopPlaceSearch(
                        StopPlaceSearch.newStopPlaceSearchBuilder()
                                .setVersionValidity(ExportParams.VersionValidity.CURRENT_FUTURE)
                                .build())
                .setTopographicPlaceExportMode(ExportParams.ExportMode.ALL)
                .setTariffZoneExportMode(ExportParams.ExportMode.NONE)
                .setExportMultiSurface(true)
                .build();

        streamingPublicationDelivery.stream(exportParamsTrue, outputTrue);
        String xmlTrue = outputTrue.toString();
        validate(xmlTrue);

        PublicationDeliveryStructure pdTrue = publicationDeliveryUnmarshaller.unmarshal(new ByteArrayInputStream(xmlTrue.getBytes()));
        org.rutebanken.netex.model.SiteFrame siteFrameTrue = publicationDeliveryHelper.findSiteFrame(pdTrue);
        org.rutebanken.netex.model.TopographicPlace exportedPlaceTrue = siteFrameTrue.getTopographicPlaces()
                .getTopographicPlace().stream()
                .filter(tp -> tp.getId().equals(topographicPlaceNetexId))
                .findFirst()
                .orElseThrow();

        assertThat(exportedPlaceTrue.getPolygon())
                .as("With exportMultiSurface=true, polygon should be null")
                .isNull();
        assertThat(exportedPlaceTrue.getMultiSurface())
                .as("With exportMultiSurface=true, multiSurface should be exported")
                .isNotNull();
    }

    /**
     * Test the exportMultiSurface parameter with default value (false).
     * When exportMultiSurface=false:
     * - Places with polygon should export polygon
     * - Places with multiSurface should have multiSurface cleared (backward compatible)
     */
    @Test
    public void exportMultiSurfaceParameterDefaultFalse() throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        // Create TopographicPlace with polygon only
        Coordinate[] polygonCoordinates = new Coordinate[] {
                new Coordinate(9.0, 59.0),
                new Coordinate(10.0, 59.0),
                new Coordinate(10.0, 60.0),
                new Coordinate(9.0, 60.0),
                new Coordinate(9.0, 59.0)
        };
        LinearRing polygonRing = new LinearRing(new CoordinateArraySequence(polygonCoordinates), geometryFactory);
        Polygon polygon = geometryFactory.createPolygon(polygonRing, null);

        TopographicPlace polygonPlace = new TopographicPlace(new EmbeddableMultilingualString("Place with polygon"));
        polygonPlace.setTopographicPlaceType(TopographicPlaceTypeEnumeration.MUNICIPALITY);
        polygonPlace.setPolygon(polygon);
        polygonPlace = topographicPlaceVersionedSaverService.saveNewVersion(polygonPlace);
        final String polygonPlaceNetexId = polygonPlace.getNetexId();

        // Create TopographicPlace with multiSurface only
        Coordinate[] multiPolygon1Coordinates = new Coordinate[] {
                new Coordinate(11.0, 61.0),
                new Coordinate(12.0, 61.0),
                new Coordinate(12.0, 62.0),
                new Coordinate(11.0, 62.0),
                new Coordinate(11.0, 61.0)
        };
        LinearRing multiPolygon1Ring = new LinearRing(new CoordinateArraySequence(multiPolygon1Coordinates), geometryFactory);
        Polygon multiPolygon1 = geometryFactory.createPolygon(multiPolygon1Ring, null);

        Coordinate[] multiPolygon2Coordinates = new Coordinate[] {
                new Coordinate(13.0, 63.0),
                new Coordinate(14.0, 63.0),
                new Coordinate(14.0, 64.0),
                new Coordinate(13.0, 64.0),
                new Coordinate(13.0, 63.0)
        };
        LinearRing multiPolygon2Ring = new LinearRing(new CoordinateArraySequence(multiPolygon2Coordinates), geometryFactory);
        Polygon multiPolygon2 = geometryFactory.createPolygon(multiPolygon2Ring, null);

        MultiPolygon multiSurface = geometryFactory.createMultiPolygon(new Polygon[] { multiPolygon1, multiPolygon2 });

        TopographicPlace multiSurfacePlace = new TopographicPlace(new EmbeddableMultilingualString("Place with multiSurface"));
        multiSurfacePlace.setTopographicPlaceType(TopographicPlaceTypeEnumeration.COUNTY);
        multiSurfacePlace.setMultiSurface(multiSurface);
        multiSurfacePlace = topographicPlaceVersionedSaverService.saveNewVersion(multiSurfacePlace);
        final String multiSurfacePlaceNetexId = multiSurfacePlace.getNetexId();

        // Create stop places referencing these topographic places
        StopPlace stopPlace1 = new StopPlace(new EmbeddableMultilingualString("Stop in polygon place"));
        stopPlace1.setTopographicPlace(polygonPlace);
        stopPlaceRepository.save(stopPlace1);

        StopPlace stopPlace2 = new StopPlace(new EmbeddableMultilingualString("Stop in multiSurface place"));
        stopPlace2.setTopographicPlace(multiSurfacePlace);
        stopPlaceRepository.save(stopPlace2);
        stopPlaceRepository.flush();

        // Export with exportMultiSurface=false (default)
        ExportParams exportParams = ExportParams.newExportParamsBuilder()
                .setStopPlaceSearch(
                        StopPlaceSearch.newStopPlaceSearchBuilder()
                                .setVersionValidity(ExportParams.VersionValidity.CURRENT_FUTURE)
                                .build())
                .setTopographicPlaceExportMode(ExportParams.ExportMode.ALL)
                .setTariffZoneExportMode(ExportParams.ExportMode.NONE)
                .setExportMultiSurface(false)
                .build();

        streamingPublicationDelivery.stream(exportParams, byteArrayOutputStream);

        String xml = byteArrayOutputStream.toString();

        // Parse and verify
        PublicationDeliveryStructure publicationDeliveryStructure = publicationDeliveryUnmarshaller.unmarshal(
                new ByteArrayInputStream(xml.getBytes()));

        org.rutebanken.netex.model.SiteFrame siteFrame = publicationDeliveryHelper.findSiteFrame(publicationDeliveryStructure);

        assertThat(siteFrame.getTopographicPlaces().getTopographicPlace())
                .as("Should have two topographic places")
                .hasSize(2);

        // Find the polygon place
        org.rutebanken.netex.model.TopographicPlace exportedPolygonPlace = siteFrame.getTopographicPlaces()
                .getTopographicPlace().stream()
                .filter(tp -> tp.getId().equals(polygonPlaceNetexId))
                .findFirst()
                .orElseThrow();

        assertThat(exportedPolygonPlace.getPolygon())
                .as("Polygon place should have polygon exported")
                .isNotNull();
        assertThat(exportedPolygonPlace.getMultiSurface())
                .as("Polygon place should NOT have multiSurface")
                .isNull();

        // Find the multiSurface place - with exportMultiSurface=false, multiSurface should be cleared
        org.rutebanken.netex.model.TopographicPlace exportedMultiSurfacePlace = siteFrame.getTopographicPlaces()
                .getTopographicPlace().stream()
                .filter(tp -> tp.getId().equals(multiSurfacePlaceNetexId))
                .findFirst()
                .orElseThrow();

        assertThat(exportedMultiSurfacePlace.getMultiSurface())
                .as("MultiSurface place should NOT have multiSurface when exportMultiSurface=false")
                .isNull();
        // Polygon was never set, so it should also be null
        assertThat(exportedMultiSurfacePlace.getPolygon())
                .as("MultiSurface place has no polygon data, so polygon should be null")
                .isNull();
    }

    /**
     * Test the exportMultiSurface parameter with value true.
     * When exportMultiSurface=true:
     * - Places with multiSurface should export multiSurface (polygon cleared)
     * - Places with only polygon should still export polygon
     */
    @Test
    public void exportMultiSurfaceParameterTrue() throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        // Create TopographicPlace with polygon only
        Coordinate[] polygonCoordinates = new Coordinate[] {
                new Coordinate(9.0, 59.0),
                new Coordinate(10.0, 59.0),
                new Coordinate(10.0, 60.0),
                new Coordinate(9.0, 60.0),
                new Coordinate(9.0, 59.0)
        };
        LinearRing polygonRing = new LinearRing(new CoordinateArraySequence(polygonCoordinates), geometryFactory);
        Polygon polygon = geometryFactory.createPolygon(polygonRing, null);

        TopographicPlace polygonPlace = new TopographicPlace(new EmbeddableMultilingualString("Place with polygon for multiSurface test"));
        polygonPlace.setTopographicPlaceType(TopographicPlaceTypeEnumeration.MUNICIPALITY);
        polygonPlace.setPolygon(polygon);
        polygonPlace = topographicPlaceVersionedSaverService.saveNewVersion(polygonPlace);
        final String polygonPlaceNetexId = polygonPlace.getNetexId();

        // Create TopographicPlace with multiSurface only
        Coordinate[] multiPolygon1Coordinates = new Coordinate[] {
                new Coordinate(11.0, 61.0),
                new Coordinate(12.0, 61.0),
                new Coordinate(12.0, 62.0),
                new Coordinate(11.0, 62.0),
                new Coordinate(11.0, 61.0)
        };
        LinearRing multiPolygon1Ring = new LinearRing(new CoordinateArraySequence(multiPolygon1Coordinates), geometryFactory);
        Polygon multiPolygon1 = geometryFactory.createPolygon(multiPolygon1Ring, null);

        Coordinate[] multiPolygon2Coordinates = new Coordinate[] {
                new Coordinate(13.0, 63.0),
                new Coordinate(14.0, 63.0),
                new Coordinate(14.0, 64.0),
                new Coordinate(13.0, 64.0),
                new Coordinate(13.0, 63.0)
        };
        LinearRing multiPolygon2Ring = new LinearRing(new CoordinateArraySequence(multiPolygon2Coordinates), geometryFactory);
        Polygon multiPolygon2 = geometryFactory.createPolygon(multiPolygon2Ring, null);

        MultiPolygon multiSurface = geometryFactory.createMultiPolygon(new Polygon[] { multiPolygon1, multiPolygon2 });

        TopographicPlace multiSurfacePlace = new TopographicPlace(new EmbeddableMultilingualString("Place with multiSurface for export test"));
        multiSurfacePlace.setTopographicPlaceType(TopographicPlaceTypeEnumeration.COUNTY);
        multiSurfacePlace.setMultiSurface(multiSurface);
        multiSurfacePlace = topographicPlaceVersionedSaverService.saveNewVersion(multiSurfacePlace);
        final String multiSurfacePlaceNetexId = multiSurfacePlace.getNetexId();

        // Create stop places referencing these topographic places
        StopPlace stopPlace1 = new StopPlace(new EmbeddableMultilingualString("Stop in polygon place 2"));
        stopPlace1.setTopographicPlace(polygonPlace);
        stopPlaceRepository.save(stopPlace1);

        StopPlace stopPlace2 = new StopPlace(new EmbeddableMultilingualString("Stop in multiSurface place 2"));
        stopPlace2.setTopographicPlace(multiSurfacePlace);
        stopPlaceRepository.save(stopPlace2);
        stopPlaceRepository.flush();

        // Export with exportMultiSurface=true
        ExportParams exportParams = ExportParams.newExportParamsBuilder()
                .setStopPlaceSearch(
                        StopPlaceSearch.newStopPlaceSearchBuilder()
                                .setVersionValidity(ExportParams.VersionValidity.CURRENT_FUTURE)
                                .build())
                .setTopographicPlaceExportMode(ExportParams.ExportMode.ALL)
                .setTariffZoneExportMode(ExportParams.ExportMode.NONE)
                .setExportMultiSurface(true)
                .build();

        streamingPublicationDelivery.stream(exportParams, byteArrayOutputStream);

        String xml = byteArrayOutputStream.toString();

        // Validate against NeTEx schema
        validate(xml);

        // Parse and verify
        PublicationDeliveryStructure publicationDeliveryStructure = publicationDeliveryUnmarshaller.unmarshal(
                new ByteArrayInputStream(xml.getBytes()));

        org.rutebanken.netex.model.SiteFrame siteFrame = publicationDeliveryHelper.findSiteFrame(publicationDeliveryStructure);

        assertThat(siteFrame.getTopographicPlaces().getTopographicPlace())
                .as("Should have two topographic places")
                .hasSize(2);

        // Find the polygon-only place - should still have polygon when exportMultiSurface=true
        // because it doesn't have multiSurface data
        org.rutebanken.netex.model.TopographicPlace exportedPolygonPlace = siteFrame.getTopographicPlaces()
                .getTopographicPlace().stream()
                .filter(tp -> tp.getId().equals(polygonPlaceNetexId))
                .findFirst()
                .orElseThrow();

        assertThat(exportedPolygonPlace.getPolygon())
                .as("Polygon-only place should still have polygon when exportMultiSurface=true")
                .isNotNull();
        assertThat(exportedPolygonPlace.getMultiSurface())
                .as("Polygon-only place should NOT have multiSurface")
                .isNull();

        // Find the multiSurface place - should have multiSurface, no polygon
        org.rutebanken.netex.model.TopographicPlace exportedMultiSurfacePlace = siteFrame.getTopographicPlaces()
                .getTopographicPlace().stream()
                .filter(tp -> tp.getId().equals(multiSurfacePlaceNetexId))
                .findFirst()
                .orElseThrow();

        assertThat(exportedMultiSurfacePlace.getMultiSurface())
                .as("MultiSurface place should have multiSurface when exportMultiSurface=true")
                .isNotNull();
        assertThat(exportedMultiSurfacePlace.getMultiSurface().getSurfaceMember())
                .as("MultiSurface should have 2 surface members")
                .hasSize(2);
        assertThat(exportedMultiSurfacePlace.getPolygon())
                .as("MultiSurface place should NOT have polygon when exportMultiSurface=true")
                .isNull();

        // Verify the XML contains MultiSurface element
        assertThat(xml)
                .as("XML should contain MultiSurface element")
                .containsPattern("(gml:|ns2:)MultiSurface");
    }

}
