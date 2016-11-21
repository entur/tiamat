package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class Frames_RelStructure
        extends ContainmentAggregationStructure {

    protected List<Common_VersionFrameStructure> commonFrame;

    public List<Common_VersionFrameStructure> getCommonFrame() {
        if (commonFrame == null) {
            commonFrame = new ArrayList<Common_VersionFrameStructure>();
        }
        return this.commonFrame;
    }

}
