

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


public class CommonSectionPointMembers_RelStructure
    extends StrictContainmentAggregationStructure
{

    protected List<JAXBElement<? extends CommonSectionPointMember_VersionedChildStructure>> commonSectionPointMember;

    public List<JAXBElement<? extends CommonSectionPointMember_VersionedChildStructure>> getCommonSectionPointMember() {
        if (commonSectionPointMember == null) {
            commonSectionPointMember = new ArrayList<JAXBElement<? extends CommonSectionPointMember_VersionedChildStructure>>();
        }
        return this.commonSectionPointMember;
    }

}
