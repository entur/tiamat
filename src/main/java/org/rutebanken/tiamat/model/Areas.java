package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class Areas {

    protected List<Object> flexibleAreaOrFlexibleAreaRefOrHailAndRideArea;

    public List<Object> getFlexibleAreaOrFlexibleAreaRefOrHailAndRideArea() {
        if (flexibleAreaOrFlexibleAreaRefOrHailAndRideArea == null) {
            flexibleAreaOrFlexibleAreaRefOrHailAndRideArea = new ArrayList<Object>();
        }
        return this.flexibleAreaOrFlexibleAreaRefOrHailAndRideArea;
    }

}
