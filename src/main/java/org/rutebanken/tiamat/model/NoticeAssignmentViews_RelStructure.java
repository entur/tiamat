

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class NoticeAssignmentViews_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<NoticeAssignmentView> noticeAssignmentView;

    public List<NoticeAssignmentView> getNoticeAssignmentView() {
        if (noticeAssignmentView == null) {
            noticeAssignmentView = new ArrayList<NoticeAssignmentView>();
        }
        return this.noticeAssignmentView;
    }

}
