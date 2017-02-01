package org.rutebanken.tiamat.model;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.List;


public class TicketingService_VersionStructure
        extends LocalService_VersionStructure {

    protected List<VehicleModeEnumeration> vehicleModes;
    protected List<TicketingServiceFacilityEnumeration> ticketingServiceList;
    protected List<JAXBElement<List<TicketTypeEnumeration>>> ticketTypeList;
    protected Boolean ticketCounterService;
    protected Boolean onlinePurchaseForCollection;
    protected Boolean onlinePurchaseForETicket;
    protected Boolean onlinePurchaseForSelfPrintTicket;
    protected Boolean mobileDeviceTickets;
    protected List<PaymentMethodEnumeration> paymentMethods;

    public List<VehicleModeEnumeration> getVehicleModes() {
        if (vehicleModes == null) {
            vehicleModes = new ArrayList<VehicleModeEnumeration>();
        }
        return this.vehicleModes;
    }

    public List<TicketingServiceFacilityEnumeration> getTicketingServiceList() {
        if (ticketingServiceList == null) {
            ticketingServiceList = new ArrayList<TicketingServiceFacilityEnumeration>();
        }
        return this.ticketingServiceList;
    }

    public List<JAXBElement<List<TicketTypeEnumeration>>> getTicketTypeList() {
        if (ticketTypeList == null) {
            ticketTypeList = new ArrayList<JAXBElement<List<TicketTypeEnumeration>>>();
        }
        return this.ticketTypeList;
    }

    public Boolean isTicketCounterService() {
        return ticketCounterService;
    }

    public void setTicketCounterService(Boolean value) {
        this.ticketCounterService = value;
    }

    public Boolean isOnlinePurchaseForCollection() {
        return onlinePurchaseForCollection;
    }

    public void setOnlinePurchaseForCollection(Boolean value) {
        this.onlinePurchaseForCollection = value;
    }

    public Boolean isOnlinePurchaseForETicket() {
        return onlinePurchaseForETicket;
    }

    public void setOnlinePurchaseForETicket(Boolean value) {
        this.onlinePurchaseForETicket = value;
    }

    public Boolean isOnlinePurchaseForSelfPrintTicket() {
        return onlinePurchaseForSelfPrintTicket;
    }

    public void setOnlinePurchaseForSelfPrintTicket(Boolean value) {
        this.onlinePurchaseForSelfPrintTicket = value;
    }

    public Boolean isMobileDeviceTickets() {
        return mobileDeviceTickets;
    }

    public void setMobileDeviceTickets(Boolean value) {
        this.mobileDeviceTickets = value;
    }

    public List<PaymentMethodEnumeration> getPaymentMethods() {
        if (paymentMethods == null) {
            paymentMethods = new ArrayList<PaymentMethodEnumeration>();
        }
        return this.paymentMethods;
    }

}
