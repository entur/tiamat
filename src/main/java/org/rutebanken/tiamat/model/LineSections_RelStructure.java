package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class LineSections_RelStructure
        extends ContainmentAggregationStructure {

    protected List<Object> lineSectionRefOrLineSection;

    public List<Object> getLineSectionRefOrLineSection() {
        if (lineSectionRefOrLineSection == null) {
            lineSectionRefOrLineSection = new ArrayList<Object>();
        }
        return this.lineSectionRefOrLineSection;
    }

}
