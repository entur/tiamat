package no.rutebanken.tiamat.netexmapping;

import no.rutebanken.netex.model.Quay;
import no.rutebanken.tiamat.model.MultilingualString;
import no.rutebanken.tiamat.model.StopPlace;
import no.rutebanken.tiamat.model.StopPlacesInFrame_RelStructure;
import org.junit.Ignore;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class NetexMapperTest {

    private NetexMapper netexMapper = new NetexMapper();

    @Test
    public void mapSiteFrameToNetexModel() throws Exception {
        no.rutebanken.tiamat.model.SiteFrame sourceSiteFrame = new no.rutebanken.tiamat.model.SiteFrame();

        StopPlacesInFrame_RelStructure stopPlacesInFrame_relStructure = new StopPlacesInFrame_RelStructure();

        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new MultilingualString("name", "en", ""));

        stopPlacesInFrame_relStructure.getStopPlace().add(stopPlace);

        sourceSiteFrame.setStopPlaces(stopPlacesInFrame_relStructure);

        no.rutebanken.netex.model.SiteFrame netexSiteFrame = netexMapper.mapToNetexModel(sourceSiteFrame);

        assertThat(netexSiteFrame).isNotNull();
        assertThat(netexSiteFrame.getStopPlaces().getStopPlace().get(0).getName().getValue()).isEqualTo(stopPlace.getName().getValue());
    }


    @Test
    public void mapSiteFrameToInternalModel() throws Exception {
        no.rutebanken.netex.model.SiteFrame netexSiteFrame = new no.rutebanken.netex.model.SiteFrame();

        no.rutebanken.netex.model.StopPlacesInFrame_RelStructure stopPlacesInFrame_relStructure = new no.rutebanken.netex.model.StopPlacesInFrame_RelStructure();

        no.rutebanken.netex.model.StopPlace stopPlace = new no.rutebanken.netex.model.StopPlace();
        no.rutebanken.netex.model.MultilingualString name = new no.rutebanken.netex.model.MultilingualString();
        name.setValue("stop place");
        name.setLang("no");
        name.setTextIdType("");
        stopPlace.setName(name);
        stopPlace.setId("1337");

        stopPlacesInFrame_relStructure.getStopPlace().add(stopPlace);
        netexSiteFrame.setStopPlaces(stopPlacesInFrame_relStructure);

        no.rutebanken.tiamat.model.SiteFrame actualSiteFrame = netexMapper.mapToTiamatModel(netexSiteFrame);

        assertThat(actualSiteFrame).isNotNull();
        assertThat(actualSiteFrame.getStopPlaces().getStopPlace().get(0).getName().getValue()).isEqualTo(stopPlace.getName().getValue());
        assertThat(actualSiteFrame.getStopPlaces().getStopPlace().get(0).getId().toString()).isEqualTo(stopPlace.getId());
    }


    @Test
    public void mapStopPlaceToNetex() throws Exception {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new MultilingualString("name", "en", ""));

        no.rutebanken.netex.model.StopPlace netexStopPlace = netexMapper.mapToNetexModel(stopPlace);

        assertThat(netexStopPlace).isNotNull();
        assertThat(netexStopPlace.getName().getValue()).isEqualTo(stopPlace.getName().getValue());
    }

    @Test
    public void mapStopPlaceToInternalWithId() throws Exception {
        no.rutebanken.netex.model.StopPlace netexStopPlace = new no.rutebanken.netex.model.StopPlace();
        netexStopPlace.setId("1337");

        no.rutebanken.tiamat.model.StopPlace tiamatStopPlace = netexMapper.mapToTiamatModel(netexStopPlace);

        assertThat(tiamatStopPlace).isNotNull();
        assertThat(tiamatStopPlace.getId().toString()).isEqualTo(netexStopPlace.getId());
    }

    @Test
    public void mapStopPlaceToInternalWithName() throws Exception {
        no.rutebanken.netex.model.StopPlace netexStopPlace = new no.rutebanken.netex.model.StopPlace();
        no.rutebanken.netex.model.MultilingualString name = new no.rutebanken.netex.model.MultilingualString();
        name.setValue("stop placec ");
        name.setLang("no");
        name.setTextIdType("");
        netexStopPlace.setName(name);


        no.rutebanken.tiamat.model.StopPlace tiamatStopPlace = netexMapper.mapToTiamatModel(netexStopPlace);

        assertThat(tiamatStopPlace).isNotNull();
        assertThat(tiamatStopPlace.getName().getValue()).isEqualTo(netexStopPlace.getName().getValue());

    }

    @Test
    public void mapStopPlaceInternalIdToNetexId() {
        StopPlace tiamatStopPlace = new StopPlace();
        tiamatStopPlace.setId(123456L);

        no.rutebanken.netex.model.StopPlace netexStopPlace = netexMapper.mapToNetexModel(tiamatStopPlace);

        assertThat(netexStopPlace.getId()).isEqualTo("NSR:StopPlace:123456");
    }

    @Test
    public void mapNetexQuayIdToInternal() {
        Quay netexQuay = new Quay();

        String netexId = "NSR:Quay:12345";
        netexQuay.setId(netexId);

        no.rutebanken.tiamat.model.Quay tiamatQuay = netexMapper.mapToTiamatModel(netexQuay);

        assertThat(tiamatQuay.getId()).isEqualTo(12345L);
    }

    @Test
    public void mapInternatQuayIdToNetex() {

        no.rutebanken.tiamat.model.Quay tiamatQuay = new no.rutebanken.tiamat.model.Quay();
        tiamatQuay.setId(1234567L);

        Quay netexQuay  = netexMapper.mapToNetexModel(tiamatQuay);
        assertThat(netexQuay.getId()).isNotNull();
        assertThat(netexQuay.getId()).isEqualTo("NSR:Quay:"+1234567);

    }
}
