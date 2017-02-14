package org.rutebanken.tiamat.netex.mapping;

import org.junit.Ignore;
import org.junit.Test;
import org.rutebanken.netex.model.KeyListStructure;
import org.rutebanken.netex.model.KeyValueStructure;
import org.rutebanken.netex.model.PathLinkEndStructure;
import org.rutebanken.tiamat.model.*;
import org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper.ORIGINAL_ID_KEY;
import static org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper.getNetexId;

public class NetexMapperTest {

    private NetexMapper netexMapper = new NetexMapper();

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

        org.rutebanken.netex.model.Quay netexQuay = netexMapper.mapToNetexModel(tiamatQuay);
        assertThat(netexQuay.getId()).isNotNull();
        assertThat(netexQuay.getId()).isEqualTo("NSR:Quay:" + 1234567);
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

        assertThat(actualQuay.getId()).isEqualTo("NSR:Quay:" + 1234567);

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
        assertThat(netexTopographicPlace.getCountryRef()).as("Reference to country shall not be null").isNotNull();
        assertThat(netexTopographicPlace.getCountryRef().getRef()).isEqualTo(org.rutebanken.netex.model.IanaCountryTldEnumeration.ZM);


    }

    @Test
    public void mapPathLinkToNetex() {
        Quay quay = new Quay();
        quay.setId(10L);

        StopPlace stopPlace = new StopPlace();
        stopPlace.setId(11L);

        PathLink pathLink = new PathLink(new PathLinkEnd(quay), new PathLinkEnd(stopPlace));
        pathLink.setId(123L);

        org.rutebanken.netex.model.PathLink netexPathLink = netexMapper.mapToNetexModel(pathLink);

        assertThat(netexPathLink).describedAs("Mapped path link shall not be null").isNotNull();
        assertThat(netexPathLink.getId()).isEqualTo(getNetexId(pathLink, pathLink.getId()));
        verifyPathLinkEnd(netexPathLink.getFrom(), quay.getId(), quay, "PathlinkEnd from");
        verifyPathLinkEnd(netexPathLink.getTo(), stopPlace.getId(), stopPlace, "PathLinkEnd to");

    }

    private void verifyPathLinkEnd(PathLinkEndStructure pathLinkEndStructure, long entityId, EntityStructure entityStructure, String describedAs) {
        assertThat(pathLinkEndStructure).describedAs(describedAs).isNotNull();
        assertThat(pathLinkEndStructure.getPlaceRef()).describedAs(describedAs).isNotNull();
        assertThat(pathLinkEndStructure.getPlaceRef().getRef()).isEqualTo(NetexIdMapper.getNetexId(entityStructure, entityId));
    }

}
