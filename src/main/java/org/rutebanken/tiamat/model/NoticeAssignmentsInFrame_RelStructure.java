package org.rutebanken.tiamat.model;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.List;


public class NoticeAssignmentsInFrame_RelStructure
        extends ContainmentAggregationStructure {

    protected List<JAXBElement<? extends DataManagedObjectStructure>> noticeAssignment_;

    public List<JAXBElement<? extends DataManagedObjectStructure>> getNoticeAssignment_() {
        if (noticeAssignment_ == null) {
            noticeAssignment_ = new ArrayList<JAXBElement<? extends DataManagedObjectStructure>>();
        }
        return this.noticeAssignment_;
    }

}
