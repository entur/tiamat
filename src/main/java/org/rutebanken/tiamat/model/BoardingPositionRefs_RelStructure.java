package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class BoardingPositionRefs_RelStructure
        extends OneToManyRelationshipStructure {

    protected List<BoardingPositionRefStructure> boardingPositionRef;

    public List<BoardingPositionRefStructure> getBoardingPositionRef() {
        if (boardingPositionRef == null) {
            boardingPositionRef = new ArrayList<BoardingPositionRefStructure>();
        }
        return this.boardingPositionRef;
    }

}
