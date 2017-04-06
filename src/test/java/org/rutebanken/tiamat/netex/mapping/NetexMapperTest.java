package org.rutebanken.tiamat.netex.mapping;

import org.junit.Ignore;
import org.junit.Test;
import org.rutebanken.netex.model.*;
import org.rutebanken.netex.model.MultilingualString;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.model.*;
import org.rutebanken.tiamat.model.AccessibilityAssessment;
import org.rutebanken.tiamat.model.CountryRef;
import org.rutebanken.tiamat.model.IanaCountryTldEnumeration;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.SiteFrame;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.StopPlacesInFrame_RelStructure;
import org.rutebanken.tiamat.model.TopographicPlace;
import org.rutebanken.tiamat.model.TopographicPlaceRefStructure;
import org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper.ORIGINAL_ID_KEY;

public class NetexMapperTest extends TiamatIntegrationTest {

    @Autowired
    private NetexMapper netexMapper;

    @Test
    public void mapKeyValuesToInternalList() throws Exception {


        org.rutebanken.netex.model.StopPlace stopPlace = new org.rutebanken.netex.model.StopPlace()
                .withKeyList(
                        new KeyListStructure()
                                .withKeyValue(
                                        new KeyValueStructure()
                                                .withKey(ORIGINAL_ID_KEY)
                                                .withValue("KOL:StopPlace:123,BRA:StopPlace:123,RUT:StopPlace:123")
                                )
                );

        StopPlace tiamatStopPlace = netexMapper.mapToTiamatModel(stopPlace);


        assertThat(tiamatStopPlace.getOriginalIds())
                .hasSize(3);

    }


    @Test
    public void mapSiteFrameToNetexModel() throws Exception {
        org.rutebanken.tiamat.model.SiteFrame sourceSiteFrame = new org.rutebanken.tiamat.model.SiteFrame();

        StopPlacesInFrame_RelStructure stopPlacesInFrame_relStructure = new StopPlacesInFrame_RelStructure();

        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new EmbeddableMultilingualString("name", "en"));

        stopPlacesInFrame_relStructure.getStopPlace().add(stopPlace);

        sourceSiteFrame.setStopPlaces(stopPlacesInFrame_relStructure);

        org.rutebanken.netex.model.SiteFrame netexSiteFrame = netexMapper.mapToNetexModel(sourceSiteFrame);

