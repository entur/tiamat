package org.rutebanken.tiamat.model;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.List;


public class CommonSectionPointMembers_RelStructure
        extends StrictContainmentAggregationStructure {

    protected List<JAXBElement<? extends CommonSectionPointMember_VersionedChildStructure>> commonSectionPointMember;

    public List<JAXBElement<? extends CommonSectionPointMember_VersionedChildStructure>> getCommonSectionPointMember() {
        if (commonSectionPointMember == null) {
            commonSectionPointMember = new ArrayList<JAXBElement<? extends CommonSectionPointMember_VersionedChildStructure>>();
        }
        return this.commonSectionPointMember;
    }

}
