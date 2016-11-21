

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


public class TicketValidatorEquipment_VersionStructure
    extends InstalledEquipment_VersionStructure
{

    protected List<TicketValidatorEnumeration> ticketValidatorType;

    public List<TicketValidatorEnumeration> getTicketValidatorType() {
        if (ticketValidatorType == null) {
            ticketValidatorType = new ArrayList<TicketValidatorEnumeration>();
        }
        return this.ticketValidatorType;
    }

}
