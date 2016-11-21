

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class CommonSectionSequenceMembers_RelStructure
    extends StrictContainmentAggregationStructure
{

    protected List<CommonSectionSequenceMemberStructure> commonSectionSequenceMember;

    public List<CommonSectionSequenceMemberStructure> getCommonSectionSequenceMember() {
        if (commonSectionSequenceMember == null) {
            commonSectionSequenceMember = new ArrayList<CommonSectionSequenceMemberStructure>();
        }
        return this.commonSectionSequenceMember;
    }

}
