package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class ModeRefs_RelStructure
        extends OneToManyRelationshipStructure {

    protected List<ModeRefStructure> modeRef;

    public List<ModeRefStructure> getModeRef() {
        if (modeRef == null) {
            modeRef = new ArrayList<ModeRefStructure>();
        }
        return this.modeRef;
    }

}
