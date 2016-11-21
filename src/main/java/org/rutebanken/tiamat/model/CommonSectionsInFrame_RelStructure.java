package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class CommonSectionsInFrame_RelStructure
        extends ContainmentAggregationStructure {

    protected List<CommonSection> commonSection;

    public List<CommonSection> getCommonSection() {
        if (commonSection == null) {
            commonSection = new ArrayList<CommonSection>();
        }
        return this.commonSection;
    }

}
