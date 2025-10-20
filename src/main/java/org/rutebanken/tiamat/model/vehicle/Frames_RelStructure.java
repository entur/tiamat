package org.rutebanken.tiamat.model.vehicle;

import jakarta.xml.bind.JAXBElement;
import org.rutebanken.tiamat.model.Common_VersionFrameStructure;
import org.rutebanken.tiamat.model.ContainmentAggregationStructure;

import java.util.ArrayList;
import java.util.List;

public class Frames_RelStructure extends ContainmentAggregationStructure {
    private List<JAXBElement<? extends Common_VersionFrameStructure>> commonFrame;

    public List<JAXBElement<? extends Common_VersionFrameStructure>> getCommonFrame() {
        if (this.commonFrame == null) {
            this.commonFrame = new ArrayList();
        }

        return this.commonFrame;
    }

}