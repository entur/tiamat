

package org.rutebanken.tiamat.model;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


    "numberOfServers",
    "railedQueue",
public class QueueingEquipment_VersionStructure
    extends AccessEquipment_VersionStructure
{

    protected BigInteger numberOfServers;
    protected Boolean railedQueue;
    protected Boolean ticketedQueue;

    public BigInteger getNumberOfServers() {
        return numberOfServers;
    }

    public void setNumberOfServers(BigInteger value) {
        this.numberOfServers = value;
    }

    public Boolean isRailedQueue() {
        return railedQueue;
    }

    public void setRailedQueue(Boolean value) {
        this.railedQueue = value;
    }

    public Boolean isTicketedQueue() {
        return ticketedQueue;
    }

    public void setTicketedQueue(Boolean value) {
        this.ticketedQueue = value;
    }

}
