package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class TicketValidatorEquipment_VersionStructure
        extends InstalledEquipment_VersionStructure {

    protected List<TicketValidatorEnumeration> ticketValidatorType;

    public List<TicketValidatorEnumeration> getTicketValidatorType() {
        if (ticketValidatorType == null) {
            ticketValidatorType = new ArrayList<TicketValidatorEnumeration>();
        }
        return this.ticketValidatorType;
    }

}
