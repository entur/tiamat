package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class NoticeAssignmentViews_RelStructure
        extends ContainmentAggregationStructure {

    protected List<NoticeAssignmentView> noticeAssignmentView;

    public List<NoticeAssignmentView> getNoticeAssignmentView() {
        if (noticeAssignmentView == null) {
            noticeAssignmentView = new ArrayList<NoticeAssignmentView>();
        }
        return this.noticeAssignmentView;
    }

}
