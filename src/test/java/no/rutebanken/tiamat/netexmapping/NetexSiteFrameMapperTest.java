package no.rutebanken.tiamat.netexmapping;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import no.rutebanken.netex.model.*;
import no.rutebanken.tiamat.model.AccessSpace;
import no.rutebanken.tiamat.model.MultilingualString;
import no.rutebanken.tiamat.model.SiteFrame;
import no.rutebanken.tiamat.model.StopPlace;
import no.rutebanken.tiamat.model.StopPlacesInFrame_RelStructure;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;

@Ignore
public class NetexSiteFrameMapperTest {

    @Test
    public void map() throws Exception {
        SiteFrame sourceSiteFrame = new SiteFrame();

        StopPlacesInFrame_RelStructure stopPlacesInFrame_relStructure = new StopPlacesInFrame_RelStructure();

        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new MultilingualString("name", "en", ""));

        stopPlacesInFrame_relStructure.getStopPlace().add(stopPlace);

        sourceSiteFrame.setStopPlaces(stopPlacesInFrame_relStructure);

        no.rutebanken.netex.model.SiteFrame netexSiteFrame = new NetexSiteFrameMapper().map(sourceSiteFrame);

    }


    @Test
    public void mapAccessSpace() {
        AccessSpace accessSpace = new AccessSpace();
        accessSpace.setName(new MultilingualString("Name", "en", ""));


        ArrayList<AccessSpace> accessSpaces = new ArrayList<>();
        accessSpaces.add(accessSpace);
        AccessSpaces_RelStructure relStructure = new NetexSiteFrameMapper().mapAccessSpaceList(accessSpaces);


    }
}