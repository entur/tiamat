package org.rutebanken.tiamat.model;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.List;


public class NoticeAssignments_RelStructure
        extends ContainmentAggregationStructure {

    protected List<JAXBElement<?>> noticeAssignment_OrNoticeAssignmentView;

    public List<JAXBElement<?>> getNoticeAssignment_OrNoticeAssignmentView() {
        if (noticeAssignment_OrNoticeAssignmentView == null) {
            noticeAssignment_OrNoticeAssignmentView = new ArrayList<JAXBElement<?>>();
        }
        return this.noticeAssignment_OrNoticeAssignmentView;
    }

}
