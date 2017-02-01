package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class Levels_RelStructure
        extends ContainmentAggregationStructure {

    protected List<Object> levelRefOrLevel;

    public List<Object> getLevelRefOrLevel() {
        if (levelRefOrLevel == null) {
            levelRefOrLevel = new ArrayList<Object>();
        }
        return this.levelRefOrLevel;
    }

}
