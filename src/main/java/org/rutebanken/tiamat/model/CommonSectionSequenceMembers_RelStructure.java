package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class CommonSectionSequenceMembers_RelStructure
        extends StrictContainmentAggregationStructure {

    protected List<CommonSectionSequenceMemberStructure> commonSectionSequenceMember;

    public List<CommonSectionSequenceMemberStructure> getCommonSectionSequenceMember() {
        if (commonSectionSequenceMember == null) {
            commonSectionSequenceMember = new ArrayList<CommonSectionSequenceMemberStructure>();
        }
        return this.commonSectionSequenceMember;
    }

}
