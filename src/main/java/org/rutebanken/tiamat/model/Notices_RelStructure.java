package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class Notices_RelStructure
        extends ContainmentAggregationStructure {

    protected List<Object> noticeRefOrNotice;

    public List<Object> getNoticeRefOrNotice() {
        if (noticeRefOrNotice == null) {
            noticeRefOrNotice = new ArrayList<Object>();
        }
        return this.noticeRefOrNotice;
    }

}
