package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class NoticesInFrame_RelStructure
        extends ContainmentAggregationStructure {

    protected List<Notice> notice;

    public List<Notice> getNotice() {
        if (notice == null) {
            notice = new ArrayList<Notice>();
        }
        return this.notice;
    }

}
