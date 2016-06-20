package no.rutebanken.tiamat.netexmapping;

import no.rutebanken.tiamat.model.MultilingualString;
import no.rutebanken.tiamat.model.StopPlace;
import no.rutebanken.tiamat.model.StopPlacesInFrame_RelStructure;
import org.junit.Ignore;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Ignore
public class NetexMapperTest {

    @Test
    public void mapToNetexModel() throws Exception {
        no.rutebanken.tiamat.model.SiteFrame sourceSiteFrame = new no.rutebanken.tiamat.model.SiteFrame();

        StopPlacesInFrame_RelStructure stopPlacesInFrame_relStructure = new StopPlacesInFrame_RelStructure();

        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new MultilingualString("name", "en", ""));

        stopPlacesInFrame_relStructure.getStopPlace().add(stopPlace);

        sourceSiteFrame.setStopPlaces(stopPlacesInFrame_relStructure);

        no.rutebanken.netex.model.SiteFrame netexSiteFrame = new NetexMapper().mapToNetexModel(sourceSiteFrame);

        assertThat(netexSiteFrame).isNotNull();
        assertThat(netexSiteFrame.getStopPlaces().getStopPlace().get(0).getName().getValue()).isEqualTo(stopPlace.getName().getValue());
    }


    @Test
    public void mapToInternalModel() throws Exception {
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

        no.rutebanken.tiamat.model.SiteFrame actualSiteFrame = new NetexMapper().mapToTiamatModel(netexSiteFrame);

        assertThat(actualSiteFrame).isNotNull();
        assertThat(actualSiteFrame.getStopPlaces().getStopPlace().get(0).getName().getValue()).isEqualTo(stopPlace.getName().getValue());
        assertThat(actualSiteFrame.getStopPlaces().getStopPlace().get(0).getId()).isEqualTo(stopPlace.getId());
    }
}