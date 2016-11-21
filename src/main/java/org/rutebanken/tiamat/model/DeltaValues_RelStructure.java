package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class DeltaValues_RelStructure {

    protected List<DeltaValueStructure> deltaValue;

    public List<DeltaValueStructure> getDeltaValue() {
        if (deltaValue == null) {
            deltaValue = new ArrayList<DeltaValueStructure>();
        }
        return this.deltaValue;
    }

}
