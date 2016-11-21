

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class StopPlaces {

    protected List<StopPlace> stopPlaces;

    public List<StopPlace> getStopPlaces() {
        if (stopPlaces == null) {
            stopPlaces = new ArrayList<>();
        }
        return this.stopPlaces;
    }

}
