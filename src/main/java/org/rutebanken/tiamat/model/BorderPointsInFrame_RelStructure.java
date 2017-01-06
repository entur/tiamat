package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class BorderPointsInFrame_RelStructure
        extends FrameContainmentStructure {

    protected List<BorderPoint> borderPoint;

    public List<BorderPoint> getBorderPoint() {
        if (borderPoint == null) {
            borderPoint = new ArrayList<BorderPoint>();
        }
        return this.borderPoint;
    }

}
