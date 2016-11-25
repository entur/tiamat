package org.rutebanken.tiamat.netexmapping;

import org.junit.Ignore;
import org.rutebanken.tiamat.model.*;
import org.junit.Test;
import org.rutebanken.tiamat.model.CountryRef;
import org.rutebanken.tiamat.model.IanaCountryTldEnumeration;
import org.rutebanken.tiamat.model.MultilingualStringEntity;
import org.rutebanken.tiamat.model.SiteFrame;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.StopPlacesInFrame_RelStructure;
import org.rutebanken.tiamat.model.TopographicPlace;

import static org.assertj.core.api.Assertions.assertThat;

public class NetexMapperTest {

    private NetexMapper netexMapper = new NetexMapper();

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
    public void mapStopPlaceToNetex() throws Exception {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new EmbeddableMultilingualString("name", "en"));

        org.rutebanken.netex.model.StopPlace netexStopPlace = netexMapper.mapToNetexModel(stopPlace);

        assertThat(netexStopPlace).isNotNull();
        assertThat(netexStopPlace.getName()).isNotNull();
        assertThat(netexStopPlace.getName().getValue()).isEqualTo(stopPlace.getName().getValue());
    }

    @Ignore
    @Test
    public void mapStopPlaceToInternalWithId() throws Exception {
        org.rutebanken.netex.model.StopPlace netexStopPlace = new org.rutebanken.netex.model.StopPlace();
        String stopPlaceId = "1339";
        netexStopPlace.setId("NSR:StopPlace:" + stopPlaceId);

        org.rutebanken.tiamat.model.StopPlace tiamatStopPlace = netexMapper.mapToTiamatModel(netexStopPlace);

        assertThat(tiamatStopPlace).isNotNull();
        assertThat(tiamatStopPlace.getId().toString()).isEqualTo(stopPlaceId);
    }

    @Test
    public void mapStopPlaceWithKeyValuesToNetex() throws Exception {

        StopPlace stopPlace = new StopPlace();

        String originalId = "OPP:StopArea:123";


        stopPlace.getKeyValues().put(NetexIdMapper.ORIGINAL_ID_KEY, new Value(originalId));

        org.rutebanken.netex.model.StopPlace netexStopPlace = netexMapper.mapToNetexModel(stopPlace);
        assertThat(netexStopPlace.getKeyList()).isNotNull();
        assertThat(netexStopPlace.getKeyList().getKeyValue()).isNotNull();
        assertThat(netexStopPlace.getKeyList().getKeyValue()).isNotEmpty();
        assertThat(netexStopPlace.getKeyList().getKeyValue()).extracting("key").contains(NetexIdMapper.ORIGINAL_ID_KEY);
        assertThat(netexStopPlace.getKeyList().getKeyValue()).extracting("value").contains(originalId);
    }

    @Test
    public void mapStopPlaceWithKeyValuesToTiamat() throws Exception {

        org.rutebanken.netex.model.StopPlace netexStopPlace = new org.rutebanken.netex.model.StopPlace();
        netexStopPlace.setKeyList(new org.rutebanken.netex.model.KeyListStructure());

        String originalId = "OPP:StopArea:123";
        org.rutebanken.netex.model.KeyValueStructure keyValueStructure = new org.rutebanken.netex.model.KeyValueStructure()
                .withKey(NetexIdMapper.ORIGINAL_ID_KEY).withValue(originalId);

        netexStopPlace.getKeyList().getKeyValue().add(keyValueStructure);

        StopPlace tiamatStopPlace = netexMapper.mapToTiamatModel(netexStopPlace);
        assertThat(tiamatStopPlace.getKeyValues()).isNotNull();
        assertThat(tiamatStopPlace.getKeyValues()).containsKey(NetexIdMapper.ORIGINAL_ID_KEY);
        assertThat(tiamatStopPlace.getKeyValues().get(NetexIdMapper.ORIGINAL_ID_KEY).getItems().contains(originalId));
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
        tiamatStopPlace.setId(123456L);

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

        assertThat(tiamatQuay.getId()).isEqualTo(12345L);
    }

    @Test
    public void mapInternalQuayIdToNetex() {

        org.rutebanken.tiamat.model.Quay tiamatQuay = new org.rutebanken.tiamat.model.Quay();
        tiamatQuay.setId(1234567L);

        org.rutebanken.netex.model.Quay netexQuay  = netexMapper.mapToNetexModel(tiamatQuay);
        assertThat(netexQuay.getId()).isNotNull();
        assertThat(netexQuay.getId()).isEqualTo("NSR:Quay:"+1234567);
    }

    @Test
    public void mapStopPlaceWithQuayToNetex() {
        org.rutebanken.tiamat.model.StopPlace stopPlace = new StopPlace();

        org.rutebanken.tiamat.model.Quay tiamatQuay = new org.rutebanken.tiamat.model.Quay();
        tiamatQuay.setId(1234567L);

        stopPlace.getQuays().add(tiamatQuay);

        org.rutebanken.netex.model.StopPlace actualStop = netexMapper.mapToNetexModel(stopPlace);

        org.rutebanken.netex.model.Quay actualQuay = actualStop.getQuays().getQuayRefOrQuay().stream()
                .filter(object -> object instanceof org.rutebanken.netex.model.Quay)
                .map(object -> ((org.rutebanken.netex.model.Quay) object))
                .findFirst()
                .get();

        assertThat(actualQuay.getId()).isEqualTo("NSR:Quay:"+1234567);

    }

    @Test
    public void countryRefMappedToNetex() {
        SiteFrame tiamatSiteFrame = new SiteFrame();
        TopographicPlace topographicPlace = new TopographicPlace();

        topographicPlace.setId(1L);
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
        assertThat(netexTopographicPlace.getCountryRef()).isNotNull().as("Reference to country shall not be null");
        assertThat(netexTopographicPlace.getCountryRef().getRef()).isEqualTo(org.rutebanken.netex.model.IanaCountryTldEnumeration.ZM);



    }

}
