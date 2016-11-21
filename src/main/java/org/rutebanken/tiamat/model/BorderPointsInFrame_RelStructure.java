

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class BorderPointsInFrame_RelStructure
    extends FrameContainmentStructure
{

    protected List<BorderPoint> borderPoint;

    public List<BorderPoint> getBorderPoint() {
        if (borderPoint == null) {
            borderPoint = new ArrayList<BorderPoint>();
        }
        return this.borderPoint;
    }

}
