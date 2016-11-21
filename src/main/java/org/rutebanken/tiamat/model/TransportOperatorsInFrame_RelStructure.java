

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


public class TransportOperatorsInFrame_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<Organisation_VersionStructure> authorityOrOperator;

    public List<Organisation_VersionStructure> getAuthorityOrOperator() {
        if (authorityOrOperator == null) {
            authorityOrOperator = new ArrayList<Organisation_VersionStructure>();
        }
        return this.authorityOrOperator;
    }

}
