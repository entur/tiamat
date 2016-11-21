

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class Frames_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<Common_VersionFrameStructure> commonFrame;

    public List<Common_VersionFrameStructure> getCommonFrame() {
        if (commonFrame == null) {
            commonFrame = new ArrayList<Common_VersionFrameStructure>();
        }
        return this.commonFrame;
    }

}
