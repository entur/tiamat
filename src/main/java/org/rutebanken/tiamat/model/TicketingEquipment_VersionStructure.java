

package org.rutebanken.tiamat.model;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


    "vehicleModes",
    "ticketMachines",
    "numberOfMachines",
    "heightOfMachineInterface",
    "ticketingFacilityList",
    "ticketingServiceFacilityList",
    "ticketOffice",
    "ticketCounter",
    "numberOfTills",
    "queueManagement",
    "paymentMethods",
    "ticketTypesAvailable",
    "scopeOfTicketsAvailable",
    "lowCounterAccess",
    "heightOfLowCounter",
public class TicketingEquipment_VersionStructure
    extends InstalledEquipment_VersionStructure
{

    protected List<VehicleModeEnumeration> vehicleModes;
    protected Boolean ticketMachines;
    protected BigInteger numberOfMachines;
    protected BigDecimal heightOfMachineInterface;
    protected List<TicketingFacilityEnumeration> ticketingFacilityList;
    protected List<TicketingServiceFacilityEnumeration> ticketingServiceFacilityList;
    protected Boolean ticketOffice;
    protected Boolean ticketCounter;
    protected BigInteger numberOfTills;
    protected QueueManagementEnumeration queueManagement;
    protected List<PaymentMethodEnumeration> paymentMethods;
    protected List<TicketTypeEnumeration> ticketTypesAvailable;
    protected List<TicketingFacilityEnumeration> scopeOfTicketsAvailable;
    protected Boolean lowCounterAccess;
    protected BigDecimal heightOfLowCounter;
    protected Boolean inductionLoops;

    public List<VehicleModeEnumeration> getVehicleModes() {
        if (vehicleModes == null) {
            vehicleModes = new ArrayList<VehicleModeEnumeration>();
        }
        return this.vehicleModes;
    }

    public Boolean isTicketMachines() {
        return ticketMachines;
    }

    public void setTicketMachines(Boolean value) {
        this.ticketMachines = value;
    }

    public BigInteger getNumberOfMachines() {
        return numberOfMachines;
    }

    public void setNumberOfMachines(BigInteger value) {
        this.numberOfMachines = value;
    }

    public BigDecimal getHeightOfMachineInterface() {
        return heightOfMachineInterface;
    }

    public void setHeightOfMachineInterface(BigDecimal value) {
        this.heightOfMachineInterface = value;
    }

    public List<TicketingFacilityEnumeration> getTicketingFacilityList() {
        if (ticketingFacilityList == null) {
            ticketingFacilityList = new ArrayList<TicketingFacilityEnumeration>();
        }
        return this.ticketingFacilityList;
    }

    public List<TicketingServiceFacilityEnumeration> getTicketingServiceFacilityList() {
        if (ticketingServiceFacilityList == null) {
            ticketingServiceFacilityList = new ArrayList<TicketingServiceFacilityEnumeration>();
        }
        return this.ticketingServiceFacilityList;
    }

    public Boolean isTicketOffice() {
        return ticketOffice;
    }

    public void setTicketOffice(Boolean value) {
        this.ticketOffice = value;
    }

    public Boolean isTicketCounter() {
        return ticketCounter;
    }

    public void setTicketCounter(Boolean value) {
        this.ticketCounter = value;
    }

    public BigInteger getNumberOfTills() {
        return numberOfTills;
    }

    public void setNumberOfTills(BigInteger value) {
        this.numberOfTills = value;
    }

    public QueueManagementEnumeration getQueueManagement() {
        return queueManagement;
    }

    public void setQueueManagement(QueueManagementEnumeration value) {
        this.queueManagement = value;
    }

    public List<PaymentMethodEnumeration> getPaymentMethods() {
        if (paymentMethods == null) {
            paymentMethods = new ArrayList<PaymentMethodEnumeration>();
        }
        return this.paymentMethods;
    }

    public List<TicketTypeEnumeration> getTicketTypesAvailable() {
        if (ticketTypesAvailable == null) {
            ticketTypesAvailable = new ArrayList<TicketTypeEnumeration>();
        }
        return this.ticketTypesAvailable;
    }

    public List<TicketingFacilityEnumeration> getScopeOfTicketsAvailable() {
        if (scopeOfTicketsAvailable == null) {
            scopeOfTicketsAvailable = new ArrayList<TicketingFacilityEnumeration>();
        }
        return this.scopeOfTicketsAvailable;
    }

    public Boolean isLowCounterAccess() {
        return lowCounterAccess;
    }

    public void setLowCounterAccess(Boolean value) {
        this.lowCounterAccess = value;
    }

    public BigDecimal getHeightOfLowCounter() {
        return heightOfLowCounter;
    }

    public void setHeightOfLowCounter(BigDecimal value) {
        this.heightOfLowCounter = value;
    }

    public Boolean isInductionLoops() {
        return inductionLoops;
    }

    public void setInductionLoops(Boolean value) {
        this.inductionLoops = value;
    }

}
