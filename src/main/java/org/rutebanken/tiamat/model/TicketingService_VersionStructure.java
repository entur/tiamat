/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

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