        assertThat(netexSiteFrame).isNotNull();
        assertThat(netexSiteFrame.getStopPlaces().getStopPlace().get(0).getName().getValue()).isEqualTo(stopPlace.getName().getValue());
    }

    @Test
    public void mapSiteFrameToInternalModel() throws Exception {
        org.rutebanken.netex.model.SiteFrame netexSiteFrame = new org.rutebanken.netex.model.SiteFrame();

        org.rutebanken.netex.model.StopPlacesInFrame_RelStructure stopPlacesInFrame_relStructure = new org.rutebanken.netex.model.StopPlacesInFrame_RelStructure();

        org.rutebanken.netex.model.StopPlace stopPlace = new org.rutebanken.netex.model.StopPlace();
        org.rutebanken.netex.model.MultilingualString name = new org.rutebanken.netex.model.MultilingualString();
        name.setValue("stop place");
        name.setLang("no");
        name.setTextIdType("");
        stopPlace.setName(name);
        String stopPlaceId = "1337";
        stopPlace.setId("AVI:StopPlace:" + stopPlaceId);

        stopPlacesInFrame_relStructure.getStopPlace().add(stopPlace);
        netexSiteFrame.setStopPlaces(stopPlacesInFrame_relStructure);

        org.rutebanken.tiamat.model.SiteFrame actualSiteFrame = netexMapper.mapToTiamatModel(netexSiteFrame);

        assertThat(actualSiteFrame).isNotNull();
        assertThat(actualSiteFrame.getStopPlaces().getStopPlace().get(0).getName().getValue()).isEqualTo(stopPlace.getName().getValue());
    }


    @Test
    public void mapStopPlaceWithNameAndTopographicPlaceRefToNetex() throws Exception {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setNetexId("stopplacenetexid");
        stopPlace.setName(new EmbeddableMultilingualString("name", "en"));
        stopPlace.setVersion(1L);

        TopographicPlace topographicPlace = new TopographicPlace(new EmbeddableMultilingualString("Asker"));
        topographicPlace.setVersion(1L);
        topographicPlace.setNetexId("netexidfortopoplace");
        stopPlace.setTopographicPlace(topographicPlace);

        org.rutebanken.netex.model.StopPlace netexStopPlace = netexMapper.mapToNetexModel(stopPlace);

        assertThat(netexStopPlace).isNotNull();
        assertThat(netexStopPlace.getName()).isNotNull();
        assertThat(netexStopPlace.getName().getValue()).isEqualTo(stopPlace.getName().getValue());
        assertThat(netexStopPlace.getTopographicPlaceRef()).isNotNull();
        assertThat(netexStopPlace.getTopographicPlaceRef().getRef()).isEqualTo(topographicPlace.getNetexId());
        assertThat(netexStopPlace.getTopographicPlaceRef().getVersion()).isEqualTo(String.valueOf(topographicPlace.getVersion()));
    }

    @Ignore
    @Test
    public void mapStopPlaceToInternalWithId() throws Exception {
        org.rutebanken.netex.model.StopPlace netexStopPlace = new org.rutebanken.netex.model.StopPlace();
        String stopPlaceId = "1339";
        netexStopPlace.setId("NSR:StopPlace:" + stopPlaceId);

        org.rutebanken.tiamat.model.StopPlace tiamatStopPlace = netexMapper.mapToTiamatModel(netexStopPlace);

        assertThat(tiamatStopPlace).isNotNull();
        assertThat(tiamatStopPlace.getNetexId()).isEqualTo(stopPlaceId);
    }

    @Test
    public void mapStopPlaceWithKeyValuesToNetex() throws Exception {

        StopPlace stopPlace = new StopPlace();

        String originalId = "OPP:StopArea:123";


        stopPlace.getKeyValues().put(ORIGINAL_ID_KEY, new Value(originalId));

        org.rutebanken.netex.model.StopPlace netexStopPlace = netexMapper.mapToNetexModel(stopPlace);
        assertThat(netexStopPlace.getKeyList()).isNotNull();
        assertThat(netexStopPlace.getKeyList().getKeyValue()).isNotNull();
        assertThat(netexStopPlace.getKeyList().getKeyValue()).isNotEmpty();
        assertThat(netexStopPlace.getKeyList().getKeyValue()).extracting("key").contains(ORIGINAL_ID_KEY);
        assertThat(netexStopPlace.getKeyList().getKeyValue()).extracting("value").contains(originalId);
    }

    /**
     * Usually, a stop place's ID field will be moved to key value.
     * But when the stop place already has key values, we should map them to tiamat's keyValues.
     */
    @Test
    public void mapStopPlaceWithKeyValuesToTiamat() throws Exception {
        String originalId = "OPP:StopArea:123";
        org.rutebanken.netex.model.StopPlace netexStopPlace = new org.rutebanken.netex.model.StopPlace()
                .withKeyList(
                        new KeyListStructure().withKeyValue(
                                new KeyValueStructure()
                                        .withKey(ORIGINAL_ID_KEY)
                                        .withValue(originalId)));

        StopPlace tiamatStopPlace = netexMapper.mapToTiamatModel(netexStopPlace);
        assertThat(tiamatStopPlace.getKeyValues()).isNotNull();
        assertThat(tiamatStopPlace.getKeyValues()).containsKey(ORIGINAL_ID_KEY);
        assertThat(tiamatStopPlace.getKeyValues().get(ORIGINAL_ID_KEY).getItems().contains(originalId));
    }

    @Test
    public void mapStopPlaceToInternalWithName() throws Exception {
        org.rutebanken.netex.model.StopPlace netexStopPlace = new org.rutebanken.netex.model.StopPlace();
        org.rutebanken.netex.model.MultilingualString name = new org.rutebanken.netex.model.MultilingualString();
        name.setValue("stop place ");
        name.setLang("no");
        name.setTextIdType("");
        netexStopPlace.setName(name);

        org.rutebanken.tiamat.model.StopPlace tiamatStopPlace = netexMapper.mapToTiamatModel(netexStopPlace);

        assertThat(tiamatStopPlace).isNotNull();
        assertThat(tiamatStopPlace.getName().getValue()).isEqualTo(netexStopPlace.getName().getValue());

    }

    @Test
    public void mapStopPlaceInternalIdToNetexId() {
        StopPlace tiamatStopPlace = new StopPlace();
        tiamatStopPlace.setNetexId("NSR:StopPlace:123456");

        org.rutebanken.netex.model.StopPlace netexStopPlace = netexMapper.mapToNetexModel(tiamatStopPlace);

        assertThat(netexStopPlace.getId()).isEqualTo("NSR:StopPlace:123456");
    }

    /**
     * Ignored because the logic for handling incoming NSR IDs must be implemented differently.
     */
    @Ignore
    @Test
    public void mapNetexQuayIdToInternal() {
        org.rutebanken.netex.model.Quay netexQuay = new org.rutebanken.netex.model.Quay();

        String netexId = "NSR:Quay:12345";
        netexQuay.setId(netexId);

        org.rutebanken.tiamat.model.Quay tiamatQuay = netexMapper.mapToTiamatModel(netexQuay);

        assertThat(tiamatQuay.getNetexId()).isEqualTo("NSR:Quay:12345");
    }

    @Test
    public void mapInternalQuayIdToNetex() {

        org.rutebanken.tiamat.model.Quay tiamatQuay = new org.rutebanken.tiamat.model.Quay();
        String netexId = "NSR:Quay:" + 1234567;
        tiamatQuay.setNetexId(netexId);

        org.rutebanken.netex.model.Quay netexQuay = netexMapper.mapToNetexModel(tiamatQuay);
        assertThat(netexQuay.getId()).isNotNull();
        assertThat(netexQuay.getId()).isEqualTo(netexId);
    }

    @Test
    public void mapStopPlaceWithQuayToNetex() {
        org.rutebanken.tiamat.model.StopPlace stopPlace = new StopPlace();

        String netexId = "NSR:Quay:" + 1234567;
        org.rutebanken.tiamat.model.Quay tiamatQuay = new org.rutebanken.tiamat.model.Quay();
        tiamatQuay.setNetexId(netexId);

        stopPlace.getQuays().add(tiamatQuay);

        org.rutebanken.netex.model.StopPlace actualStop = netexMapper.mapToNetexModel(stopPlace);

        org.rutebanken.netex.model.Quay actualQuay = actualStop.getQuays().getQuayRefOrQuay().stream()
                .filter(object -> object instanceof org.rutebanken.netex.model.Quay)
                .map(object -> ((org.rutebanken.netex.model.Quay) object))
                .findFirst()
                .get();

        assertThat(actualQuay.getId()).isEqualTo(netexId);

    }

    @Test
    public void countryRefMappedToNetex() {
        SiteFrame tiamatSiteFrame = new SiteFrame();
        TopographicPlace topographicPlace = new TopographicPlace();

        topographicPlace.setNetexId("1");
        CountryRef countryRef = new CountryRef();
        countryRef.setRef(IanaCountryTldEnumeration.ZM);
        topographicPlace.setCountryRef(countryRef);

        tiamatSiteFrame
                .getTopographicPlaces()
                .getTopographicPlace()
                .add(topographicPlace);

        org.rutebanken.netex.model.SiteFrame netexSiteFrame = netexMapper.mapToNetexModel(tiamatSiteFrame);


        assertThat(netexSiteFrame).isNotNull();
        assertThat(netexSiteFrame.getTopographicPlaces().getTopographicPlace()).isNotEmpty();

        org.rutebanken.netex.model.TopographicPlace netexTopographicPlace = netexSiteFrame.getTopographicPlaces().getTopographicPlace().get(0);
        assertThat(netexTopographicPlace.getCountryRef()).as("Reference to country shall not be null").isNotNull();
        assertThat(netexTopographicPlace.getCountryRef().getRef()).isEqualTo(org.rutebanken.netex.model.IanaCountryTldEnumeration.ZM);


    }

    @Test
    public void mapCountyRefsFromMunicipalitiesFromTiamatToNetex() {
        SiteFrame tiamatSiteFrame = new SiteFrame();

        CountryRef countryRef = new CountryRef();
        countryRef.setRef(IanaCountryTldEnumeration.ZM);

        TopographicPlace county = new TopographicPlace(new EmbeddableMultilingualString("Akershus"));
        county.setCountryRef(countryRef);
        county.setNetexId("1L");

        TopographicPlace municipality = new TopographicPlace(new EmbeddableMultilingualString("Asker"));
        municipality.setParentTopographicPlaceRef(new TopographicPlaceRefStructure(county));
        municipality.setNetexId("2L");

        tiamatSiteFrame
                .getTopographicPlaces()
                .getTopographicPlace()
                .add(municipality);

        tiamatSiteFrame
                .getTopographicPlaces()
                .getTopographicPlace()
                .add(county);

        org.rutebanken.netex.model.SiteFrame netexSiteFrame = netexMapper.mapToNetexModel(tiamatSiteFrame);


        assertThat(netexSiteFrame).isNotNull();
        assertThat(netexSiteFrame.getTopographicPlaces().getTopographicPlace()).isNotEmpty();

        org.rutebanken.netex.model.TopographicPlace netexMunicipality = netexSiteFrame.getTopographicPlaces().getTopographicPlace().get(0);
        assertThat(netexMunicipality).isNotNull();
        assertThat(netexMunicipality.getParentTopographicPlaceRef()).describedAs("The municipality should have a reference to the parent topographic place").isNotNull();
        assertThat(netexMunicipality.getParentTopographicPlaceRef().getRef()).isEqualTo(county.getNetexId());
    }

    @Test
    public void  mapCountyRefsFromMunicipalitiesFromNetexToTiamat() {
        org.rutebanken.netex.model.SiteFrame netexSiteFrame = new org.rutebanken.netex.model.SiteFrame();
        netexSiteFrame.withTopographicPlaces(new TopographicPlacesInFrame_RelStructure());

        org.rutebanken.netex.model.CountryRef countryRef = new org.rutebanken.netex.model.CountryRef();
        countryRef.setRef(org.rutebanken.netex.model.IanaCountryTldEnumeration.ZM);

        org.rutebanken.netex.model.TopographicPlace county = new org.rutebanken.netex.model.TopographicPlace();
        county.setId("NSR:TopographicPlace:1");
        county.setName(new MultilingualString().withValue("Akershus"));
        county.withCountryRef(countryRef);

        org.rutebanken.netex.model.TopographicPlace municipality = new org.rutebanken.netex.model.TopographicPlace();
        municipality.setId("NSR:TopographicPlace:2");
        municipality.setName(new MultilingualString().withValue("Asker"));
        municipality.withParentTopographicPlaceRef(
                new org.rutebanken.netex.model.TopographicPlaceRefStructure().withRef(county.getId())
        );

        netexSiteFrame
                .getTopographicPlaces()
                .getTopographicPlace()
                .add(municipality);

        netexSiteFrame
                .getTopographicPlaces()
                .getTopographicPlace()
                .add(county);

        // To be able to look up NSR references, we need to persist municipality and county
        TopographicPlace tiamatCounty = new TopographicPlace(new EmbeddableMultilingualString(county.getName().getValue()));
        tiamatCounty.setNetexId(county.getId());
        topographicPlaceRepository.save(tiamatCounty);

        TopographicPlace tiamatMunicipality = new TopographicPlace(new EmbeddableMultilingualString(municipality.getName().getValue()));
        tiamatMunicipality.setNetexId(municipality.getId());
        tiamatMunicipality.setParentTopographicPlaceRef(new TopographicPlaceRefStructure(tiamatCounty));
        topographicPlaceRepository.save(tiamatMunicipality);


        SiteFrame tiamatSiteFrame = netexMapper.mapToTiamatModel(netexSiteFrame);

        assertThat(netexSiteFrame).isNotNull();
        assertThat(netexSiteFrame.getTopographicPlaces().getTopographicPlace()).isNotEmpty();

        TopographicPlace actualTiamatMunicipality = tiamatSiteFrame.getTopographicPlaces().getTopographicPlace().get(0);
        assertThat(actualTiamatMunicipality).isNotNull();
        assertThat(actualTiamatMunicipality.getParentTopographicPlaceRef())
                .describedAs("The municipality should have a parent topographic place").isNotNull();
        assertThat(actualTiamatMunicipality.getParentTopographicPlaceRef().getRef()).isEqualTo(county.getId());
    }

    @Test
    public void accessibilityAssesmentIdToNetex() throws Exception {
        AccessibilityAssessment accessibilityAssessment = new AccessibilityAssessment();
        accessibilityAssessment.setNetexId("NSR:AccessibilityAssesment:123124");

        StopPlace stopPlace = new StopPlace();
        stopPlace.setNetexId("NSR:StopPlace:321123");
        stopPlace.setAccessibilityAssessment(accessibilityAssessment);

        org.rutebanken.netex.model.StopPlace netexStopPlace = netexMapper.mapToNetexModel(stopPlace);

        org.rutebanken.netex.model.AccessibilityAssessment netexAccessibilityAssesment = netexStopPlace.getAccessibilityAssessment();

        assertThat(netexAccessibilityAssesment.getId()).isNotEmpty();
        assertThat(netexAccessibilityAssesment.getId()).isEqualToIgnoringCase("NSR:AccessibilityAssesment:123124");
    }
}
