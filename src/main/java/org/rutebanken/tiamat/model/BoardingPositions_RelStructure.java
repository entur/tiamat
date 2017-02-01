package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class BoardingPositions_RelStructure
        extends ContainmentAggregationStructure {

    protected List<Object> boardingPositionRefOrBoardingPosition;

    public List<Object> getBoardingPositionRefOrBoardingPosition() {
        if (boardingPositionRefOrBoardingPosition == null) {
            boardingPositionRefOrBoardingPosition = new ArrayList<Object>();
        }
        return this.boardingPositionRefOrBoardingPosition;
    }

}
